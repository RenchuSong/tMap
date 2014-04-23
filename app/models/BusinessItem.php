<?php
/**
 * Created by PhpStorm.
 * User: songrenchu
 * Date: 3/3/14
 * Time: 11:46 PM
 */

// TODO 老版本代码
class BusinessItem extends RModel{
	public $id, $buildingId, $floor, $x, $y, $title, $type, $content, $imageURL, $extra;

	public static $table = "business_item";
	public static $primary_key = "id";
	public static $mapping = array(
		"id" => "id",
		"buildingId" => "building_id",
		"floor" => "floor",
		"x" => "x",
		"y" => "y",
		"title" => "title",
		"type" => "item_type",
		"content" => "content",
		"imageURL" => "image_url",
		"extra" => "extra",
	);

	public function createBusinessItem($buildingId, $floor, $x, $y, $title, $type, $content, $imageURL) {
		$this->buildingId = $buildingId;
		$this->floor = $floor;
		$this->x = $x;
		$this->y = $y;
		$this->title = $title;
		$this->type = $type;
		$this->content = $content;
		$this->imageURL = $imageURL;
	}
} 