<?php
/**
 * Created by PhpStorm.
 * User: songrenchu
 * Date: 23/4/14
 * Time: 11:17 AM
 */

class RoomApList extends RModel{
	public $id, $roomId, $apList;

	public static $table = "room_ap_list";
	public static $primary_key = "id";
	public static $mapping = array(
		"id" => "id",
		"roomId" => "room_id",
		"apList" => "ap_list",
	);

	/**
	 * Unpack ap list
	 */
	public function unpack() {
		if ($this->apList !== null && !is_array($this->apList)) {
			$this->apList = json_decode($this->apList, true);
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