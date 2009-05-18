-- phpMyAdmin SQL Dump
-- version 2.11.5
-- http://www.phpmyadmin.net
--
-- 主机: localhost
-- 生成日期: 2009 年 05 月 18 日 13:44
-- 服务器版本: 5.0.51
-- PHP 版本: 5.2.5

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";

--
-- 数据库: `doublering`
--

-- --------------------------------------------------------

--
-- 表的结构 `dr_user`
--

CREATE TABLE IF NOT EXISTS `dr_user` (
  `id` int(11) NOT NULL auto_increment,
  `uid` varchar(20) default NULL,
  `title` varchar(40) default NULL,
  `douban_id` int(11) default NULL,
  `douban_link` varchar(128) default NULL,
  `location_id` varchar(20) default NULL,
  `location` varchar(20) default NULL,
  `content` text NOT NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `uid_2` (`uid`),
  UNIQUE KEY `douban_id` (`douban_id`),
  KEY `uid` (`uid`)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8 AUTO_INCREMENT=226 ;
