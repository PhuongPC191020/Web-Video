package com.poly.service;

import java.util.List;

import com.poly.entity.History;
import com.poly.entity.User;
import com.poly.entity.Video;

public interface HistoryService {
	List<History> findbyUser(String username);
	List<History> findByUserAndIsLiked(String username);
	History findByuserIdAndVideoId(Integer userId, Integer videoId);
	History create (User user, Video video);
	boolean updateLikeOrUnlike (User user, String videoHref);
}
