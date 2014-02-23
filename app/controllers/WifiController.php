<?php
/**
 * Created by PhpStorm.
 * User: songrenchu
 * Date: 23/2/14
 * Time: 5:03 PM
 */

class WifiController extends RController {

	public function actionAddWifi() {
		if (Rays::isPost()) {
			$buildingId = 0;
			$floor = 0;
			$x = 0; $y = 0;
			$bssiVector = array();
			extract(json_decode($_POST), EXTR_OVERWRITE);
			WiFiSample::createWiFiSample($buildingId, $floor, $x, $y, $bssiVector);
			echo json_encode("ok");
		} else {
			echo json_encode("fail");
		}
	}

} 