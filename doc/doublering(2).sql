-- phpMyAdmin SQL Dump
-- version 2.11.5
-- http://www.phpmyadmin.net
--
-- 主机: localhost
-- 生成日期: 2009 年 05 月 24 日 14:08
-- 服务器版本: 5.0.51
-- PHP 版本: 5.2.5

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";

--
-- 数据库: `doublering`
--

-- --------------------------------------------------------

--
-- 表的结构 `dr_book`
--

DROP TABLE IF EXISTS `dr_book`;
CREATE TABLE IF NOT EXISTS `dr_book` (
  `bid` int(11) NOT NULL auto_increment,
  `title` varchar(120) default NULL COMMENT '标题',
  `douban_id` int(11) default NULL COMMENT '豆瓣id',
  `douban_link` varchar(128) default NULL COMMENT '豆瓣链接',
  `author` varchar(60) default NULL,
  `summary` text COMMENT '摘要',
  `price` varchar(20) default NULL,
  `publisher` varchar(80) default NULL,
  `binding` varchar(80) default NULL,
  `pubdate` varchar(10) default NULL,
  `isbn10` varchar(10) default NULL COMMENT '10位isdn',
  `isbn13` varchar(13) default NULL COMMENT '13位isdn',
  PRIMARY KEY  (`bid`),
  UNIQUE KEY `douban_id` (`douban_id`)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8 COMMENT='书籍信息表' AUTO_INCREMENT=8038 ;

-- --------------------------------------------------------

--
-- 表的结构 `dr_movie`
--

DROP TABLE IF EXISTS `dr_movie`;
CREATE TABLE IF NOT EXISTS `dr_movie` (
  `mid` int(11) NOT NULL auto_increment,
  `title` varchar(120) default NULL,
  `author` varchar(80) default NULL,
  `douban_id` int(11) default '0',
  `douban_link` varchar(128) default NULL,
  `summary` text,
  `country` varchar(10) default NULL,
  `director` varchar(80) default NULL,
  `language` varchar(80) default NULL,
  `cast` varchar(80) default NULL,
  `pubdate` varchar(10) default NULL,
  `imdb` varchar(128) default NULL,
  `aka` varchar(80) NOT NULL,
  `minrate` int(3) default '0',
  `maxrate` int(3) default '0',
  `ratenum` int(5) default '0',
  `avgrate` float NOT NULL default '0',
  PRIMARY KEY  (`mid`),
  UNIQUE KEY `douban_id` (`douban_id`)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8 COMMENT='movie表' AUTO_INCREMENT=9 ;

-- --------------------------------------------------------

--
-- 表的结构 `dr_music`
--

DROP TABLE IF EXISTS `dr_music`;
CREATE TABLE IF NOT EXISTS `dr_music` (
  `mid` int(11) NOT NULL auto_increment,
  `title` varchar(80) default NULL,
  `author` varchar(80) default NULL,
  `douban_id` int(11) default '0',
  `douban_link` varchar(120) default NULL,
  `summary` text,
  `discs` varchar(10) default NULL,
  `ean` varchar(80) default NULL,
  `tracks` text,
  `singer` varchar(80) default NULL,
  `pubdate` varchar(10) default NULL,
  `publisher` varchar(80) default NULL,
  `media` varchar(40) default NULL,
  `aka` varchar(80) default NULL,
  PRIMARY KEY  (`mid`),
  UNIQUE KEY `douban_id` (`douban_id`)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8 COMMENT='music音乐表' AUTO_INCREMENT=80 ;

-- --------------------------------------------------------

--
-- 表的结构 `dr_subject_rating`
--

DROP TABLE IF EXISTS `dr_subject_rating`;
CREATE TABLE IF NOT EXISTS `dr_subject_rating` (
  `rid` int(11) NOT NULL auto_increment,
  `minrate` int(3) default '0',
  `maxrate` int(3) default '0',
  `avgrate` float default '0',
  `ratenum` int(5) default '0',
  `target_id` int(11) default '0',
  PRIMARY KEY  (`rid`),
  UNIQUE KEY `target_id` (`target_id`)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8 COMMENT='subject的评分表' AUTO_INCREMENT=3254 ;

-- --------------------------------------------------------

--
-- 表的结构 `dr_subject_tag`
--

DROP TABLE IF EXISTS `dr_subject_tag`;
CREATE TABLE IF NOT EXISTS `dr_subject_tag` (
  `tid` int(11) NOT NULL auto_increment COMMENT 'tid',
  `tag` varchar(40) default NULL COMMENT 'tag名字',
  `count` int(5) default '0' COMMENT '数量',
  `target_id` int(11) default NULL COMMENT '指向目标的id',
  PRIMARY KEY  (`tid`),
  UNIQUE KEY `tag` (`tag`,`target_id`)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8 COMMENT='subject的tag表' AUTO_INCREMENT=11946 ;

-- --------------------------------------------------------

--
-- 表的结构 `dr_user`
--

DROP TABLE IF EXISTS `dr_user`;
CREATE TABLE IF NOT EXISTS `dr_user` (
  `id` int(11) NOT NULL auto_increment,
  `uid` varchar(20) default NULL,
  `title` varchar(120) default NULL,
  `douban_id` int(11) default NULL,
  `douban_link` varchar(128) default NULL,
  `location_id` varchar(20) default NULL,
  `location` varchar(80) default NULL,
  `content` text NOT NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `uid_2` (`uid`),
  UNIQUE KEY `douban_id` (`douban_id`),
  KEY `uid` (`uid`)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8 AUTO_INCREMENT=4061 ;

-- --------------------------------------------------------

--
-- 表的结构 `dr_user_friends`
--

DROP TABLE IF EXISTS `dr_user_friends`;
CREATE TABLE IF NOT EXISTS `dr_user_friends` (
  `ufid` int(11) NOT NULL auto_increment,
  `userid` int(11) default '0',
  `friendid` int(11) default '0',
  PRIMARY KEY  (`ufid`)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8 COMMENT='用户关系表' AUTO_INCREMENT=158 ;
