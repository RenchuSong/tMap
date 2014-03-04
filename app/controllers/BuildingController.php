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


	public function actionShortestPath() {
		$buildingId = 1;
		$person1_floor = 2;
		$person1_x = 4;
		$person1_y = -4;
		$person1_cell = -1;

		$person2_floor = 1;
		$person2_x = 0;
		$person2_y = 2;
		$person2_cell = -1;

		//$cellList = Cell::getCellList($buildingId);
		$cellList = Cell::find("buildingId", $buildingId)->all();
		$keyCellList = [];
		foreach ($cellList as $cell) {
			//build the distance of adjacent nodes into distance array
			if ($cell->inside($person1_floor,$person1_x,$person1_y)) $person1_cell = $cell->id;
			if ($cell->inside($person2_floor,$person2_x,$person2_y)) $person2_cell = $cell->id;
			$keyCellList[$cell->id] = $cell;
		}
		/*echo $person1_cell;
		echo "<br>";
		echo $person2_cell;
		echo "<br>";
		//var_dump($keyCellList);
		echo "<br>";
		echo "<br>";
		echo "<br>";*/

		//add ladders information into the keyCellList
		$ladders = Ladder::find("buildingId",$buildingId)->all();
		foreach ($ladders as $ladder) {
			$dis = $ladder->getLength();
			array_push($keyCellList[$ladder->lowCellId]->adjacentCellList,$ladder->highCellId);
			array_push($keyCellList[$ladder->highCellId]->adjacentCellList,$ladder->lowCellId);
			$this->ladderDis[$ladder->lowCellId][$ladder->highCellId] = $ladder;
			$this->ladderDis[$ladder->highCellId][$ladder->lowCellId] = $ladder;
		}

		//SPFA part
		$point1 = new Point($person1_x,$person1_y,$person1_floor);
		$pointQueue = array(); array_push($pointQueue,$point1);
		$cellIdQueue = array(); array_push($cellIdQueue,$person1_cell);

		$distance = [];
		$prePoint = [];
		$ladderListForPrePoint = [];
		$distance[$point1->x][$point1->y][$point1->floor] = 0;
		$prePoint[$point1->x][$point1->y][$point1->floor] = null;
		while (count($cellIdQueue)>0) {
			$point = array_shift($pointQueue);
			$i = array_shift($cellIdQueue);
			$cell = $keyCellList[$i];
			$adjacentList = $cell->adjacentCellList;
			$disNow = $distance[$point->x][$point->y][$point->floor];
			foreach ($adjacentList as $j) {
				//$dis = -1;
				$point2 = null;
				if (isset($this->ladderDis[$i][$j])) {  //stairs or elevator
                	$ladder = $this->ladderDis[$i][$j];

					$dis = $ladder->getLength() + $disNow;

					//$ladder = new Ladder();
					if ($ladder->lowCellId == $i) {
						$point2 = new Point($ladder->highPoint->x,$ladder->highPoint->y,$keyCellList[$ladder->highCellId]->floorId);
						$dis += BaseStaticFun::getDistance($ladder->lowPoint->x,$ladder->lowPoint->y,$point->x,$point->y);
						$ladderListForPrePoint[$point2->x][$point2->y][$point2->floor] = $ladder->verticeList;
					}
					else {
						$point2 = new Point($ladder->lowPoint->x,$ladder->lowPoint->y,$keyCellList[$ladder->lowCellId]->floorId);
						$dis += BaseStaticFun::getDistance($ladder->highPoint->x,$ladder->highPoint->y,$point->x,$point->y);
						$temp = array_reverse($ladder->verticeList);
						for ($i = count($temp)-1; $i>0; $i--)
							$temp[$i]->z = -$temp[$i-1]->z;
						$temp[0]->z = 0;
						//var_dump($temp);
						$ladderListForPrePoint[$point2->x][$point2->y][$point2->floor] = $temp;
					}
				}
				else {
					$point2 = $this->getTwoCellsMiddlePoint($cell,$keyCellList[$j]);
					$dis = BaseStaticFun::getDistance($point->x,$point->y,$point2->x,$point2->y) + $disNow;
				}
				if (! isset($distance[$point2->x][$point2->y][$point2->floor]) || (isset($distance[$point2->x][$point2->y][$point2->floor]) && $dis<$distance[$point2->x][$point2->y][$point2->floor])) {
					$distance[$point2->x][$point2->y][$point2->floor] = $dis;
					$prePoint[$point2->x][$point2->y][$point2->floor] = $point;
					array_push($pointQueue,$point2);
					array_push($cellIdQueue,$j);
				}
			}
		}

		//check the point of person2's cell
		$endCellIdList = $keyCellList[$person2_cell]->adjacentCellList;
		$ansDistance = 6000000000;
		$endPoint = null;
		foreach ($endCellIdList as $endCellId) {
			$endCell = $keyCellList[$endCellId];
			$tempPoint = $this->getTwoCellsMiddlePoint($endCell,$keyCellList[$person2_cell]);
			//var_dump($tempPoint);
			if (isset($distance[$tempPoint->x][$tempPoint->y][$tempPoint->floor])) {
				$tempDis = $distance[$tempPoint->x][$tempPoint->y][$tempPoint->floor] + BaseStaticFun::getDistance($tempPoint->x,$tempPoint->y,$person2_x,$person2_y);
				if ($tempDis<$ansDistance) {
					$ansDistance = $tempDis;
					$endPoint = $tempPoint;
				}
			}
		}
		//var_dump($prePoint[2][2]);
		$ansList = [];
		array_push($ansList,new _3DPoint($person2_x,$person2_y,0));
		$point = $endPoint;
		while ($point != null) {
			if (isset($ladderListForPrePoint[$point->x][$point->y][$point->floor])) {
				foreach (array_reverse($ladderListForPrePoint[$point->x][$point->y][$point->floor]) as $p) {
					array_unshift($ansList,$p);
				}
				array_shift($ansList);
			}
			array_unshift($ansList,new _3DPoint($point->x,$point->y,0));
			$point = $prePoint[$point->x][$point->y][$point->floor];
		}
		echo json_encode($ansList);
	}



//	public function actionTest(){
//		//$cellList = Cell::find()->all();
//		$aa = array();
//		/*array_push($a,"aaa");
//		array_push($a,"bbb");
//		var_dump($a);
//		echo "\n";
//		$b = array_shift($a);
//		var_dump($b);
//		var_dump($a);
//
//		$aa[1] = new Point();
//		$aa[1]->x = 1;
//		$aa[1]->y = 1;
//		$aa[2]->x = 0;
//		$aa[2]->y = 0;*/
//		array_push($aa,new Point(2,0));
//		array_push($aa,new Point(2,-4));
//		array_push($aa,new Point(4,-4));
//		array_push($aa,new Point(4,0));
//		echo json_encode($aa);
//		echo "<br />";
//		echo "<br />";
//		$a = [];
//		array_push($a,4);
//
//		$aa = [];
//		array_push($aa,new _3DPoint(3,3,0));
//		array_push($aa,new _3DPoint(4,3,1));
//		array_push($aa,new _3DPoint(3,3,1));
//		echo json_encode($aa);
//		$cellList = Cell::find()->all();
//		$ladderList = Ladder::find()->all();
//		echo "<br>";
//		echo "<br>";
//		var_dump( $ladderList);
//		echo "<br>";
//		var_dump($cellList);
//	}


}