<?php
/**
 * Created by PhpStorm.
 * User: songrenchu
 * Date: 19/4/14
 * Time: 4:59 PM
 */

// New Version
class Room extends RModel{
	public $id, $buildingId, $floor, $rotate, $x, $y, $model;
	public $attributes = null, $boundary = null;

	public static $table = "room";
	public static $primary_key = "id";
	public static $mapping = array(
		"id" => "id",
		"buildingId" => "building_id",
		"floor" => "floor_number",
		"rotate" => "rotate",
		"x" => "x",
		"y" => "y",
		"attributes" => "attributes",
		"model" => "model",
		"boundary" => "boundary",
	);

	/**
	 * Unpack
	 */
	public function unpack() {
		if ($this->attributes !== null && !is_array($this->attributes)) {
			$this->attributes = json_decode($this->attributes);
		}
		if ($this->boundary !== null && !is_array($this->boundary)) {
			$this->boundary = json_decode($this->boundary);
		}
	}

	/**
	 * Pack
	 */
	public function pack() {
		if (is_array($this->attributes)) {
			$this->attributes = json_encode($this->attributes);
		}
		if (is_array($this->boundary)) {
			$this->boundary = json_encode($this->boundary);
		}
	}

} 