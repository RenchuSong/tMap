<?php
/**
 * Created by PhpStorm.
 * User: songrenchu
 * Date: 25/4/14
 * Time: 2:29 PM
 */

class RpApList extends RModel {
	const EBSILON = 0.0001;

	public $id, $roomId, $x, $y, $z, $apList;

	public static $table = "rp_ap_list";
	public static $primary_key = "id";
	public static $mapping = array(
		"id" => "id",
		"roomId" => "room_id",
		'x' => "x",
		'y' => "y",
		'z' => "z",
		"apList" => "ap_list",
	);

	/**
	 * Unpack ap list
	 */
	public function unpack() {
		if ($this->apList !== null && !is_array($this->apList)) {
			$this->apList = json_decode($this->apList);
		}
	}

	/**
	 * Pack ap list
	 */
	public function pack() {
		if (is_array($this->apList)) {
			$this->apList = json_encode($this->apList);
		}
	}

	/**
	 * Get wifi fingerprint point with x, y, z. Take float error into consideration, use ebsilon to restrict a range
	 */
	public static function getRpApList($roomId, $x, $y, $z) {
		return RpApList::find("roomId", $roomId)
			->where("[x] > ?", $x - RpApList::EBSILON)
			->where("[x] < ?", $x + RpApList::EBSILON)
			->where("[y] > ?", $y - RpApList::EBSILON)
			->where("[y] < ?", $y + RpApList::EBSILON)
			->where("[z] > ?", $z - RpApList::EBSILON)
			->where("[z] < ?", $z + RpApList::EBSILON)
			->first();
	}
} 