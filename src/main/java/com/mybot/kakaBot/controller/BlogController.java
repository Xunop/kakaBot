//package com.mybot.kakaBot.controller;
//
//import com.mybot.kakaBot.anotation.Event;
//import com.mybot.kakaBot.enums.EventType;
//import com.mybot.kakaBot.task.BlogCommentHandle;
//import lombok.extern.slf4j.Slf4j;
//import net.mamoe.mirai.event.events.FriendMessageEvent;
//import org.springframework.stereotype.Controller;
//
//import javax.annotation.Resource;
//
///**
// * 回复博客评论
// * @Author xun
// * @create 2022/7/31 14:25
// */
//@Slf4j
//@Controller
//public class BlogController {
//
//    // 博客评论id
//    String objectId = null;
//    // 博客文章url
//    String articleUrl = null;
//    // 需要回复的评论内容
//    String comment = null;
//    // 评论昵称
//    String userName = null;
//
//    @Resource
//    BlogCommentHandle blogCommentHandle;
//
//    @Event(type = EventType.User, command = "回复评论")
//    public void ReplyComment(FriendMessageEvent event) {
//        try {
//            if (comment == null || objectId == null || articleUrl == null) {
//                throw new Exception();
//            }
//            event.getSubject().sendMessage("objectId、文章url请务必复制正确");
//            event.getSubject().sendMessage("/oid/url/cm/un");
//            blogCommentHandle.reply(comment, objectId, articleUrl, userName);
//        } catch (Exception e) {
//            log.error(String.valueOf(e));
//            event.getSubject().sendMessage("回复失败，请确定内容正确");
//        } finally {
//            objectId = null;
//            articleUrl = null;
//            comment = null;
//            userName = null;
//        }
//    }
//
//    @Event(type = EventType.User, command = "/oid")
//    public void setObjectId(FriendMessageEvent event) {
//        objectId = event.getMessage().contentToString().substring(5);
//    }
//    @Event(type = EventType.User, command = "/url")
//    public void setArticleUrl(FriendMessageEvent event) {
//        articleUrl = event.getMessage().contentToString().substring(5);
//    }
//    @Event(type = EventType.User, command = "/cm")
//    public void setComment(FriendMessageEvent event) {
//        comment = event.getMessage().contentToString().substring(4);
//    }
//    @Event(type = EventType.User, command = "/un")
//    public void setUserName(FriendMessageEvent event) {
//        userName = event.getMessage().contentToString().substring(4);
//    }
//
//
//    @Event(type = EventType.User, command = "删除评论")
//    public void deleteComment(FriendMessageEvent event) {
//        try {
//            if (objectId == null) {
//                throw new Exception();
//            }
//            event.getSubject().sendMessage("将删除该评论");
//            blogCommentHandle.deleteComment(objectId);
//        } catch (Exception e) {
//            event.getSubject().sendMessage("删除失败");
//        }
//    }
//}
