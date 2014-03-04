<?php
/**
 * Created by PhpStorm.
 * User: songrenchu
 * Date: 3/3/14
 * Time: 11:46 PM
 */

class BusinessItem extends RModel{
	public $id, $buildingId, $floor, $x, $y, $title, $content, $imageURL, $extra;

	public static $table = "business_item";
	public static $primary_key = "id";
	public static $mapping = array(
		"id" => "id",
		"buildingId" => "wifi_building_id",
		"floor" => "wifi_floor",
		"x" => "wifi_x",
		"y" => "wifi_y",
		"title" => "title",
		"content" => "content",
		"imageURL" => "image_url",
		"extra" => "extra",
	);


} 