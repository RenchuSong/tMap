<?php
/**
 * Created by PhpStorm.
 * User: songrenchu
 * Date: 24/2/14
 * Time: 12:59 PM
 */

class BuildingController extends RController {

	public function actionIndex() {
		echo json_encode(["response" => "Hello tMap!"]);
	}

	/**
	 * Update building Wifi
	 */
	public function actionBuildingUpdateWifi($buildingId = null) {
		/* TODO verify authority, only admin can update building wifi */
		if ($buildingId !== null && is_numeric($buildingId)) {			// update a single building
			$buildingWifi = new BuildingWifiList();
			$buildingWifi->buildingId = $buildingId;
			$buildingWifi->buildingUpdateWifi();
			echo json_encode(["response" => "ok"]);
			return;
		}
		/* TODO update all buildings */
		throw new RException("Update all building wifi not implemented");
	}
}