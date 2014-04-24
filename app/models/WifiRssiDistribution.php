<?php
/**
 * Created by PhpStorm.
 * User: songrenchu
 * Date: 24/4/14
 * Time: 10:47 AM
 */

class WifiRssiDistribution extends RModel {
	public $id, $roomId, $x, $y, $z, $bssid, $distribution;

	public static $table = "wifi_rssi_distribution";
	public static $primary_key = "id";
	public static $mapping = array(
		"id" => "id",
		"roomId" => "room_id",
		"x" => "x",
		"y" => "y",
		"z" => "z",
		"distribution" => "distribution",
	);

	/**
	 * Unpack ap list
	 */
	public function unpack() {
		if ($this->distribution !== null && !is_array($this->distribution)) {
			$this->distribution = json_decode($this->distribution);
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
} 