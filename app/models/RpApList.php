<?php
/**
 * Created by PhpStorm.
 * User: songrenchu
 * Date: 25/4/14
 * Time: 2:29 PM
 */

class RpApList extends RModel{
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
} 