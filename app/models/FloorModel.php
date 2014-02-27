<?php
/**
 * Created by PhpStorm.
 * User: songrenchu
 * Date: 27/2/14
 * Time: 5:09 PM
 */

class FloorModel extends RModel{
	public $id, $buildingId, $floorId, $modelPack;

	public static $table = "floor_model";
	public static $primary_key = "id";
	public static $mapping = array(
		"id" => "id",
		"buildingId" => "floor_building_id",
		"floorId" => "floor_floor_id",
		"modelPack" => "floor_model"
	);

} 