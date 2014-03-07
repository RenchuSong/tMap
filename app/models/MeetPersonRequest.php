<?php
/**
 * Created by PhpStorm.
 * User: songrenchu
 * Date: 7/3/14
 * Time: 3:26 PM
 */

class MeetPersonRequest {
	public static $UN_PROCESSED = 1;
	public static $ACCEPTED = 2;
	public static $REJECTED = 3;
	public static $TIMEOUT = 4;

	public static $EXPIRE_TIME = 30;

	public $id, $senderPhoneNumber, $receiverPhoneNumber, $state, $startTime, $guidanceData;
	public static $table = "two_persons_request";
	public static $primary_key = "id";
	public static $mapping = array(
		"id" => "id",
		"senderPhoneNumber" => "phone_sender",
		"receiverPhoneNumber" => "phone_receiver",
		"state" => "request_state",
		"startTime" => "request_start_time",
		"guidanceData" => "request_result",
	);

	public static $protected = array("id");

//	public static function checkStatus($senderPhoneNumber) {
//		$request = MeetPersonRequest::where("[senderPhoneNumber] = ?", $senderPhoneNumber)->first();
//		if ($request != null)
//	}
//
//	public static function reject($receiverPhoneNumber) {
//		$
//	}
} 