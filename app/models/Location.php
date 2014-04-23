<?php
/**
 * Created by PhpStorm.
 * User: songrenchu
 * Date: 28/2/14
 * Time: 2:32 PM
 */

// TODO 老版本代码
class Location {
	public $buildingId, $floor, $x, $y;
	public $score;

	/**
	 * judge which location is better
	 */
	public function betterThan(Location $other) {
		return $this->score > $other->score;
	}
} 