<?php
/**
 * Created by PhpStorm.
 * User: boboo92
 * Date: 14-2-26
 * Time: 下午11:13
 */

class Ladder extends RModel {
	public $id, $shapePack, $lowCellId, $highCellId, $buildingId, $lowPoint, $highPoint;
	public $verticeList = array();

	public static $table = "ladder";
	public static $primary_key = "id";
	public static $mapping = array(
		"id" => "id",
		"shapePack" => "ladder_shape",
		"lowCellId" => "ladder_low_cell_id",
		"highCellId" => "ladder_high_cell_id",
		"buildingId" => "ladder_building_id",
	);

	/**
	 * Destract wifi name list into vector
	 */
	private function unPack() {
		if ($this->shapePack !== null) {
			$this->verticeList = json_decode($this->shapePack);
			//var_dump($this->verticeList[0]);
			$this->lowPoint = $this->verticeList[0];
			$this->highPoint = $this->verticeList[count($this->verticeList)-1];
		}
	}

	/**
	 * Pack wifi name list vector into a string
	 */
	private function pack() {
		$this->shapePack = json_encode($this->verticeList);
	}

	/**
	 * get the length of ladder
	 */
	public  function getLength() {
		$this->unPack();
		$distance = 0;
		for ($i = 1; $i < count($this->verticeList); $i++) {
			$distance += BaseStaticFun::getDistanceWithDeltaZ($this->verticeList[$i-1]->x,$this->verticeList[$i-1]->y,$this->verticeList[$i]->x,$this->verticeList[$i]->y,$this->verticeList[$i]->z);
		}
		return $distance;
	}
} 