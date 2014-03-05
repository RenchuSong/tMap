<?php
/**
 * Created by PhpStorm.
 * User: boboo
 * Date: 26/2/14
 * Time: 10:11 PM
 */

class Cell extends RModel {
	public $id, $buildingId, $floorId, $shapePack, $adjacentPack, $minX, $minY, $maxX, $maxY;
	public $verticeList = array();
	public $adjacentCellList = array();

	public static $table = "cell";
	public static $primary_key = "id";
	public static $mapping = array(
		"id" => "id",
		"buildingId" => "cell_building_id",
		"floorId" => "cell_floor_id",
		"shapePack" => "cell_shape",
		"adjacentPack" => "cell_adjacent",
	);
/*
	public function Cell($temp) {
		$this->id = $temp->id;
		$this->buildingId = $temp->buildingId;
		$this->floorId = $temp->floorId;
		$this->shapePack = $temp->shapePack;
		$this->adjacentPack = $temp->adjacentPack;

		$this->unPack();
	}
*/
	/**
	 * Destract wifi name list into vector
	 */
	private function unPack() {
		if ($this->shapePack !== null) {
			$this->verticeList = json_decode($this->shapePack);
		}
		if ($this->adjacentPack !== null) {
			$this->adjacentCellList = json_decode($this->adjacentPack);
		}
	}

	/**
	 * Pack wifi name list vector into a string
	 */
	private function pack() {
		$this->shapePack = json_encode($this->verticeList);
		$this->adjacentPack = json_encode($this->adjacentCellList);
	}


	/**
	 * check the point is inside the cell or not
	 */
	public function inside($floor, $x, $y) {
		if ($this->floorId != $floor) {
			return false;
		}
		/** todo */
		$this->unpack();
		$this->minX = 655360000;
		$this->maxX = 0;
		$this->minY = 655360000;
		$this->maxY = 0;
		foreach ($this->verticeList as $vertex) {
			if ($vertex->x<$this->minX) $this->minX = $vertex->x;
			if ($vertex->x>$this->maxX) $this->maxX = $vertex->x;
			if ($vertex->y<$this->minY) $this->minY = $vertex->y;
			if ($vertex->y>$this->maxY) $this->maxY = $vertex->y;
		}
		if ($this->minX<=$x && $x<=$this->maxX && $this->minY<=$y && $y<=$this->maxY) return true;
		return false;
	}

	public static function getCellList( $buildingId) {
		$tempList = Cell::find("buildingId", $buildingId)->all();
		if ($tempList == null) return null;
		$cellList = array();
		foreach ($tempList as $temp) {
			$cell = new Cell($temp);
			array_push($cellList, $cell);
		}
		return $cellList;
	}

	public function distance($floor, $x, $y) {
		if ($floor !== $this->floorId) {
			return 1e100;
		}

		return BaseStaticFun::getDistance(($this->minX + $this->maxX) / 2, ($this->minY + $this->maxY) / 2, $x, $y);
	}
} 