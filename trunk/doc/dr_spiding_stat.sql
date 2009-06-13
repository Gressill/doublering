-- phpMyAdmin SQL Dump
-- version 2.11.5
-- http://www.phpmyadmin.net
--
-- 主机: localhost
-- 生成日期: 2009 年 06 月 13 日 09:24
-- 服务器版本: 5.0.51
-- PHP 版本: 5.2.5

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";

--
-- 数据库: `doublering`
--

-- --------------------------------------------------------

--
-- 表的结构 `dr_spiding_stat`
--

CREATE TABLE IF NOT EXISTS `dr_spiding_stat` (
  `id` int(11) NOT NULL auto_increment,
  `virtualmachine` varchar(30) NOT NULL,
  `type` varchar(40) NOT NULL,
  `current_position` int(11) NOT NULL,
  `from` int(11) NOT NULL,
  `to` int(11) NOT NULL,
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8 COMMENT='抓取状态表' AUTO_INCREMENT=3 ;

--
-- 导出表中的数据 `dr_spiding_stat`
--

INSERT INTO `dr_spiding_stat` (`id`, `virtualmachine`, `type`, `current_position`, `from`, `to`) VALUES
(1, 'zf1', 'peoplefriends', 1000150, 1000001, 2000000),
(2, 'zf1', 'peoplecollection', 1000030, 1000001, 2000000);
