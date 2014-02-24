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
		echo json_encode(["response" => "Hello tMap!"]);
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
			echo json_encode(["response" => "ok"]);
		} else {
			throw new RException("no data received");
		}
	}

	/**
	 * Update wifi sample point information
	 */
	public function actionUpdateWifi() {

	}

	/**
	 * Use a json array of <BSSI, magnitude> to judge the location.
	 * Return a json entity {buildingId:xx, floor:xx, x:xx, y:xx}
	 */
	public function actionWifiJudgePosition() {
		/* TODO verify authority, only our application users can use the wifi positioning function */
		if (Rays::isPost()) {
			$wifiPair = json_decode($_POST['json']);
			if (!is_array($wifiPair)) {
				$wifiPair = o2a($wifiPair);
			}

			/**
			 * Implementation
			 */
			$buildingId = -1; $floor = 0; $x = 0; $y = 0;

			echo json_encode(["buildingId" => $buildingId, "floor" => $floor, "x" => $x, "y" => $y]);
		} else {
			throw new RException("no data received");
		}
	}
} 