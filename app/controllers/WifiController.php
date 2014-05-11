<?php
/**
 * Created by PhpStorm.
 * User: songrenchu
 * Date: 23/2/14
 * Time: 5:03 PM
 */

require_once(dirname(__FILE__) . '/../util/Util.php');

class WifiController extends RController {

	public function actionIndex() {
		echo json_encode(array("response" => "Hello tMap!"));
	}

	/**
	 * TODO Check auth before invoke actions
	 */
//	public function beforeAction() {
//
//	}

	/**
	 * Add a new wifi sample point to database
	 */
	public function actionUploadWifi($roomId, $x, $y, $z) {
		if (Rays::isPost()) {
			$wifiData = json_decode(Rays::getParam("wifiData", "[]"));
			if (!count($wifiData)) {
				throw new RException("wifi empty error");
			}

			$wifiPoint = WifiFingerprint::getWifiFingerprintPoint($roomId, $x, $y, $z);
			if ($wifiPoint === null) {
				$wifiPoint = new WifiFingerprint();
				$wifiPoint->roomId = $roomId;
				$wifiPoint->x = $x;
				$wifiPoint->y = $y;
				$wifiPoint->z = $z;
				$wifiPoint->wifiData = array($wifiData);
				$wifiPoint->pack();
			} else {
				$wifiPoint->unpack();
				array_push($wifiPoint->wifiData, $wifiData);
				$wifiPoint->pack();
			}
			$wifiPoint->save();
			echo json_encode(array("response" => "ok"));
		} else {
			throw new RException("no wifi data received");
		}
	}

	/**
	 * Filter out temperate aps and get distribution
	 */
	public function actionWifiRssiDistribution($roomId, $threshold) {
		// delete the old distribution
		$oldDistribution = WifiRssiDistribution::find("roomId", $roomId)->all();
		foreach ($oldDistribution as $item) {
			$item->delete();
		}

		// get data
		$wifiFingerPrint = WifiFingerprint::find("roomId", $roomId)->all();

		foreach ($wifiFingerPrint as $point) {
			// count probability of each ap
			$apList = array();
			$point->unpack();
			foreach ($point->wifiData as $wifiScanItem) {
				foreach ($wifiScanItem as $bssidrssiPair) {
					if (!isset($apList[$bssidrssiPair->bssid])) {
						$apList[$bssidrssiPair->bssid] = 1;
					} else {
						++$apList[$bssidrssiPair->bssid];
					}
				}
			}
			// get usable ap list, filter out temperate aps
			foreach ($apList as $key => $value) {
				$apList[$key] /= count($point->wifiData);
				if ($apList[$key] < $threshold) {
					unset($apList[$key]);
				}
			}
			// count wifi distribution
			foreach ($apList as $key => $value) {
				$apList[$key] = array();
				for ($i = 0; $i > -120; $i--) {
					$apList[$key][$i] = 0;
				}
				$apList[$key][-120] = count($point->wifiData);
			}

			foreach ($point->wifiData as $wifiScanItem) {
				foreach ($wifiScanItem as $bssidrssiPair) {
					if (isset($apList[$bssidrssiPair->bssid])) {
						$apList[$bssidrssiPair->bssid][WifiRssiDistribution::standardRssi($bssidrssiPair->rssi)]++;
						$apList[$bssidrssiPair->bssid][-120]--;
					}
				}
			}

			foreach ($apList as $key => $value) {
				for ($i = 0; $i >= -120; $i--) {
					$apList[$key][$i] /= count($point->wifiData);
				}

				$tmpList = array();
				// smoothing
				for ($times = 0; $times < 2; ++$times) {
					$tmpList[0] = 0;

					for ($i = -1; $i >= -115; $i--) {
						$tmpList[$i] = ($apList[$key][$i + 1] + $apList[$key][$i] + $apList[$key][$i - 1]) / 3;
					}
					for ($i = 0; $i >= -115; $i--) {
						$apList[$key][$i] = $tmpList[$i];
					}
				}

				$distribution = new WifiRssiDistribution();
				$distribution->roomId = $roomId;
				$distribution->x = $point->x;
				$distribution->y = $point->y;
				$distribution->z = $point->z;
				$distribution->bssid = $key;
				$distribution->distribution = $apList[$key];
				$distribution->pack();
				$distribution->save();
			}
		}
		echo json_encode(array("response" => "ok"));
	}

	/**
	 * Get wifi AP list of a room
	 */
	public function actionRoomGetApList($roomId) {
		if (Room::get($roomId) === null) {
			throw new RException("Room not exist");
		}

		$roomApList = RoomApList::find("roomId", $roomId)->first();
		if ($roomApList === null) {
			$roomApList = new RoomApList();
			$roomApList->roomId = $roomId;
		}
		$roomApList->apList = array();

		// First method: Get AP list from original wifi fingerprint
		/*
		$wifiFingerPrint = WifiFingerprint::find("roomId", $roomId)->all();
		foreach ($wifiFingerPrint as $point) {
			$point->unpack();
			foreach ($point->wifiData as $wifiScanItem) {
				foreach ($wifiScanItem as $bssidrssiPair) {
					array_push($roomApList->apList, $bssidrssiPair->bssid);
				}
			}
		}*/

		// Second method: Get AP from distribution after filtering out temperate aps
		$distribution = WifiRssiDistribution::find("roomId", $roomId)->all();
		foreach ($distribution as $item) {
			array_push($roomApList->apList, $item->bssid);
		}

		$roomApList->apList = array_unique($roomApList->apList);
		$roomApList->pack();
		$roomApList->save();
		echo json_encode(array("response" => "ok"));
	}

