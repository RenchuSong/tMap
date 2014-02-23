<?php
/**
 * Created by PhpStorm.
 * User: songrenchu
 * Date: 23/2/14
 * Time: 5:03 PM
 */


class WifiController extends RController {

	public function actionIndex() {
		echo json_encode(["response" => "Hello tMap!"]);
	}

	/**
	 * Add a new wifi sample point to database
	 * @throws RException
	 */
	public function actionAddWifi() {
		/* TODO verify authority */
		if (Rays::isPost()) {
			$buildingId = 0;
			$floor = 0;
			$x = 0; $y = 0;
			$bssiVector = array();
			extract(json_decode($_POST), EXTR_OVERWRITE);
			WiFiSample::createWiFiSample($buildingId, $floor, $x, $y, $bssiVector);
			echo json_encode(["response" => "ok"]);
		} else {
			throw new RException("no data received");
		}
	}

	/**
	 * Update wifi sample point information
	 * @throws RException
	 */
	public function actionUpdateWifi() {

	}
} 