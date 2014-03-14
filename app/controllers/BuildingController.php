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
	public function actionBuildingUpdateWifi($buildingId = null, $floor = null) {
		/* TODO verify authority, only admin can update building wifi */
		if ($buildingId !== null && is_numeric($buildingId) && $floor !== null && is_numeric($floor)) {			// update a single building
			$buildingWifi = BuildingWifiList::find("buildingId", $buildingId)->where("[floor] = ?", $floor)->first();
			if ($buildingWifi === null) {
				$buildingWifi = new BUildingWifiList();
				$buildingWifi->buildingId = $buildingId;
				$buildingWifi->floor = $floor;
			}

			$buildingWifi->buildingUpdateWifi();
			echo json_encode(array("response" => "ok"));
			return;
		}
		/* TODO update all buildings */
		throw new RException("Update all building wifi not implemented");
	}

	public function actionGetBuildingWifiList($buildingId, $floor) {
		$wifi = BuildingWifiList::getBuildingWiFiList($buildingId, $floor);
		echo json_encode($wifi->wifiList);

//		$wifi1 = BuildingWifiList::getBuildingWiFiList(1, 0);
//		$wifi2 = BuildingWifiList::getBuildingWiFiList(1, 1);
//
//		echo json_encode(array_intersect($wifi1->wifiList, $wifi2->wifiList));
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

	public function actionAddOneCell($id, $buildingId, $floorId,$x0, $y0, $x1, $y1, $adjacentlist)
	{
		$cell = new Cell();
		$cell->buildingId = $buildingId;
		$cell->floorId = $floorId;
		$verticeList = array();
		array_push($verticeList,new _2DPoint($x0,$y0));
		array_push($verticeList,new _2DPoint($x1,$y0));
		array_push($verticeList,new _2DPoint($x1,$y1));
		array_push($verticeList,new _2DPoint($x0,$y1));
		$cell->shapePack = json_encode($verticeList);
		$cell->adjacentPack = json_encode($adjacentlist);
		var_dump($cell);
		$cell->save();
		//var_dump($x3);
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

		//var_dump($x0);
		//var_dump($y0);

		if (isset($x0) && isset($y0)) return new Point($x0,$y0,$cell->floorId);
		else //echo "please check the list of the adjacent cells";
		return null;
	}



	public function actionShortestPath($buildingId, $person1_floor, $person1_x, $person1_y, $person2_floor, $person2_x, $person2_y) {
		//$cellList = Cell::getCellList($buildingId);
		$cellList = Cell::find("buildingId", $buildingId)->all();
		$keyCellList = array();
		foreach ($cellList as $cell) {
			//build the distance of adjacent nodes into distance array
			if ($cell->inside($person1_floor,$person1_x,$person1_y)) $person1_cell = $cell->id;
			if ($cell->inside($person2_floor,$person2_x,$person2_y)) $person2_cell = $cell->id;
			$keyCellList[$cell->id] = $cell;
		}

		//var_dump($person1_cell);
		//var_dump($person2_cell);

		if (!isset($person1_cell)) {
			$min = 1e100;
			foreach ($cellList as $cell) {
				if ($cell->distance($person1_floor, $person1_x, $person1_y) < $min) {
					$min = $cell->distance($person1_floor, $person1_x, $person1_y);
					$person1_cell = $cell->id;
				}
			}
		}

		if (!isset($person2_cell)) {
			$min = 1e100;
			foreach ($cellList as $cell) {
				if ($cell->distance($person2_floor, $person2_x, $person2_y) < $min) {
					$min = $cell->distance($person2_floor, $person2_x, $person2_y);
					$person2_cell = $cell->id;
				}
			}
		}

		if ($person1_cell == $person2_cell) {
			$a = new _3DPoint($person1_x, $person1_y, 0);
			$b = new _3DPoint($person2_x, $person2_y, 0);
			echo json_encode(array($a, $b));
			exit;
		}

		//add ladders information into the keyCellList

		$ladders = Ladder::where("[buildingId] = ?",$buildingId)->all();
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

		$distance = array();
		$prePoint = array();
		$ladderListForPrePoint = array();
		$distance[$point1->x*1000][$point1->y*1000][$point1->floor] = 0;
		$prePoint[$point1->x*1000][$point1->y*1000][$point1->floor] = null;

		while (count($cellIdQueue)>0) {
			$point = array_shift($pointQueue);
			$i = array_shift($cellIdQueue);

			$cell = $keyCellList[$i];
			$adjacentList = $cell->adjacentCellList;
			$disNow = $distance[$point->x*1000][$point->y*1000][$point->floor];
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
						$ladderListForPrePoint[$point2->x*1000][$point2->y*1000][$point2->floor] = $ladder->verticeList;
					}
					else {
						$point2 = new Point($ladder->lowPoint->x,$ladder->lowPoint->y,$keyCellList[$ladder->lowCellId]->floorId);
						$dis += BaseStaticFun::getDistance($ladder->highPoint->x,$ladder->highPoint->y,$point->x,$point->y);
						$temp = array_reverse($ladder->verticeList);
						for ($i = count($temp)-1; $i>0; $i--)
							$temp[$i]->z = -$temp[$i-1]->z;
						$temp[0]->z = 0;
						//var_dump($temp);
						$ladderListForPrePoint[$point2->x*1000][$point2->y*1000][$point2->floor] = $temp;
					}
				}
				else {
					$point2 = $this->getTwoCellsMiddlePoint($cell,$keyCellList[$j]);
					$dis = BaseStaticFun::getDistance($point->x,$point->y,$point2->x,$point2->y) + $disNow;
				}
				//echo json_encode($point2); echo "   ;";
				if (! isset($distance[$point2->x*1000][$point2->y*1000][$point2->floor]) || (isset($distance[$point2->x*1000][$point2->y*1000][$point2->floor]) && $dis<$distance[$point2->x*1000][$point2->y*1000][$point2->floor])) {
					$distance[$point2->x*1000][$point2->y*1000][$point2->floor] = $dis;
					$prePoint[$point2->x*1000][$point2->y*1000][$point2->floor] = $point;
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
			if (isset($distance[$tempPoint->x*1000][$tempPoint->y*1000][$tempPoint->floor])) {
				$tempDis = $distance[$tempPoint->x*1000][$tempPoint->y*1000][$tempPoint->floor] + BaseStaticFun::getDistance($tempPoint->x,$tempPoint->y,$person2_x,$person2_y);
				if ($tempDis<$ansDistance) {
					$ansDistance = $tempDis;
					$endPoint = $tempPoint;
				}
			}
		}

		//var_dump($prePoint[2][2]);
		$ansList = array();
		array_push($ansList,new _3DPoint($person2_x,$person2_y,0));
		$point = $endPoint;
		while ($point != null) {
			if (isset($ladderListForPrePoint[$point->x*1000][$point->y*1000][$point->floor])) {
				foreach (array_reverse($ladderListForPrePoint[$point->x*1000][$point->y*1000][$point->floor]) as $p) {
					array_unshift($ansList,$p);
				}
				array_shift($ansList);
			}
			array_unshift($ansList,new _3DPoint($point->x,$point->y,0));
			$point = $prePoint[$point->x*1000][$point->y*1000][$point->floor];
		}
		echo json_encode($ansList);
	}


	public function actionBuildCells() {
		$this->actionAddOneCell(22, 2, 1, 0, 0, 17, 5.5, array(23, 24, 25, 26));

		$this->actionAddOneCell(23, 2, 1, 0, 5.5, 2.2, 28, array(22, 27));
		$this->actionAddOneCell(24, 2, 1, 5, 5.5, 6.5, 28, array(22, 27));
		$this->actionAddOneCell(25, 2, 1, 10.7, 5.5, 12.2, 28, array(22, 27));
		$this->actionAddOneCell(26, 2, 1, 15, 5.5, 17.2, 28, array(22, 27));

		$this->actionAddOneCell(27, 2, 1, 0, 23.5, 17, 25.5, array(23, 24, 25, 26));


//		$this->actionAddOneCell(14,1,1,	1.45,	0,		8.53,	3,		array(15, 16));
//		$this->actionAddOneCell(15,1,1,	0,		1.45,	1.45,	11.29,	array(14, 17, 18, 19, 20, 21));
//		$this->actionAddOneCell(16,1,1,	6.5,	3,		8,		11.29,	array(14, 17, 18, 19, 20, 21));
//		$this->actionAddOneCell(17,1,1,	1.45,	3,		6.5,	4.5,	array(15, 16));
//		$this->actionAddOneCell(18,1,1,	1.45,	4.5,	6.5,	6,		array(15, 16));
//		$this->actionAddOneCell(19,1,1,	1.45,	6,		6.5,	7.5,	array(15, 16));
//		$this->actionAddOneCell(20,1,1,	1.45,	8.5,	6.5,	9,		array(15, 16));
//		$this->actionAddOneCell(21,1,1,	1.45,	10,		6.5,	11,		array(15, 16));


//		$this->actionAddOneCell(1,1,1,	0,		0,	3.5,	4.2,	array(7));
//		$this->actionAddOneCell(2,1,1,	4.5,	0,	8.3,	4.5,	array(7));
//		$this->actionAddOneCell(3,1,1,	0,	4.2,	3.5,	8.5,	array(8));
//		$this->actionAddOneCell(4,1,1,	4.5,4.5,	8.3,	9,		array(8));
//		$this->actionAddOneCell(5,1,1,	0,	8.5,	3.5,	12.55,	array(9));
//		$this->actionAddOneCell(6,1,1,	4.5,	9,	8.3,	12.55,	array(9));
//
//		$this->actionAddOneCell(7,1,1,	3.5,1.6,	4.5,	2.6,	array(1,2,10,12));
//		$this->actionAddOneCell(8,1,1,	3.5,5.8,	4.5,	6.8,	array(3,4,10,11));
//		$this->actionAddOneCell(9,1,1,	3.5,10.1,	4.5,	11.1,	array(5,6,11,13));
//
//		$this->actionAddOneCell(10,1,1,	3.5,2.6,	4.5,	5.8,	array(7,8));
//		$this->actionAddOneCell(11,1,1,	3.5,6.8,	4.5,	10.1,	array(8,9));
//
//		$this->actionAddOneCell(12,1,1,	3.5,0,		4.5,	1.6,	array(7));
//		$this->actionAddOneCell(13,1,1,	3.5,11.1,	4.5,	12.55,	array(9));
	}

	public function actionTest(){
		//$cellList = Cell::find()->all();
		$aa = array();
		/*array_push($a,"aaa");
		array_push($a,"bbb");
		var_dump($a);
		echo "\n";
		$b = array_shift($a);
		var_dump($b);
		var_dump($a);

		$aa[1] = new Point();
		$aa[1]->x = 1;
		$aa[1]->y = 1;
		$aa[2]->x = 0;
		$aa[2]->y = 0;*/
		/*
		array_push($aa,new Point(2,0));
		array_push($aa,new Point(2,-4));
		array_push($aa,new Point(4,-4));
		array_push($aa,new Point(4,0));
		echo json_encode($aa);
		echo "<br />";
		echo "<br />";
		$a = [];
		array_push($a,4);

		$aa = [];
		array_push($aa,new _3DPoint(3,3,0));
		array_push($aa,new _3DPoint(4,3,1));
		array_push($aa,new _3DPoint(3,3,1));
		echo json_encode($aa);
		$cellList = Cell::find()->all();
		$ladderList = Ladder::find()->all();
		echo "<br>";
		echo "<br>";
		var_dump( $ladderList);
		echo "<br>";
		var_dump($cellList);*/
		$a = array();
		$a[4][6.8] = 0;
		var_dump($a);

	}

}