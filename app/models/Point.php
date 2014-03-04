<?php
/**
 * Created by PhpStorm.
 * User: boboo92
 * Date: 14-2-28
 * Time: 下午8:26
 */

class Point {
	public $x, $y, $floor;
	public function Point($x,$y,$floor) {
		$this->x = $x;
		$this->y = $y;
		$this->floor = $floor;
	}
}