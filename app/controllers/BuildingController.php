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

	/**
	 * Get json presented model of a 3d floor
	 */
	public function actionGetModel($buildingId, $floorId) {
		$floor = FloorModel::where("[buildingId] = ?", $buildingId)->where("[floorId] = ?", $floorId)->first();
		if ($floor !== null) {
			echo $floor->modelPack;
		} else {
			throw new RException("Model not found");
		}
	}

	/**
	 * 保存某幢建筑某层楼的模型
	 */
	public function actionSaveModel($buildingId, $floorId) {
		/* TODO verify authority, only admin can modify models at this time */
		if (Rays::isPost()) {
			$floor = FloorModel::where("[buildingId] = ?", $buildingId)->where("[floorId] = ?", $floorId)->first();
			if ($floor === null) {
				$floor = new FloorModel();
				$floor->buildingId = $buildingId;
				$floor->floorId = $floorId;
			}
			$floor->modelPack = $_POST['json'];
			$floor->save();
			echo json_encode(["response" => "ok"]);
		} else {
			throw new RException("no data received");
		}
	}
}