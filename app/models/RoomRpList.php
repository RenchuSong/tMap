<?php
/**
 * Created by PhpStorm.
 * User: songrenchu
 * Date: 25/4/14
 * Time: 3:20 PM
 */

class RoomRpList extends RModel{
	public $id, $roomId, $rpList;

	public static $table = "room_rp_list";
	public static $primary_key = "id";
	public static $mapping = array(
		"id" => "id",
		"roomId" => "room_id",
		"rpList" => "rp_list",
	);

	/**
	 * Unpack rp list
	 */
	public function unpack() {
		if ($this->rpList !== null && !is_array($this->rpList)) {
			$this->rpList = json_decode($this->rpList, true);
		}
	}

	/**
	 * Pack rp list
	 */
	public function pack() {
		if (is_array($this->rpList)) {
			$this->rpList = json_encode($this->rpList);
		}
	}
} 