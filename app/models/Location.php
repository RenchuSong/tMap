<?php
/**
 * Created by PhpStorm.
 * User: songrenchu
 * Date: 28/2/14
 * Time: 2:32 PM
 */

class Location {
	const MINIMAL_SCORE = -1e100;

	public $buildingId, $roomId, $x, $y, $z;
	public $score;

	public function Location($buildingId, $roomId, $x, $y, $z) {
		$this->buildingId = $buildingId;
		$this->roomId = $roomId;
		$this->x = $x;
		$this->y = $y;
		$this->z = $z;
	}
	/**
	 * judge which location is better
	 */
	public function betterThan(Location $other) {
		return $this->score > $other->score;
	}
} 