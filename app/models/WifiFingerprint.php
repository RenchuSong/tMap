<?php
/**
 * Created by PhpStorm.
 * User: songrenchu
 * Date: 20/4/14
 * Time: 7:59 PM
 */

class WifiFingerprint extends RModel {
	const EBSILON = 0.0001;

	public $id, $roomId, $x, $y, $z, $wifiData;

	public static $table = "wifi_fingerprint";
	public static $primary_key = "id";
	public static $mapping = array(
		"id" => "id",
		"roomId" => "room_id",
		"x" => "x",
		"y" => "y",
		"z" => "z",
		"wifiData" => "wifi_data",
	);

	/**
	 * Unpack wifi data
	 */
	public function unpack() {
		if ($this->wifiData !== null && !is_array($this->wifiData)) {
			$this->wifiData = json_decode($this->wifiData);
		}
	}

	/**
	 * Pack wifi data
	 */
	public function pack() {
		if (is_array($this->wifiData)) {
			$this->wifiData = json_encode($this->wifiData);
		}
	}

	/**
	 * Get wifi fingerprint point with x, y, z. Take float error into consideration, use ebsilon to restrict a range
	 */
	public static function getWifiFingerprintPoint($roomId, $x, $y, $z) {
		return WifiFingerprint::find("roomId", $roomId)
			->where("[x] > ?", $x - WifiFingerprint::EBSILON)
			->where("[x] < ?", $x + WifiFingerprint::EBSILON)
			->where("[y] > ?", $y - WifiFingerprint::EBSILON)
			->where("[y] < ?", $y + WifiFingerprint::EBSILON)
			->where("[z] > ?", $z - WifiFingerprint::EBSILON)
			->where("[z] < ?", $z + WifiFingerprint::EBSILON)
			->first();
	}
}
