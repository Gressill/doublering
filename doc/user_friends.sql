-- phpMyAdmin SQL Dump
-- version 2.11.5
-- http://www.phpmyadmin.net
--
-- 主机: localhost
-- 生成日期: 2009 年 05 月 23 日 08:32
-- 服务器版本: 5.0.51
-- PHP 版本: 5.2.5

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";

--
-- 数据库: `doublering`
--

-- --------------------------------------------------------

--
-- 表的结构 `dr_user_friends`
--

CREATE TABLE IF NOT EXISTS `dr_user_friends` (
  `ufid` int(11) NOT NULL auto_increment,
  `userid` int(11) default '0',
  `friendid` int(11) default '0',
  PRIMARY KEY  (`ufid`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COMMENT='用户关系表' AUTO_INCREMENT=1 ;

--
-- 导出表中的数据 `dr_user_friends`
--

