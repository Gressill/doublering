CREATE TABLE IF NOT EXISTS `dr_user_collection` (
  `cid` int(11) NOT NULL auto_increment,
  `user_id` int(11) default '0',
  `subject_id` int(11) default '0',
  `collection_id` int(11) default NULL COMMENT '收藏id',
  `type` varchar(10) default NULL,
  `title` varchar(150) default NULL,
  `status` varchar(20) NOT NULL,
  PRIMARY KEY  (`cid`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COMMENT='用户收藏表' AUTO_INCREMENT=1 ;


CREATE TABLE IF NOT EXISTS `dr_user_friends` (
  `ufid` int(11) NOT NULL auto_increment,
  `userid` int(11) default '0',
  `friendid` int(11) default '0',
  PRIMARY KEY  (`ufid`),
  UNIQUE KEY `userid` (`userid`,`friendid`)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8 COMMENT='用户关系表' AUTO_INCREMENT=706 ;