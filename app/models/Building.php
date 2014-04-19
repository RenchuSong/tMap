<?php
/**
 * Created by PhpStorm.
 * User: songrenchu
 * Date: 19/4/14
 * Time: 4:11 PM
 */

class Building extends RModel{
	public $id, $latitude, $longitude, $rotate;
	public $attributes = null;							// Other attributes describing the building

	public static $table = "tmap_building";
	public static $primary_key = "id";
	public static $mapping = array(
		"id" => "id",
		"longitude" => "x",
		"latitude" => "y",
		"rotate" => "rotate",
		"attributes" => "attributes",
	);

	/**
	 * Unpack attributes
	 */
	public function unpackAttributes() {
		if ($this->attributes !== null && !is_array($this->attributes)) {
			$this->attributes = json_decode($this->attributes);
		}
	}

	/**
	 * Pack attributes
	 */
	public function packAttributes() {
		if (is_array($this->attributes)) {
			$this->attributes = json_encode($this->attributes);
		}
	}


} 