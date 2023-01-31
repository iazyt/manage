package com.manage.back_jdk8.service.impl;

import com.manage.back_jdk8.dao.postLikers;
import com.manage.back_jdk8.entity.PostLikers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostLikersServiceImpl {
    @Autowired
    postLikers postLikers_selections;

    public List<PostLikers> getListById(Long id){return postLikers_selections.getListById(id);}

    public void save(PostLikers t){postLikers_selections.save(t);}

    public void deleteByUserId(Long user_id){postLikers_selections.deleteByUserId(user_id);}
}
