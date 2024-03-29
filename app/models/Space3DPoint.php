<?php
/**
 * Created by PhpStorm.
 * User: boboo92
 * Date: 14-3-3
 * Time: 下午7:50
 */

class Space3DPoint {
	const EBSILON = 0.0001;

	public $x, $y, $z;
	public function Space3DPoint($x,$y,$z) {
		$this->x = $x;
		$this->y = $y;
		$this->z = $z;
	}

	public function equalTo($other) {
		if (abs($this->x - $other->x) < Space3DPoint::EBSILON) {
			if (abs($this->y - $other->y) < Space3DPoint::EBSILON) {
				if (abs($this->z - $other->z) < Space3DPoint::EBSILON) {
					return true;
				}
			}
		}
		return false;
	}

	public function equalToValue($x, $y, $z) {
		if (abs($this->x - $x) < Space3DPoint::EBSILON) {
			if (abs($this->y - $y) < Space3DPoint::EBSILON) {
				if (abs($this->z - $z) < Space3DPoint::EBSILON) {
					return true;
				}
			}
		}
		return false;
	}

	public function distance($other) {
		return sqrt(
			($this->x - $other->x) * ($this->x - $other->x) +
			($this->y - $other->y) * ($this->y - $other->y) +
			($this->z - $other->z) * ($this->z - $other->z)
		);
	}
}