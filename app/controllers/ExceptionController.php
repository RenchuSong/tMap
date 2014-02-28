<?php
/**
 * Created by PhpStorm.
 * User: songrenchu
 * Date: 23/2/14
 * Time: 8:26 PM
 */

class ExceptionController  extends RController {

	public function actionException(RException $e){
		echo json_encode(array("exception" => $e->getMessage()));
	}

} 