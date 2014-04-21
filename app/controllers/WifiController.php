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


	public function actionShowSample($sampleId = -1) {
		$sample = new WiFiSample();
		echo json_encode($sample->getWiFiSample($sampleId));
	}

	/**
	 * Add a new wifi sample point to database
	 */
	public function actionUploadWifi($roomId, $x, $y, $z) {
		if (Rays::isPost()) {
			$wifiData = json_decode(Rays::getParam("wifiData", "[]"));
			if (!count($wifiData)) {
				throw new RException("wifi empty error");
			}
			$wifiPoint = WifiFingerprint::find("roomId", $roomId)->where("[x] = ?", $x)->where("[y] = ?", $y)->where("[z] = ?", $z)->first();
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
	 * Add a new wifi sample point to database
	 */
	public function actionAddWifi() {
		/* TODO verify authority, only admin can add wifi at this time */
		if (Rays::isPost()) {
			WiFiSample::createWiFiSample(json_decode($_POST['json']));
			echo json_encode(array("response" => "ok"));
		} else {
			throw new RException("no data received");
		}
	}

	/**
	 * Update wifi sample point information
	 */
	public function actionUpdateWifi() {

	}

	public function actionUpdateDistribution($buildingId, $floor, $x, $y) {
		RSSIDistribution::getRSSIDistribution($buildingId, $floor, $x, $y);
	}

	public function actionWatch($x1, $y1, $x2, $y2) {
		$wifiSample1 = WifiSample::where("[x] = ?", $x1)->where("[y] = ?", $y1)->first();
		$wifiSample1->unPackBSSIVector();

		$wifiSample2 = WifiSample::where("[x] = ?", $x2)->where("[y] = ?", $y2)->first();
		$wifiSample2->unPackBSSIVector();

		//echo json_encode($wifiSample2->bssiVector);exit;


		var_dump(knnDistance($wifiSample1->bssiVector, $wifiSample2->bssiVector));

	}

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

	public function actionWifiJudgePosition2() {
		/* TODO verify authority, only our application users can use the wifi positioning function */
		if (Rays::isPost()) {
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

			$result = new Location();
			$result->score = -1e100;

			if ($wifiReceive->buildingId != -1) {
				$buildingWifiSet = BuildingWifiList::find("buildingId", $wifiReceive->buildingId)->all();
			} else {
				$buildingWifiSet = BuildingWifiList::find()->all();
			}

			foreach ($buildingWifiSet as $building) {
				$building->unPackWifiList();
				// filter out buildings with low similarity
				if (count(array_intersect($wifiList, $building->wifiList)) / count($wifiList) < 0.5) {
					continue;
				}
				// get points of the building
				$wifiSamples = WiFiSample::find("buildingId", $building->buildingId)->all();
				foreach ($wifiSamples as $wifiSample) {
					$wifiSample->unPackBSSIVector();
					$tmp = new Location();
					$tmp->buildingId = $wifiSample->buildingId;
					$tmp->floor = $wifiSample->floor;
					$tmp->x = $wifiSample->x;
					$tmp->y = $wifiSample->y;

					/** TODO do experiments to select which algorithm to use */
					$tmp->score = knnDistance($wifiSample->bssiVector, $wifiPair);

					//$tmp->score = cosDistance($wifiSample->bssiVector, $wifiPair);
					//$tmp->score = -euclideanDistance($wifiSample->bssiVector, $wifiPair);
					//$tmp->score = weighedDistance($wifiSample->bssiVector, $wifiPair);
					//$tmp->score = setDistance($wifiSample->bssiVector, $wifiPair);

					if ($tmp->betterThan($result)) {
						$result = $tmp;
					}
				}
			}

			// score too small
//			if ($result->score < 0.3) {
//				throw new RException("locating failed");
//			}

			/** TODO do experiments to select which algorithm to use */
			/*//For euclidean distance
			if ($result->score < -1e10) {
				throw new RException("locating failed");
			}*/
			/*//For weighed distance
			if ($result->score < 0.1) {
				throw new RException("locating failed");
			}
			*/

//			echo json_encode(array("buildingId" => $result->buildingId,
//				"floor" => $result->floor,
//				"x" => $result->x, "y" => $result->y)
//			);

			echo json_encode(array("buildingId" => 1,
					"floor" => 1,
					"x" => 6, "y" => 1
				)
			);

		} else {
			throw new RException("no data received");
		}
	}

}