	/**
	 * Get wifi AP list of a RP
	 */
	public function actionRpGetApList($roomId) {
		if (Room::get($roomId) === null) {
			throw new RException("Room not exist");
		}

		$roomRpList = RoomRpList::find("roomId", $roomId)->first();
		if (Room::get($roomId) === null) {
			throw new RException("Room rp list not created");
		}
		$roomRpList->unpack();
		foreach ($roomRpList->rpList as $rp) {
			$rpApList = RpApList::getRpApList($roomId, $rp->x, $rp->y, $rp->z);
			if ($rpApList === null) {
				$rpApList = new RpApList();
				$rpApList->roomId = $roomId;
				$rpApList->x = $rp->x;
				$rpApList->y = $rp->y;
				$rpApList->z = $rp->z;
			}
			$rpApList->apList = array();

			$distribution = WifiRssiDistribution::getWifiRssiDistribution($roomId, $rp->x, $rp->y, $rp->z);
			foreach ($distribution as $item) {
				array_push($rpApList->apList, $item->bssid);
			}

			$rpApList->apList = array_unique($rpApList->apList);
			$rpApList->pack();
			$rpApList->save();
		}

		echo json_encode(array("response" => "ok"));
	}

	/**
	 * Wi-Fi locating algorithm
	 */
	public function actionWifiLocating() {
		if (!Rays::isPost()) {

			/*$wifiData = json_decode(Rays::getParam("wifiData", "[]"));
			if (!count($wifiData)) {
				throw new RException("wifi empty error");
			}*/

			// TODO debug model, hard code wifi data

			//$wifiData = json_decode('[{"bssid":"80:56:f2:ea:2f:df","rssi":-59},{"bssid":"d0:57:4c:cb:8d:76","rssi":-85},{"bssid":"5c:63:bf:3f:12:f4","rssi":-79},{"bssid":"e0:05:c5:ba:99:bc","rssi":-85},{"bssid":"d0:57:4c:cb:7c:06","rssi":-81},{"bssid":"dc:7b:94:34:86:e0","rssi":-72},{"bssid":"dc:7b:94:34:aa:40","rssi":-74},{"bssid":"dc:7b:94:34:86:ef","rssi":-89},{"bssid":"dc:7b:94:34:aa:4f","rssi":-85},{"bssid":"d0:57:4c:cb:8e:10","rssi":-79},{"bssid":"d0:57:4c:cb:7c:00","rssi":-88},{"bssid":"d0:57:4c:cb:7c:02","rssi":-81},{"bssid":"dc:7b:94:34:aa:43","rssi":-74},{"bssid":"d0:57:4c:cb:8e:16","rssi":-80},{"bssid":"d0:57:4c:cb:7c:03","rssi":-82},{"bssid":"dc:7b:94:34:86:e9","rssi":-89},{"bssid":"d0:57:4c:cb:8d:73","rssi":-86},{"bssid":"40:16:9f:a6:b2:66","rssi":-86},{"bssid":"dc:7b:94:34:35:83","rssi":-87},{"bssid":"dc:7b:94:35:a4:56","rssi":-90},{"bssid":"14:cf:92:e0:7f:b0","rssi":-92},{"bssid":"00:24:01:93:34:dc","rssi":-93},{"bssid":"d0:57:4c:cb:8e:12","rssi":-79},{"bssid":"dc:7b:94:34:aa:4d","rssi":-85},{"bssid":"00:15:e9:e0:2b:bf","rssi":-83},{"bssid":"dc:7b:94:34:86:ed","rssi":-89},{"bssid":"dc:7b:94:35:9e:e0","rssi":-89},{"bssid":"d0:57:4c:cb:bc:00","rssi":-89},{"bssid":"dc:7b:94:35:a4:50","rssi":-89},{"bssid":"dc:7b:94:35:9e:e2","rssi":-91},{"bssid":"dc:7b:94:34:35:80","rssi":-92}]');
			//$wifiData = json_decode('[{"bssid":"80:56:f2:ea:2f:df","rssi":-46},{"bssid":"dc:7b:94:34:86:e3","rssi":-83},{"bssid":"5c:63:bf:3f:12:f4","rssi":-85},{"bssid":"dc:7b:94:34:aa:46","rssi":-71},{"bssid":"d0:57:4c:cb:7c:0c","rssi":-90},{"bssid":"d0:57:4c:cb:8d:7c","rssi":-80},{"bssid":"d0:57:4c:cb:bc:0c","rssi":-84},{"bssid":"d0:57:4c:cb:8e:13","rssi":-85},{"bssid":"dc:7b:94:34:86:e0","rssi":-83},{"bssid":"d0:57:4c:cb:7c:0f","rssi":-90},{"bssid":"dc:7b:94:34:aa:40","rssi":-72},{"bssid":"dc:7b:94:34:86:ef","rssi":-86},{"bssid":"d0:57:4c:cb:8e:10","rssi":-80},{"bssid":"d0:57:4c:cb:bc:06","rssi":-85},{"bssid":"d0:57:4c:cb:8d:76","rssi":-85},{"bssid":"d0:57:4c:cb:bc:03","rssi":-85},{"bssid":"e0:05:c5:ba:99:bc","rssi":-86},{"bssid":"dc:7b:94:35:9e:e3","rssi":-86},{"bssid":"d0:57:4c:cb:7c:06","rssi":-90},{"bssid":"d0:57:4c:cb:8d:7f","rssi":-79},{"bssid":"d0:57:4c:cb:bc:0f","rssi":-83},{"bssid":"dc:7b:94:35:9e:e0","rssi":-85},{"bssid":"d0:57:4c:cb:8d:72","rssi":-86},{"bssid":"d0:57:4c:cb:8d:70","rssi":-87},{"bssid":"d0:57:4c:cb:7c:00","rssi":-88}]');


			// 3.6 3.2 1.6
			$wifiData = json_decode('[{"bssid":"80:56:f2:ea:2f:df","rssi":-67},{"bssid":"dc:7b:94:34:aa:40","rssi":-81},{"bssid":"d0:57:4c:cb:8e:10","rssi":-91},{"bssid":"dc:7b:94:35:9e:e0","rssi":-87},{"bssid":"dc:7b:94:34:86:e0","rssi":-71},{"bssid":"e0:05:c5:ba:99:bc","rssi":-85},{"bssid":"dc:7b:94:34:86:e6","rssi":-71},{"bssid":"5c:63:bf:3f:12:f4","rssi":-72},{"bssid":"dc:7b:94:34:aa:43","rssi":-74},{"bssid":"d0:57:4c:cb:7c:03","rssi":-77},{"bssid":"40:16:9f:a6:b2:66","rssi":-83},{"bssid":"dc:7b:94:35:be:a3","rssi":-87},{"bssid":"d0:57:4c:cb:bc:06","rssi":-88},{"bssid":"c8:d7:19:2e:53:4b","rssi":-90},{"bssid":"d0:57:4c:cb:7c:00","rssi":-78},{"bssid":"00:15:e9:e0:2b:bf","rssi":-82},{"bssid":"d0:57:4c:cb:8e:12","rssi":-86},{"bssid":"dc:7b:94:34:86:e3","rssi":-78},{"bssid":"dc:7b:94:34:aa:46","rssi":-80},{"bssid":"d0:57:4c:cb:bc:0c","rssi":-89},{"bssid":"d0:57:4c:ca:6b:23","rssi":-86},{"bssid":"dc:7b:94:34:aa:42","rssi":-75},{"bssid":"dc:7b:94:34:86:e2","rssi":-76},{"bssid":"d0:57:4c:ca:6b:20","rssi":-87},{"bssid":"dc:7b:94:35:be:a2","rssi":-90}]');

			// 3.6 6.4 1
			//$wifiData = json_decode('[{"bssid":"80:56:f2:ea:2f:df","rssi":-42},{"bssid":"dc:7b:94:34:86:e6","rssi":-68},{"bssid":"d0:57:4c:cb:8e:13","rssi":-70},{"bssid":"dc:7b:94:34:aa:49","rssi":-83},{"bssid":"5c:63:bf:3f:12:f4","rssi":-82},{"bssid":"dc:7b:94:34:aa:43","rssi":-82},{"bssid":"dc:7b:94:35:9e:e3","rssi":-82},{"bssid":"d0:57:4c:cb:bc:0c","rssi":-88},{"bssid":"d0:57:4c:cb:7c:0c","rssi":-88},{"bssid":"d0:57:4c:ca:6b:23","rssi":-87},{"bssid":"5e:85:56:8d:56:a2","rssi":-90},{"bssid":"dc:7b:94:34:86:e3","rssi":-91},{"bssid":"00:24:01:93:34:dc","rssi":-92},{"bssid":"dc:7b:94:34:86:e0","rssi":-66},{"bssid":"d0:57:4c:cb:8d:72","rssi":-69},{"bssid":"d0:57:4c:cb:8d:70","rssi":-70},{"bssid":"d0:57:4c:cb:8e:10","rssi":-70},{"bssid":"dc:7b:94:34:86:ef","rssi":-75},{"bssid":"dc:7b:94:34:a9:00","rssi":-81},{"bssid":"dc:7b:94:35:9e:e0","rssi":-81},{"bssid":"dc:7b:94:34:aa:40","rssi":-81},{"bssid":"d0:57:4c:ca:6b:20","rssi":-86},{"bssid":"d0:57:4c:cb:9e:1f","rssi":-91},{"bssid":"dc:7b:94:35:c8:a2","rssi":-87}]');

			// 3.4 6 0.8
			//$wifiData = json_decode('[{"bssid":"80:56:f2:ea:2f:df","rssi":-22},{"bssid":"dc:7b:94:34:aa:49","rssi":-82},{"bssid":"dc:7b:94:34:aa:43","rssi":-78},{"bssid":"d0:57:4c:cb:7c:0c","rssi":-89},{"bssid":"00:24:01:93:34:dc","rssi":-92},{"bssid":"dc:7b:94:34:86:e0","rssi":-75},{"bssid":"d0:57:4c:cb:8d:72","rssi":-78},{"bssid":"d0:57:4c:cb:8d:70","rssi":-78},{"bssid":"d0:57:4c:cb:8e:10","rssi":-74},{"bssid":"dc:7b:94:34:86:ef","rssi":-89},{"bssid":"dc:7b:94:34:aa:40","rssi":-69},{"bssid":"d0:57:4c:cb:8d:7c","rssi":-78},{"bssid":"dc:7b:94:34:aa:4c","rssi":-82},{"bssid":"d0:57:4c:cb:8d:76","rssi":-88},{"bssid":"d0:57:4c:ca:6b:26","rssi":-88},{"bssid":"dc:7b:94:35:9e:46","rssi":-89},{"bssid":"d0:57:4c:cb:8d:7f","rssi":-78},{"bssid":"dc:7b:94:34:aa:4f","rssi":-82},{"bssid":"1a:cf:e9:17:1b:9f","rssi":-89},{"bssid":"d0:57:4c:ca:6b:2d","rssi":-90},{"bssid":"d0:57:4c:cb:7c:02","rssi":-87},{"bssid":"dc:7b:94:35:a4:52","rssi":-88}]');

			// 0 0 1.3
			//$wifiData = json_decode('[{"bssid":"80:56:f2:ea:2f:df","rssi":-54},{"bssid":"dc:7b:94:34:aa:43","rssi":-73},{"bssid":"d0:57:4c:cb:7c:0c","rssi":-82},{"bssid":"dc:7b:94:34:86:e0","rssi":-74},{"bssid":"dc:7b:94:34:86:ef","rssi":-86},{"bssid":"dc:7b:94:34:aa:40","rssi":-74},{"bssid":"dc:7b:94:35:9e:46","rssi":-85},{"bssid":"d0:57:4c:cb:7c:02","rssi":-78},{"bssid":"5c:63:bf:3f:12:f4","rssi":-61},{"bssid":"dc:7b:94:34:86:e3","rssi":-74},{"bssid":"40:16:9f:a6:b2:66","rssi":-76},{"bssid":"e0:05:c5:ba:99:bc","rssi":-78},{"bssid":"d0:57:4c:cb:bc:09","rssi":-84},{"bssid":"d0:57:4c:cb:bc:0c","rssi":-84},{"bssid":"dc:7b:94:34:86:ec","rssi":-87},{"bssid":"d0:57:4c:ca:e2:53","rssi":-84},{"bssid":"d0:57:4c:cb:bc:06","rssi":-85},{"bssid":"d0:57:4c:cb:7c:0f","rssi":-83},{"bssid":"d0:57:4c:cb:bc:0f","rssi":-84},{"bssid":"d0:57:4c:cb:7c:00","rssi":-81},{"bssid":"d0:57:4c:ca:e2:50","rssi":-83},{"bssid":"d0:57:4c:ca:6b:20","rssi":-84},{"bssid":"dc:7b:94:35:9e:e0","rssi":-85},{"bssid":"00:15:e9:e0:2b:bf","rssi":-85},{"bssid":"d0:57:4c:cb:bc:02","rssi":-86},{"bssid":"dc:7b:94:35:d7:70","rssi":-89},{"bssid":"dc:7b:94:35:be:a0","rssi":-90}]');

			// outside 1.3
			//$wifiData = json_decode('[{"bssid":"80:56:f2:ea:2f:df","rssi":-53},{"bssid":"dc:7b:94:34:86:e9","rssi":-68},{"bssid":"dc:7b:94:34:86:ec","rssi":-68},{"bssid":"d0:57:4c:cb:8d:79","rssi":-74},{"bssid":"d0:57:4c:cb:8d:7c","rssi":-75},{"bssid":"5c:63:bf:3f:12:f4","rssi":-72},{"bssid":"d0:57:4c:cb:8d:76","rssi":-76},{"bssid":"dc:7b:94:34:aa:49","rssi":-81},{"bssid":"94:0c:6d:64:ce:c6","rssi":-77},{"bssid":"d0:57:4c:cb:8e:1c","rssi":-83},{"bssid":"5c:63:bf:40:df:36","rssi":-80},{"bssid":"dc:7b:94:34:a9:09","rssi":-85},{"bssid":"dc:7b:94:34:aa:43","rssi":-81},{"bssid":"d0:57:4c:cb:bb:56","rssi":-83},{"bssid":"dc:7b:94:35:9e:e3","rssi":-85},{"bssid":"b0:48:7a:7f:af:f4","rssi":-85},{"bssid":"e0:05:c5:ba:99:bc","rssi":-86},{"bssid":"00:24:01:93:34:dc","rssi":-86},{"bssid":"dc:7b:94:34:e8:c6","rssi":-87},{"bssid":"d0:57:4c:ca:d5:a6","rssi":-88},{"bssid":"c0:3f:0e:81:20:37","rssi":-89},{"bssid":"dc:7b:94:34:86:e0","rssi":-59},{"bssid":"dc:7b:94:34:86:ef","rssi":-66},{"bssid":"dc:7b:94:34:86:ed","rssi":-68},{"bssid":"d0:57:4c:cb:8d:7d","rssi":-74},{"bssid":"d0:57:4c:cb:8d:7f","rssi":-74},{"bssid":"d0:57:4c:cb:8d:72","rssi":-76},{"bssid":"dc:7b:94:35:9f:72","rssi":-77},{"bssid":"d0:57:4c:cb:8d:70","rssi":-79},{"bssid":"dc:7b:94:34:a9:00","rssi":-82},{"bssid":"d0:57:4c:cb:7c:00","rssi":-82},{"bssid":"dc:7b:94:34:aa:40","rssi":-83},{"bssid":"dc:7b:94:35:c7:20","rssi":-85},{"bssid":"dc:7b:94:35:9e:e0","rssi":-86},{"bssid":"d0:57:4c:cb:9e:10","rssi":-87},{"bssid":"d0:57:4c:ca:d5:a0","rssi":-87}]');

			// The wifi ap list received at this locating time, and get wifi BSSID-Rssi pair
			$receiveApList = array();
			$wifiPair = array();
			foreach ($wifiData as $wifiItem) {
				array_push($receiveApList, $wifiItem->bssid);
				$wifiPair[$wifiItem->bssid] = $wifiItem->rssi;
			}


			// TODO filter which building the person is in, and which rooms the person may be in
			$buildingId = Building::find()->first()->id;
			$roomList = Room::find("buildingId", $buildingId)->all();

			$result = new Location($buildingId, -1, 0, 0, 0, Location::MINIMAL_SCORE);

			foreach ($roomList as $room) {
				$roomApList = RoomApList::find("roomId", $room->id)->first();
				if ($roomApList === null) {
					throw new RException('Room Ap List not created at Room:'.$room->id);
				}
				$roomApList->unpack();

				$usableApList = array_intersect($receiveApList, $roomApList->apList);
				// TODO need to maintain the two metrics to see whether the room need to be skipped
				if (count($usableApList) < 5 && count($usableApList) < $receiveApList * 0.3) {
					continue;
				}

				$roomRpList = RoomRpList::find("roomId", $room->id)->first();
				$roomRpList->unpack();

				// Merge potential APs not scanned into AP list we need to take into consideration
				$matrixApList = $usableApList;

				foreach ($roomRpList->rpList as $rp) {
					$rpApList = RpApList::getRpApList($room->id, $rp->x, $rp->y, $rp->z);
					$rpApList->unpack();
					if (count(array_intersect($rpApList->apList, $usableApList)) / count($usableApList) > 0.7) {
						$matrixApList = array_merge($matrixApList, $rpApList->apList);
					}
				}

				$matrixApList = array_unique($matrixApList);

				// Get probability matrix
				$probMatrix = array();
				foreach ($roomRpList->rpList as $rp) {
					$distribution = WifiRssiDistribution::getWifiRssiDistribution($room->id, $rp->x, $rp->y, $rp->z);
					$rpApProb = array();
					$rpApDistribution = array();
					foreach ($distribution as $bssidDistribution) {
						$rpApDistribution[$bssidDistribution->bssid] = $bssidDistribution;
					}
					foreach ($matrixApList as $ap) {
						// received RSSI of an ap
						if (in_array($ap, $receiveApList)) {
							$rssi = $wifiPair[$ap];
						} else {
							$rssi = -120;
						}

						if (isset($rpApDistribution[$ap])) {
							$rpApDistribution[$ap]->unpack();
							array_push($rpApProb, $rpApDistribution[$ap]->distribution[$rssi]);
						} else {
							array_push($rpApProb, 0);
						}
					}
					array_push($probMatrix, $rpApProb);
				}

				// AP filter
				$apLocation = array();
				for ($i = 0; $i < count($matrixApList); ++$i) {
					if (count($roomRpList->rpList) == 1) {
						array_push($apLocation,
							new Space3DPoint(
								$roomRpList->rpList[0]->x,
								$roomRpList->rpList[0]->y,
								$roomRpList->rpList[0]->z
							)
						);
					} else {
						$prob1 = -1e100;
						$prob2 = -1e100;
						for ($j = 0; $j < count($roomRpList->rpList); ++$j) {
							if ($probMatrix[$j][$i] > $prob1) {
								$prob1 = $probMatrix[$j][$i];
								$rp1 = $j;
							}
						}
						for ($j = 0; $j < count($roomRpList->rpList); ++$j) {
							if ($j != $rp1 && $probMatrix[$j][$i] > $prob2) {
								$prob2 = $probMatrix[$j][$i];
								$rp2 = $j;
							}
						}
						array_push($apLocation,
							new Space3DPoint(
								($roomRpList->rpList[$rp1]->x + $roomRpList->rpList[$rp2]->x) / 2,
								($roomRpList->rpList[$rp1]->y + $roomRpList->rpList[$rp2]->y) / 2,
								($roomRpList->rpList[$rp1]->z + $roomRpList->rpList[$rp2]->z) / 2
							)
						);
					}
				}

				$apValid = array();
				$hasAp = false;
				foreach ($apLocation as $location) {
					$count = 0;
					foreach ($apLocation as $other) {
						if ($location->distance($other) < 3) {
							++$count;
						}
					}
					$valid = $count > (count($apLocation) + 1) / 2;
					array_push($apValid, $valid);
					$hasAp |= $valid;
				}

				// Outside this room
				if (!$hasAp) {
					continue;
				}

				// Score of each point
				$wScore = array();
				for ($i = 0; $i < count($roomRpList->rpList); ++$i) {
					$wScore[$i] = 0;
					for ($j = 0; $j < count($apValid); ++$j) {
						if ($apValid[$j]) {
							$wScore[$i] += $probMatrix[$i][$j];
						}
					}
				}

//				for ($i = 0; $i < count($apValid); ++$i) {
//					if ($apValid[$i]) {
//						//echo $apLocation[$i]->x." ".$apLocation[$i]->y." ".$apLocation[$i]->z."<br/>";
//						$hasAp = true; break;
//					}
//				}

				// Construct X, Y, Z grid
				// TODO Attention: Now room must contain at least 2*2 X,Y RP, and 3 Layer Z RP
				$xList = array();
				$yList = array();
				$zList = array();

				foreach ($roomRpList->rpList as $rp) {
					array_push($xList, $rp->x);
					array_push($yList, $rp->y);
					array_push($zList, $rp->z);
				}

				$xList = array_values(array_unique($xList));
				$yList = array_values(array_unique($yList));
				$zList = array_values(array_unique($zList));

				$gridMatrix = array();
				for ($i = 0; $i < count($xList); ++$i) {
					$x = $xList[$i];
					for ($j = 0; $j < count($yList); ++$j) {
						$y = $yList[$j];
						for ($k = 0; $k < count($zList); ++$k) {
							$z = $zList[$k];
							$gridMatrix[$i][$j][$k] = -1;
							for ($tp = 0; $tp < count($roomRpList->rpList); ++$tp) {
								if ((new Space3DPoint($x, $y, $z))->equalToValue(
									$roomRpList->rpList[$tp]->x,
									$roomRpList->rpList[$tp]->y,
									$roomRpList->rpList[$tp]->z)
								) {
									$gridMatrix[$i][$j][$k] = $tp;
									break;
								}
							}
						}
					}
				}

				// X, Y, Z partial sum
				$gridMatrixScore = array();
				for ($i = 0; $i < count($xList); ++$i) {
					for ($j = 0; $j < count($yList); ++$j) {
						for ($k = 0; $k < count($zList); ++$k) {
							if ($gridMatrix[$i][$j][$k] == -1) {
								$gridMatrixScore[$i][$j][$k] = 0;
							} else {
								$gridMatrixScore[$i][$j][$k] = $wScore[$gridMatrix[$i][$j][$k]];
							}
//							if ($i > 0) {
//								$gridMatrixScore[$i][$j][$k] += $gridMatrixScore[$i - 1][$j][$k];
//							}
//							if ($j > 0) {
//								$gridMatrixScore[$i][$j][$k] += $gridMatrixScore[$i][$j - 1][$k];
//							}
//							if ($i > 0 && $j > 0) {
//								$gridMatrixScore[$i][$j][$k] -= $gridMatrixScore[$i - 1][$j - 1][$k];
//							}
						}
					}
				}

				// Contract range
				for ($anchorZ = 0; $anchorZ < 2; ++$anchorZ) {
					$anchorX = 0; $anchorY = 0;
					$xRange = count($xList);
					$yRange = count($yList);
					while ($xRange > 2 || $yRange > 2) {
						if ($xRange > $yRange) {
							$score1 = 0; $score2 = 0;
							for ($i = 1; $i < $xRange; ++$i) {
								for ($j = 0; $j < $yRange; ++$j) {
									$score1 += $gridMatrixScore[$anchorX + $i - 1][$anchorY + $j][$anchorZ]
										+ $gridMatrixScore[$anchorX + $i - 1][$anchorY + $j][$anchorZ + 1];
									$score2 += $gridMatrixScore[$anchorX + $i][$anchorY + $j][$anchorZ]
										+ $gridMatrixScore[$anchorX + $i][$anchorY + $j][$anchorZ + 1];
								}
							}
							if ($score1 < $score2) {
								++$anchorX;
							}
							--$xRange;
						} else {
							$score1 = 0; $score2 = 0;
							for ($i = 0; $i < $xRange; ++$i) {
								for ($j = 1; $j < $yRange; ++$j) {
									$score1 += $gridMatrixScore[$anchorX + $i][$anchorY + $j - 1][$anchorZ]
										+ $gridMatrixScore[$anchorX + $i][$anchorY + $j - 1][$anchorZ + 1];
									$score2 += $gridMatrixScore[$anchorX + $i][$anchorY + $j][$anchorZ]
										+ $gridMatrixScore[$anchorX + $i][$anchorY + $j][$anchorZ + 1];
								}
							}
							if ($score1 < $score2) {
								++$anchorY;
							}
							--$yRange;
						}
					}
					echo $xList[$anchorX]." ".$yList[$anchorY];
				}


				//echo json_encode($gridMatrixScore);

				//echo json_encode($apValid);
				//echo json_encode($probMatrix);
			}
			//$roomApList = RoomRpList::find("roomId", $)
		} else {
			throw new RException("no data received");
		}
	}

//	public function actionWatch($x1, $y1, $x2, $y2) {
//		$wifiSample1 = WifiSample::where("[x] = ?", $x1)->where("[y] = ?", $y1)->first();
//		$wifiSample1->unPackBSSIVector();
//
//		$wifiSample2 = WifiSample::where("[x] = ?", $x2)->where("[y] = ?", $y2)->first();
//		$wifiSample2->unPackBSSIVector();
//
//		//echo json_encode($wifiSample2->bssiVector);exit;
//
//
//		var_dump(knnDistance($wifiSample1->bssiVector, $wifiSample2->bssiVector));
//
//	}

