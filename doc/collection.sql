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