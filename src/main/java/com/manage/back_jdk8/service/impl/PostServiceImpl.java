package com.manage.back_jdk8.service.impl;

import com.manage.back_jdk8.dao.post;
import com.manage.back_jdk8.entity.Post;
import com.manage.back_jdk8.entity.SysUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostServiceImpl {
    @Autowired
    post post_selections;

    public void save(Post t){
        post_selections.insert(t);
    }

    public void delete(Long id){
        post_selections.deleteById(id);
    }

    public Post getById(Long id){
        return post_selections.getById(id);
    }

    public List<Post> get(){
        return post_selections.get();
    }

    public void update(Post t){
        post_selections.update(t);
    }

    public List<Post> pageSelect(String content, Long current, Long size){
        return post_selections.pageSelect(content,current,size);
    }

}
