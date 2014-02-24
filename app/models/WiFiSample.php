<?php
/**
 * Created by PhpStorm.
 * User: songrenchu
 * Date: 23/2/14
 * Time: 4:18 PM
 */

class WiFiSample extends RModel {
	public $id, $buildingId, $floor, $x, $y, $fingerPrintPack;
	public $bssiVector = array();								   //BSSI name => magnitude vector

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

	/**
	 * Destract wifi magnitude pair into vector
	 */
	private function unPackBSSIVector() {
		if ($this->fingerPrintPack !== null) {
			$this->bssiVector = json_decode($this->fingerPrintPack);
		}
	}

	/**
	 * Pack wifi magnitude vector into a string
	 */
	private function packBSSIVector() {
		$this->fingerPrintPack = json_encode($this->bssiVector);
	}

	/**
	 * Insert into database wifi sample from a transfered json object
	 * @param $jsonWifiSample
	 * @return WiFiSample
	 */
	public static function createWiFiSample($jsonWifiSample) {
		$sample = new WiFiSample();
		$sample->buildingId = $jsonWifiSample->buildingId;
		$sample->floor = $jsonWifiSample->floor;
		$sample->x = $jsonWifiSample->x;
		$sample->y = $jsonWifiSample->y;
		$sample->fingerPrintPack = $jsonWifiSample->fingerPrintPack;
		$sample->save();
		$sample->unPackBSSIVector();
		return $sample;
	}

	/**
	 * Get wifi sample from database and unpack BSSI string into vector
	 * @param $id
	 * @return null|WiFiSample
	 */
	public function getWiFiSample($id) {
		$sample = WiFiSample::get($id);
		if ($sample !== null) {
			$sample->unPackBSSIVector();
			return $sample;
		}
		return null;
	}
} 