	/**
	 * Use a json array of <BSSI, magnitude> to judge the location.
	 * Return a json entity {buildingId:xx, floor:xx, x:xx, y:xx}
	 */
	public function actionWifiJudgePosition() {
		/* TODO verify authority, only our application users can use the wifi positioning function */
		if (Rays::isPost()) {
			echo json_encode(array("buildingId" => 2,
					"floor" => 1,
					"x" => 11, "y" => 3.5,
				)
			);
			exit;

//			$wifiPair = array(
////				"00:23:5d:8c:08:43" => -93,
////				"00:23:5d:8c:08:42" => -93,
////				"0c:84:dc:95:b8:ea" => -93,
////				"00:23:5d:8c:08:40" => -93,
////				"d0:57:4c:cb:8e:10" => -96,
////				"dc:7b:94:35:9e:c2" => -83,
////				"dc:7b:94:35:9e:c3" => -82,
////				"00:16:9c:ba:3d:90" => -48,
////				"dc:7b:94:35:9e:c6" => -78,
////				"d0:57:4c:ca:a9:52" => -81,
////				"d0:57:4c:ca:a9:53" => -80,
////				"e0:46:9a:53:4f:f5" => -92,
////				"d0:57:4c:ca:a9:50" => -81,
////				"dc:7b:94:35:9e:c0" => -78,
////				"00:23:5d:8c:08:00" => -95,
////				"d0:57:4c:ca:a9:56" => -82
//
//				"d0:57:4c:ca:a9:52" => -65,
//				"0c:84:dc:95:b8:ea" => -91,
//				"d0:57:4c:ca:a9:53" => -64,
//				"e0:46:9a:53:4f:f5" => -98,
//				"d0:57:4c:ca:a9:50" => -77,
//				"dc:7b:94:35:9e:c0" => -84,
//				"dc:7b:94:35:9e:c2" => -74,
//				"00:23:5d:8c:08:00" => -92,
//				"dc:7b:94:35:9e:c3" => -73,
//				"00:16:9c:ba:3d:90" => -59,
//				"dc:7b:94:35:9e:c6" => -84,
//				"d0:57:4c:ca:a9:56" => -78,
////
////				"dc:7b:94:35:9e:c2" => -80,
////				"dc:7b:94:35:9e:c0" => -80,
////				"00:16:9c:ba:3d:90" => -64,
//
//			);


			$wifiReceive = json_decode($_POST['json']);
			$wifiPair = json_decode($wifiReceive->fingerPrintPack);
			//echo json_encode($wifiPair);exit;
			if (!is_array($wifiPair)) {
				$wifiPair = o2a($wifiPair);
			}


			/**
			 * Implementation
			 */
			if (!count($wifiPair)) {
				throw new RException("wifi empty");
			}
			$wifiList = array_keys($wifiPair);

			$referencePoint = array(	/** TODO hard coded reference points */
				array(0, 0),	array(2, 0),	array(4, 0),	array(6, 0),	array(8, 0),
				array(0, 2),	array(2, 2),	array(4, 2),	array(6, 2),	array(8, 2),
				array(0, 4),	array(2, 4),	array(4, 4),	array(6, 4),	array(8, 4),
				array(0, 6),	array(2, 6),	array(4, 6),	array(6, 6),	array(8, 6),
				array(0, 8),	array(2, 8),	array(4, 8),	array(6, 8),	array(8, 8),
				array(0, 10),	array(2, 10),	array(4, 10),	array(6, 10),	array(8, 10),
			);

			$region = array(
				array(0, 1, 5, 6),		array(1, 2, 6, 7), 		array(2, 3, 7, 8),		array(3, 4, 8, 9),
				array(5, 6, 10, 11),	array(6, 7, 11, 12), 	array(7, 8, 12, 13),	array(8, 9, 13, 14),
				array(10, 11, 15, 16),	array(11, 12, 16, 17), 	array(12, 13, 17, 18),	array(13, 14, 18, 19),
				array(15, 16, 20, 21),	array(16, 17, 21, 22), 	array(17, 18, 22, 23),	array(18, 19, 23, 24),
				array(20, 21, 25, 26),	array(21, 22, 26, 27), 	array(22, 23, 27, 28),	array(23, 24, 28, 29),
			);

			//====================== Stage I ==================================

			$buildingWifiList = BuildingWifiList::getBuildingWiFiList(1, 1);	/** TODO hard coded building and floor */
			$buildingWifiList->unPackWifiList();

			//var_dump($buildingWifiList->wifiList);exit;
			$wifiList = array_intersect($wifiList, $buildingWifiList->wifiList);	// intersect filter out new wifis

			if (count($wifiList) < 3) {
				echo json_encode(array("buildingId" => 1,
						"floor" => 1,
						"x" => 6, "y" => 1,
					)
				);
				exit;
			}

			$distributionList = array();
			$rssiDistributionSet = RSSIDistribution::where("[buildingId] = ?", 1)->where("[floor] = ?", 1)->all();	/** TODO hard coded building and floor */

			foreach ($buildingWifiList->wifiList as $ap) {
				foreach ($referencePoint as $point) {
					$distribution = new RSSIDistribution();
					for ($t = 0; $t < 110; $t += 2)
						$distribution->distribution[$t] = 0;
					$distributionList[$ap][$point[0]][$point[1]] = $distribution;
				}
			}

			foreach ($rssiDistributionSet as $distribution) {
				$distribution->unPackDistribution();
				$distributionList[$distribution->bssid][$distribution->x][$distribution->y] = $distribution;
			}

			$probList = array();

			$avgCoord = array();

			$times = 0;
			foreach ($buildingWifiList->wifiList as $ap) {
				if (isset($wifiList[$ap])) {
					$rssi = -$wifiPair[$ap];
					$rssi = $rssi - ($rssi % 2);
				} else {
					$rssi = 0;
				}
				$count = 0;
				foreach ($referencePoint as $point) {
					$probList[$count] = $distributionList[$ap][$point[0]][$point[1]]->distribution[$rssi];
					++$count;
				}
                $tmp1 = -1e100;
				$tmp2 = -1e100;
				$firstId = 0;
				$secondId = 0;
				$count = 0;
				foreach($probList as $probpoint){
					if ($probpoint > $tmp1) {
						$tmp2 = $tmp1;
						$secondId = $firstId;
						$firstId = $count;
						$tmp1 = $probpoint;
					} else if ($probpoint > $tmp2) {
						$secondId = $count;
						$tmp2 = $probpoint;
					}
					++$count;
				}

				$avgCoord[$times] = array(
					($referencePoint[$firstId][0] + $referencePoint[$secondId][0]) / 2,
					($referencePoint[$firstId][1] + $referencePoint[$secondId][1]) / 2,
					$firstId, $secondId,
				);

				++$times;
			}

			$flag = array();
			for ($i = 0; $i < $times; ++$i) {
				$flag[$i] = true;
			}
			for ($i = 0; $i < $times / 2; ++$i) {
				$calX = 0; $calY = 0;
				for ($j = 0; $j < $times; ++$j) {
					if ($flag[$j]) {
						$calX += $avgCoord[$j][0];
						$calY += $avgCoord[$j][1];
					}
				}
				$tmp = -1e100;
				$id = 0;
				for ($j = 0; $j < $times; ++$j) {
					if ($flag[$j]) {
						$dd = dist($avgCoord[$j][0], $avgCoord[$j][1], $calX, $calY);
						if ($dd > $tmp) {
							$tmp = $dd;
							$id = $j;
						}
					}
				}
				$flag[$id] = false;
			}

			//====================== Stage II ==================================

			$pointValid = array();
			for ($i = 0; $i < 30; ++$i) {		/** TODO hard coded reference point number */
				$pointValid[$i] = false;
			}

			for ($i = 0; $i < $times; ++$i) {
				if ($flag[$i]) {
					$pointValid[$avgCoord[$i][2]] = $pointValid[$avgCoord[$i][3]] = true;
				}
			}

			$tmp = -1e100;
			$groupId = -1;
			$tsop = array();
			for ($g = 0; $g < 20; ++$g) {
				if ($pointValid[$region[$g][0]] || $pointValid[$region[$g][1]] ||
					$pointValid[$region[$g][2]] || $pointValid[$region[$g][3]]) {
					$sop = 0;
					$sops = array();
					for ($loop = 0; $loop < 4; ++$loop) {
						$tmpId = $region[$g][$loop];
						$sops[$loop] = 0;
						for ($ii = 0; $ii < $times; ++$ii) {
							if ($flag[$ii]) {
								$ap = $buildingWifiList->wifiList[$ii];
								$distribution = $distributionList[$ap][$referencePoint[$tmpId][0]][$referencePoint[$tmpId][1]];
								if (in_array($ap, $wifiList)) {
									$rssi = -$wifiPair[$ap];
									$rssi = $rssi - ($rssi % 2);
									//var_dump($rssi);
								} else {
									$rssi = 0;
								}
								if ($rssi != 0) {
									$sop += $distribution->distribution[$rssi];
									$sops[$loop] += $distribution->distribution[$rssi];
								}

							}
						}
					}
					//var_dump($sop);
					if ($sop > $tmp) {
						$tmp = $sop;
						$groupId = $g;
						for ($k = 0; $k < 4; ++$k) {
							$tsop[$k] = $sops[$k];
						}
					}
				}
			}

			//var_dump($groupId);

			//====================== Stage III ================================
			$estX = 5; $estY = 1;
			if ($tmp > -1e100 && $tmp != 0) {
				$estX = 0; $estY = 0;
				for ($p = 0; $p < 4; ++$p) {
					$estX += $tsop[$p] / $tmp * $referencePoint[$region[$groupId][$p]][0];
					$estY += $tsop[$p] / $tmp * $referencePoint[$region[$groupId][$p]][1];
				}
			}

			/**
			 * TODO hook
			 */
			if (($estX < 2 && $estY  < 2) || $estY > 4) {
				$estX = 6; $estY = 1;
			}

			echo json_encode(array("buildingId" => 1,
					"floor" => 1,
					"x" => $estX, "y" => $estY,
				)
			);

		} else {
			throw new RException("no data received");
		}
	}

//	public function actionWifiJudgePosition2() {
//		/* TODO verify authority, only our application users can use the wifi positioning function */
//		if (Rays::isPost()) {
//			$wifiReceive = json_decode($_POST['json']);
//			$wifiPair = json_decode($wifiReceive->fingerPrintPack);
//			//echo json_encode($wifiPair);exit;
//			if (!is_array($wifiPair)) {
//				$wifiPair = o2a($wifiPair);
//			}
//
//			/**
//			 * Implementation
//			 */
//			if (!count($wifiPair)) {
//				throw new RException("wifi empty");
//			}
//			$wifiList = array_keys($wifiPair);
//
//			$result = new Location();
//			$result->score = -1e100;
//
//			if ($wifiReceive->buildingId != -1) {
//				$buildingWifiSet = BuildingWifiList::find("buildingId", $wifiReceive->buildingId)->all();
//			} else {
//				$buildingWifiSet = BuildingWifiList::find()->all();
//			}
//
//			foreach ($buildingWifiSet as $building) {
//				$building->unPackWifiList();
//				// filter out buildings with low similarity
//				if (count(array_intersect($wifiList, $building->wifiList)) / count($wifiList) < 0.5) {
//					continue;
//				}
//				// get points of the building
//				$wifiSamples = WiFiSample::find("buildingId", $building->buildingId)->all();
//				foreach ($wifiSamples as $wifiSample) {
//					$wifiSample->unPackBSSIVector();
//					$tmp = new Location();
//					$tmp->buildingId = $wifiSample->buildingId;
//					$tmp->floor = $wifiSample->floor;
//					$tmp->x = $wifiSample->x;
//					$tmp->y = $wifiSample->y;
//
//					/** TODO do experiments to select which algorithm to use */
//					$tmp->score = knnDistance($wifiSample->bssiVector, $wifiPair);
//
//					//$tmp->score = cosDistance($wifiSample->bssiVector, $wifiPair);
//					//$tmp->score = -euclideanDistance($wifiSample->bssiVector, $wifiPair);
//					//$tmp->score = weighedDistance($wifiSample->bssiVector, $wifiPair);
//					//$tmp->score = setDistance($wifiSample->bssiVector, $wifiPair);
//
//					if ($tmp->betterThan($result)) {
//						$result = $tmp;
//					}
//				}
//			}
//
//			// score too small
////			if ($result->score < 0.3) {
////				throw new RException("locating failed");
////			}
//
//			/** TODO do experiments to select which algorithm to use */
//			/*//For euclidean distance
//			if ($result->score < -1e10) {
//				throw new RException("locating failed");
//			}*/
//			/*//For weighed distance
//			if ($result->score < 0.1) {
//				throw new RException("locating failed");
//			}
//			*/
//
////			echo json_encode(array("buildingId" => $result->buildingId,
////				"floor" => $result->floor,
////				"x" => $result->x, "y" => $result->y)
////			);
//
//			echo json_encode(array("buildingId" => 1,
//					"floor" => 1,
//					"x" => 6, "y" => 1
//				)
//			);
//
//		} else {
//			throw new RException("no data received");
//		}
//	}

}