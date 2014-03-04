<?php
/**
 * Created by PhpStorm.
 * User: boboo92
 * Date: 14-2-28
 * Time: 下午8:43
 */

class BaseStaticFun {
	public  static function  getDistance($x0 , $y0, $x, $y) {
		return sqrt(($x0 - $x)*($x0 - $x) + ($y0 - $y)*($y0 - $y));
	}

	public  static function  getDistanceWithDeltaZ($x0 , $y0, $x, $y, $z) {
		return sqrt(($x0 - $x)*($x0 - $x) + ($y0 - $y)*($y0 - $y) + $z*$z);
	}
} 