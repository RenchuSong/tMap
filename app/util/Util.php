<?php
/**
 * Created by PhpStorm.
 * User: songrenchu
 * Date: 24/2/14
 * Time: 1:22 PM
 */

function a2o(Array $array, $obj, array $except = []) {
	$except = array_combine($except, $except);
	foreach ($array as $key => $value) {
		if (!isset($except[$key])) {
			$obj->{$key} = $value;
		}
	}
	return $obj;
}

function o2a($obj, array $except = []) {
	$array = [];
	$except = array_combine($except, $except);
	foreach ($obj as $key => $value) {
		if (!isset($except[$key])) {
			$array[$key] = $value;
		}
	}
	return $array;
}
