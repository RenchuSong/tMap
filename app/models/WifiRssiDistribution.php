<?php
/**
 * Created by PhpStorm.
 * User: songrenchu
 * Date: 24/4/14
 * Time: 10:47 AM
 */

class WifiRssiDistribution extends RModel {
	const EBSILON = 0.0001;

	public $id, $roomId, $x, $y, $z, $bssid, $distribution;

	public static $table = "wifi_rssi_distribution";
	public static $primary_key = "id";
	public static $mapping = array(
		"id" => "id",
		"roomId" => "room_id",
		"x" => "x",
		"y" => "y",
		"z" => "z",
		"bssid" => "bssid",
		"distribution" => "distribution",
	);

	/**
	 * Unpack ap list
	 */
	public function unpack() {
		if ($this->distribution !== null && !is_array($this->distribution)) {
			$this->distribution = json_decode($this->distribution, true);
		}
	}

	/**
	 * Pack ap list
	 */
	public function pack() {
		if (is_array($this->distribution)) {
			$this->distribution = json_encode($this->distribution);
		}
	}

	/**
	 * Get drawer of the rssi value
	 */
	public static function standardRssi($rssi) {
		return $rssi;
	}

	/**
	 * Get Wifi Rssi Distribution of an RP
	 */
	public static function getWifiRssiDistribution($roomId, $x, $y, $z) {
		return WifiRssiDistribution::find("roomId", $roomId)
			->where("[x] > ?", $x - WifiRssiDistribution::EBSILON)
			->where("[x] < ?", $x + WifiRssiDistribution::EBSILON)
			->where("[y] > ?", $y - WifiRssiDistribution::EBSILON)
			->where("[y] < ?", $y + WifiRssiDistribution::EBSILON)
			->where("[z] > ?", $z - WifiRssiDistribution::EBSILON)
			->where("[z] < ?", $z + WifiRssiDistribution::EBSILON)
			->all();
	}
} 