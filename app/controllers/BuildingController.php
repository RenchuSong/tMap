<?php
/**
 * Created by PhpStorm.
 * User: songrenchu
 * Date: 24/2/14
 * Time: 12:59 PM
 */

class BuildingController extends RController {

	public function actionIndex() {
		echo json_encode(array("response" => "Hello tMap!"));
	}

	/**
	 * Update building Wifi
	 */
	public function actionBuildingUpdateWifi($buildingId = null) {
		/* TODO verify authority, only admin can update building wifi */
		if ($buildingId !== null && is_numeric($buildingId)) {			// update a single building
			$buildingWifi = BuildingWifiList::find("buildingId", $buildingId)->first();
			if ($buildingWifi === null) {
				$buildingWifi = new BUildingWifiList();
				$buildingWifi->buildingId = $buildingId;
			}

			$buildingWifi->buildingUpdateWifi();
			echo json_encode(array("response" => "ok"));
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
			echo json_encode(array("response" => "ok"));
		} else {
			throw new RException("no data received");
		}
	}

//=======
//	public function actionAddCell($x0, $y0, $x1, $y1, $x2, $y2, $x3, $y3) {
//		var_dump($x3);
//	}

	//store the distance of the ladder
	public $ladderDis = array();

	//get the middle point of cell1,cell2's common border
    public function getTwoCellsMiddlePoint($cell, $cell2) {

		if (isset($this->ladderDis[$cell->id][$cell2->id])) return $this->ladderDis[$cell->id][$cell2->id];

		if ($cell->minX == $cell2->maxX || $cell->maxX == $cell2->minX) {
			$y0 = (min($cell->maxY,$cell2->maxY)+max($cell->minY,$cell2->minY))/2;
		    if ($cell->minX == $cell2->maxX) $x0 = $cell->minX;
			else $x0 = $cell->maxX;
		}

		if ($cell->minY == $cell2->maxY || $cell->maxY == $cell2->minY) {
			$x0 = (min($cell->maxX,$cell2->maxX)+max($cell->minX,$cell2->minX))/2;
			if ($cell->minY == $cell2->maxY) $y0 = $cell->minY;
			else $y0 = $cell->maxY;
		}

		if (isset($x0) && isset($y0)) return new Point($x0,$y0,$cell->floorId);
		else echo "please check the list of the adjacent cells";
		return null;
	}


}