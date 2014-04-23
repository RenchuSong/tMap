<?php
/**
 * Created by PhpStorm.
 * User: songrenchu
 * Date: 8/3/14
 * Time: 9:12 PM
 */

// TODO 老版本代码
class RSSIDistribution extends RModel{
	public $id, $buildingId, $floor, $x, $y, $bssid, $distributionPack;
	public $distribution = array();	// 0 - 1, 2 - 3, 4 - 5, ..., 108 - 109

	public static $table = "wifi_rssi_distribution";
	public static $primary_key = "id";
	public static $mapping = array(
		"id" => "id",
		"buildingId" => "building_id",
		"floor" => "floor",
		"x" => "x",
		"y" => "y",
		"bssid" => "bssid",
		"distributionPack" => "distribution",
	);

	public function unPackDistribution() {
		if ($this->distributionPack !== null) {
			$this->distribution = json_decode($this->distributionPack);
			if (!is_array($this->distribution)) {
				$this->distribution = o2a($this->distribution);
			}
		}
	}

	/**
	 * Pack wifi rssi vector into a string
	 */
	private function packDistribution() {
		$this->distributionPack = json_encode($this->distribution);
	}

	public static function getRSSIDistribution($buildingId, $floor, $x, $y) {
		$distribution = array();

		// all samples of a point
		$all = WiFiSample::find("buildingId", $buildingId)
						->where("[floor] = ?", $floor)
						->where("[x] = ?", $x)->where("[y] = ?", $y)->all();

		// all wifis of a floor
		$wifiList = BuildingWifiList::getBuildingWiFiList($buildingId, $floor);
		$wifiList->unPackWifiList();

		$times = 0;
		foreach ($wifiList->wifiList as $ap) {		// each ap mac
			$distribution = RSSIDistribution::find("buildingId", $buildingId)
											->where("[floor] = ?", $floor)
											->where("[x] = ?", $x)
											->where("[y] = ?", $y)
											->where("[bssid] = ?", $ap)->first();
			if ($distribution == null) {
				$distribution = new RSSIDistribution();
				$distribution->buildingId = $buildingId;
				$distribution->floor = $floor;
				$distribution->x = $x;
				$distribution->y = $y;
				$distribution->bssid = $ap;
			}

			for ($i = 0; $i < 110; $i += 2) {
				$distribution->distribution[$i] = 0;
			}
			foreach ($all as $sample) {
				$sample->unPackBSSIVector();
				//print_r($sample->bssiVector);
				if (isset($sample->bssiVector[$ap])) {
					$rssi = -$sample->bssiVector[$ap];
					$rssi = $rssi - ($rssi % 2);
				} else {
					$rssi = 0;
				}
				++$distribution->distribution[$rssi];
			}
			for ($i = 0; $i < 110; $i += 2) {
				$distribution->distribution[$i] /= count($all);
			}
			$distribution->packDistribution();
			$distribution->save();
			++$times;
		}
		echo $times;
		return true;
	}
} 