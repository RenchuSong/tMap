<?php
/**
 * Created by PhpStorm.
 * User: songrenchu
 * Date: 23/4/14
 * Time: 8:29 PM
 */

// This Controller is to conduct experiments
class ExperimentController extends RController{
	public function actionGetApList($roomId, $x, $y, $z, $from = null, $to = null) {
		$wifiFingerPrint = WifiFingerprint::find("roomId", $roomId);
		if ($x !== null) {
			$wifiFingerPrint = $wifiFingerPrint->where("[x] < ?", $x + WifiFingerprint::EBSILON)->where("[x] > ?", $x - WifiFingerprint::EBSILON);
		}
		if ($y !== null) {
			$wifiFingerPrint = $wifiFingerPrint->where("[y] < ?", $y + WifiFingerprint::EBSILON)->where("[y] > ?", $y - WifiFingerprint::EBSILON);
		}
		if ($z !== null) {
			$wifiFingerPrint = $wifiFingerPrint->where("[z] < ?", $z + WifiFingerprint::EBSILON)->where("[z] > ?", $z - WifiFingerprint::EBSILON);
		}
		$wifiFingerPrint = $wifiFingerPrint->all();
		$apList = array();
		if ($from === null) {
			$from = 0;
		}
		foreach ($wifiFingerPrint as $point) {
			$point->unpack();
			if ($to === null) {
				$to = count($point->wifiData);
			}
			if ($to > count($point->wifiData)) {
				$to = count($point->wifiData);
			}
			for ($i = $from; $i < $to; ++$i) {
				$wifiScanItem = $point->wifiData[$i];
				foreach ($wifiScanItem as $bssidrssiPair) {
					if (!isset($apList[$bssidrssiPair->bssid])) {
						$apList[$bssidrssiPair->bssid] = 1;
					} else {
						++$apList[$bssidrssiPair->bssid];
					}
				}
			}
			foreach ($apList as $key => $value) {
				$apList[$key] /= $to - $from;
			}
		}
		rsort($apList);
		echo json_encode($apList);
	}

	public function actionWifiNumber($roomId, $x, $y, $z) {
		$wifi = WifiFingerprint::getWifiFingerprintPoint($roomId, $x, $y, $z);
		$wifi->unpack();
		echo count($wifi->wifiData);
	}
} 