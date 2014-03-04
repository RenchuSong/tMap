-- phpMyAdmin SQL Dump
-- version 4.1.5
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Feb 24, 2014 at 10:55 AM
-- Server version: 5.6.15
-- PHP Version: 5.4.17

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `tMap`
--

-- --------------------------------------------------------

--
-- Table structure for table `building_wifi_list`
--

CREATE TABLE IF NOT EXISTS `building_wifi_list` (
  `building_id` int(11) NOT NULL COMMENT '建筑id',
  `wifi_name_list` varchar(16384) NOT NULL COMMENT 'Wi-Fi名列表',
  PRIMARY KEY (`building_id`),
  UNIQUE KEY `building_id` (`building_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `wifi_map`
--

CREATE TABLE IF NOT EXISTS `wifi_map` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `wifi_building_id` int(11) NOT NULL COMMENT '建筑id',
  `wifi_floor` int(11) NOT NULL COMMENT '建筑楼层',
  `wifi_x` int(11) NOT NULL COMMENT '建筑X坐标',
  `wifi_y` int(11) NOT NULL COMMENT '建筑Y坐标',
  `wifi_digram` varchar(16384) NOT NULL COMMENT '强度图谱',
  `wifi_sample_times` int(11) DEFAULT '1' COMMENT '已经采样次数',
  PRIMARY KEY (`id`),
  KEY `wifi_building_id` (`wifi_building_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=24 ;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
