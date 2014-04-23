<?php
/**
 * Created by PhpStorm.
 * User: songrenchu
 * Date: 7/3/14
 * Time: 2:57 PM
 */

// TODO 老版本代码
class UserLog extends RModel{
	public $id, $userPhone, $buildingId, $floor, $x, $y;

	public static $table = "user_log";
	public static $primary_key = "id";
	public static $mapping = array(
		"id" => "id",
		"userPhone" => "user_phone",
		"buildingId" => "user_building_id",
		"floor" => "user_floor",
		"x" => "x",
		"y" => "y",
	);

	public static $protected = array("id");

	public static function updateUserLog($userPhone, $buildingId, $floor, $x, $y) {
		$userLog = UserLog::where("[userPhone] = ?", $userPhone)->first();
		if ($userLog == null) {
			$userLog = new UserLog();
		}
		$userLog->userPhone = $userPhone;
		$userLog->buildingId = $buildingId;
		$userLog->floor = $floor;
		$userLog->x = $x;
		$userLog->y = $y;
		$userLog->save();
		return $userLog;
	}


} 