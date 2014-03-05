<?php
/**
 * Created by PhpStorm.
 * User: songrenchu
 * Date: 4/3/14
 * Time: 8:52 PM
 */

class BusinessController extends RController{
	public function actionIndex() {
		echo json_encode(array("response" => "Hello tMap!"));
	}

	public function actionAddBusinessItem($buildingId, $floor, $x, $y, $title, $type, $content, $imageURL) {
		$business = new BusinessItem();
		$business->createBusinessItem($buildingId, $floor, $x, $y, $title, $type, $content, $imageURL);
		$business->save();
		echo json_encode(array("response", "ok"));
	}

	/**
	 * get business items
	 */
	public function actionGetBusinessItem($buildingId, $floor, $type = null) {
		$searchText = "";
		if (Rays::isPost()) {
			if (isset($_POST['json'])) {
				$searchText = trim($_POST['json']);
			}
		}
		$businessList = BusinessItem::where("[buildingId] = ? ", $buildingId)
									->where("[floor] = ?", $floor);
		if ($type != null) {
			$businessList = $businessList->where("[type] = ?", $type);
		}

		if (trim($searchText) != "") {
			$businessList = $businessList->like("title", $searchText);
		}

		$businessList = $businessList->all();
		$businessListDiffFloor = BusinessItem::where("[buildingId] = ? ", $buildingId)
											->where("[floor] != ?", $floor);

		if ($type != null) {
			$businessListDiffFloor = $businessListDiffFloor->where("[type] = ?", $type);
		}

		if (trim($searchText) != "") {
			$businessListDiffFloor = $businessListDiffFloor->like("title", $searchText);
		}

		$businessListDiffFloor = $businessListDiffFloor->all();

		foreach ($businessListDiffFloor as $item) {
			array_push($businessList, $item);
		}

		echo json_encode($businessList);
	}


} 