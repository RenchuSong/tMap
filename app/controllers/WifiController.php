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

	public function actionShowSample($sampleId = -1) {
		$sample = new WiFiSample();
		echo json_encode($sample->getWiFiSample($sampleId));
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

	public function actionWatch($x1, $y1, $x2, $y2) {
		$wifiSample1 = WifiSample::where("[x] = ?", $x1)->where("[y] = ?", $y1)->first();
		$wifiSample1->unPackBSSIVector();

		$wifiSample2 = WifiSample::where("[x] = ?", $x2)->where("[y] = ?", $y2)->first();
		$wifiSample2->unPackBSSIVector();

		//echo json_encode($wifiSample2->bssiVector);exit;


		var_dump(setDistance($wifiSample1->bssiVector, $wifiSample2->bssiVector));

	}

	/**
	 * Use a json array of <BSSI, magnitude> to judge the location.
	 * Return a json entity {buildingId:xx, floor:xx, x:xx, y:xx}
	 */
	public function actionWifiJudgePosition() {
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

			//var_dump($buildingWifiSet);
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
					$tmp->score = cosDistance($wifiSample->bssiVector, $wifiPair);
					//$tmp->score = -euclideanDistance($wifiSample->bssiVector, $wifiPair);
					//$tmp->score = weighedDistance($wifiSample->bssiVector, $wifiPair);
					//$tmp->score = setDistance($wifiSample->bssiVector, $wifiPair);

					if ($tmp->betterThan($result)) {
						$result = $tmp;
					}
				}
			}

			// score too small
			if ($result->score < 0.3) {
				throw new RException("locating failed");
			}

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

			echo json_encode(array("buildingId" => $result->buildingId,
				"floor" => $result->floor,
				"x" => $result->x, "y" => $result->y)
			);
		} else {
			throw new RException("no data received");
		}
	}
} 