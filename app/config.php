<?php
/**
 * Created by PhpStorm.
 * User: songrenchu
 * Date: 23/2/14
 * Time: 3:57 PM
 */

return array(
	'baseDir' => dirname(__FILE__),
	'basePath' => "/tMap",
	'name' => 'tMap',
	'layout' => 'index',
	'isCleanUrl' => true,
	'defaultController' => 'site',
	'defaultAction' => 'index',
	'exceptionAction' => 'site/exception',
	'debug' => true,

	'authProvider' => 'User',

	'db' => array(
		'host' => 'localhost',
		'user' => 'root',
		'password' => '8dxkc8x',
		'db_name' => 'tMap',
		'table_prefix' => '',
		'charset' => 'utf8',
	),

	'route' => array(
		'contact' => 'site/contact',
	),

	'cache' => array(
		'cache_dir' => '/cache',
		'cache_prefix' => "cache_",
		'cache_time' => 3600, //seconds
	)
);
