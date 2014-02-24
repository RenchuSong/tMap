<?php
/**
 * Created by PhpStorm.
 * User: songrenchu
 * Date: 24/2/14
 * Time: 11:11 AM
 */

class BuildingWifiList extends RModel {
	public $buildingId, $wifiPack;
	public $wifiList = array();								   // Wifi name list that can be received within the building

	public static $table = "building_wifi_list";
	public static $primary_key = "building_id";
	public static $mapping = array(
		"buildingId" => "building_id",
		"wifiPack" => "wifi_name_list",
	);

	/**
	 * Destract wifi name list into vector
	 */
	private function unPackWifiList() {
		if ($this->wifiPack !== null) {
			$this->wifiList = json_decode($this->wifiPack);
		}
	}

	/**
	 * Pack wifi name list vector into a string
	 */
	private function packWifiList() {
		$this->wifiPack = json_encode($this->wifiList);
	}

	/**
	 * Updata building Wifi list from existing wifi sample collection
	 * @param $jsonWifiSample
	 * @return WiFiSample
	 */
	public function buildingUpdateWifi() {
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
	 * Get building wifi list from database and unpack Wifi list string into vector
	 * @param $id
	 * @return null|WiFiSample
	 */
	public function getBuildingWiFiList($buildingId) {
		$buildingWifi = BuildingWifiList::get($buildingId);
		if ($buildingWifi !== null) {
			$buildingWifi->unPackWifiList();
			return $buildingWifi;
		}
		return null;
	}
} 