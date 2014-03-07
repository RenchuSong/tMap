<?php
/**
 * Created by PhpStorm.
 * User: songrenchu
 * Date: 23/2/14
 * Time: 4:18 PM
 */

require_once(dirname(__FILE__) . '/../util/Util.php');

class WiFiSample extends RModel {
	public $id, $buildingId, $floor, $x, $y, $fingerPrintPack, $wifiSampleTime;
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
		"wifiSampleTime" => "wifi_sample_times",
	);

	public static $protected = array("id");

	/**
	 * Destract wifi magnitude pair into vector
	 */
	public function unPackBSSIVector() {
		if ($this->fingerPrintPack !== null) {
			$this->bssiVector = json_decode($this->fingerPrintPack);
			if (!is_array($this->bssiVector)) {
				$this->bssiVector = o2a($this->bssiVector);
			}
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

//		$sample = WiFiSample::where("[buildingId] = ?", $jsonWifiSample->buildingId)
//							->where("[floor] = ?", $jsonWifiSample->floor)
//							->where("[x] = ?", $jsonWifiSample->x)
//							->where("[y] = ?", $jsonWifiSample->y)->first();
//
//		if ($sample !== null) {
//			$sample->unPackBSSIVector();
//			$sample2 = new WiFiSample();
//			$sample2->fingerPrintPack = $jsonWifiSample->fingerPrintPack;
//			$sample2->unPackBSSIVector();
//			$wifiList = array_merge(array_keys($sample->bssiVector), array_keys($sample2->bssiVector));
//			foreach ($wifiList as $wifiName) {
//				$newRssi = (isset($sample->bssiVector[$wifiName]) ?
//							$sample->bssiVector[$wifiName]: 0) *
//							$sample->wifiSampleTime +
//							(isset($sample2->bssiVector[$wifiName]) ?
//								$sample2->bssiVector[$wifiName]: 0);
//				$sample->bssiVector[$wifiName] = $newRssi / ($sample->wifiSampleTime + 1);
//			}
//			$sample->wifiSampleTime = $sample->wifiSampleTime + 1;
//			$sample->packBSSIVector();
//		} else {
			$sample = new WiFiSample();
			$sample->buildingId = $jsonWifiSample->buildingId;
			$sample->floor = $jsonWifiSample->floor;
			$sample->x = $jsonWifiSample->x;
			$sample->y = $jsonWifiSample->y;
			$sample->fingerPrintPack = $jsonWifiSample->fingerPrintPack;
			$sample->wifiSampleTime = 1;
		//}
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