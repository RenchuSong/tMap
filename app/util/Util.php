<?php
/**
 * Created by PhpStorm.
 * User: songrenchu
 * Date: 24/2/14
 * Time: 1:22 PM
 */

function a2o(Array $array, $obj, array $except = array()) {
	$except = array_combine($except, $except);
	foreach ($array as $key => $value) {
		if (!isset($except[$key])) {
			$obj->{$key} = $value;
		}
	}
	return $obj;
}

function o2a($obj, array $except = array()) {
	$array = array();
	$except = array_combine($except, $except);
	foreach ($obj as $key => $value) {
		if (!isset($except[$key])) {
			$array[$key] = $value;
		}
	}
	return $array;
}


function cosDistance(array $arr1, array $arr2) {
	$dist1 = 0;
	foreach ($arr1 as $item) {
		$dist1 += $item * $item;
	}
	$dist2 = 0;
	foreach ($arr2 as $item) {
		$dist2 += $item * $item;
	}

	if ($dist1 == 0 || $dist2 == 0) {
		return -1;
	}

	$dist3 = 0;
	foreach ($arr1 as $key => $value) {
		if (isset($arr2[$key])) {
			$dist3 += $value * $arr2[$key];
		}
	}

	return $dist3 / sqrt($dist1 * $dist2);
}

function euclideanDistance(array $arr1, array $arr2) {
	$arr3 = array_merge($arr1, $arr2);
	$result = 0;
	foreach ($arr3 as $key => $value) {
		if (isset($arr1[$key]) && isset($arr2[$key])) {
			$result += ($arr1[$key] - $arr2[$key]) * ($arr1[$key] - $arr2[$key]);
		} else if (!isset($arr1[$key])) {
			$result += $arr2[$key] * $arr2[$key];
		} else if (!isset($arr2[$key])) {
			$result += $arr1[$key] * $arr1[$key];
		}
	}

	return sqrt($result);
}

function weighedDistance(array $arr1, array $arr2) {
	$arr3 = array_intersect(array_keys($arr1), array_keys($arr2));
	$result = 0;
	foreach ($arr3 as $key) {
		var_dump(abs($arr1[$key] - $arr2[$key]) / abs($arr1[$key] + $arr2[$key]));
		$result += 1 - abs($arr1[$key] - $arr2[$key]) / abs($arr1[$key] + $arr2[$key]);
	}
	return $result / count(array_merge($arr1, $arr2));
}

function setDistance(array $arr1, array $arr2) {
	return count(array_intersect(array_keys($arr1), array_keys($arr2))) / count(array_merge($arr1, $arr2));
}