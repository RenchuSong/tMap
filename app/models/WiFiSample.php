<?php
/**
 * Created by PhpStorm.
 * User: songrenchu
 * Date: 23/2/14
 * Time: 4:18 PM
 */

class WiFiSample extends RModel {
	public $id, $buildingId, $floor, $x, $y, $fingerPrintPack;
	public $bssiVector = array();								   //RSSI键值对数组

	public static $table = "wifi_map";
	public static $primary_key = "id";
	public static $mapping = array(
		"id" => "id",
		"buildingId" => "wifi_building_id",
		"floor" => "wifi_floor",
		"x" => "wifi_x",
		"y" => "wifi_y",
		"fingerPrintPack" => "wifi_digram",
	);

	public static $protected = array("id");

	private function unPackBSSIVector() {
		if ($this->fingerPrintPack !== null) {
			$this->rssiVector = json_decode($this->fingerPrintPack);
		}
	}

	private function packBSSIVector() {
		$this->fingerPrintPack = json_encode($this->rssiVector);
	}

	public static function createWiFiSample($buildingId, $floor, $x, $y, $bssiVector) {
		$sample = new WiFiSample();
		$sample->buildingId = $buildingId;
		$sample->floor = $floor;
		$sample->x = $x;
		$sample->y = $y;
		$sample->bssiVector = $bssiVector;
		$sample->packBSSIVector();
		$sample->save();
		return $sample;
	}

	public function getWiFiSample($id) {
		$sample = WiFiSample::get($id);
		if ($sample) {
			$sample->unPackBSSIVector();
			return $sample;
		}
		return null;
	}
} 