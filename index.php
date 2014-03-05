<?php
/**
 * Created by PhpStorm.
 * User: songrenchu
 * Date: 23/2/14
 * Time: 3:54 PM
 */

$Rays = dirname(__FILE__).'/Rays/Rays/Rays.php';
$config = dirname(__FILE__).'/app/config.php';

require_once($Rays);

Rays::newApp($config)->run();