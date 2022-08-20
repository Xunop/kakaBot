//package com.mybot.kakaBot.task;
//
//import com.alibaba.fastjson.JSONArray;
//import com.alibaba.fastjson.JSONObject;
//import com.mybot.kakaBot.config.BlogConfig;
//import net.mamoe.mirai.Bot;
//import net.mamoe.mirai.contact.Friend;
//import okhttp3.*;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.Resource;
//import java.io.IOException;
//import java.io.InputStream;
//import java.net.HttpURLConnection;
//import java.net.URL;
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.*;
//
///**
// * @Author xun
// * @create 2022/6/22 15:46
// */
//@Component
//public class BlogCommentHandle {
//
//    @Resource
//    BlogConfig blogConfig;
//
//    /**
//     * 查询评论
//     * @return 评论的信息
//     * @throws IOException
//     */
//    public List<String[]> queryAll() throws IOException, ParseException {
//        List<String[]> commentList = new ArrayList<>();
//        URL url = new URL(blogConfig.getUrl());
//        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
//        httpConn.setRequestMethod("GET");
//        httpConn.setRequestProperty("X-LC-Id", blogConfig.getXlcId());
//        httpConn.setRequestProperty("X-LC-Key", blogConfig.getXlcKey());
//        httpConn.setRequestProperty("Content-Type", "application/json");
//
//        InputStream responseStream = httpConn.getResponseCode() / 100 == 2
//                ? httpConn.getInputStream()
//                : httpConn.getErrorStream();
//        Scanner s = new Scanner(responseStream).useDelimiter("\\A");
//        String response = s.hasNext() ? s.next() : "";
//        JSONObject jsonObject = JSONObject.parseObject(response);
//        JSONArray results = jsonObject.getJSONArray("results");
//        for (Object result : results) {
//            JSONObject jsonres = JSONObject.parseObject(result.toString());
//            String objectId = jsonres.get("objectId").toString();
//            String mail = jsonres.get("mail").toString();
//            String temp = jsonres.get("createdAt").toString();
//            String time = UTCToCST(temp);
//            System.out.println(time);
//            String nick = jsonres.get("nick").toString();
//            String blogUrl = jsonres.get("url").toString();
//            String comment = jsonres.get("comment").toString();
//            commentList.add(new String[]{objectId, nick, comment, mail, time, blogUrl});
//        }
//        return commentList;
//    }
//
//    /**
//     * 将UTC时间转成北京时间
//     * @param UTCStr UTC时间
//     * @throws ParseException
//     */
//    public String UTCToCST(String UTCStr) throws ParseException {
//        Date date;
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
//        SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        // 将String类型的UTC时间转化成Date类型
//        date = sdf.parse(UTCStr);
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(date);
//        // UTC时间加八小时就是北京时间
//        calendar.set(Calendar.HOUR, calendar.get(Calendar.HOUR) + 8);
//        //calendar.getTime() 返回的是Date类型，也可以使用calendar.getTimeInMillis()获取时间戳
//        return timeFormat.format(calendar.getTime());
//    }
//
//    /**
//     * 每天23点转发博客今日的评论
//     * 没有则不发送
//     * 除了我自己评论测试外估计没人会发评论了
//     * @throws IOException
//     */
//    @Scheduled(cron = "0 0 23 * * ?")
//    public void transmit() throws IOException, ParseException {
//        Bot bot = Bot.getInstance(191416049);
//        Friend master = bot.getFriend(1919581623);
//        List<String[]> commentList = this.queryAll();
//        SimpleDateFormat fomat = new SimpleDateFormat("yyyy-MM-dd");
//        Date date = new Date();
//        String currentTime = fomat.format(date);
//        int num = commentList.size();
//        if (num == 0) {
//            return;
//        }
//        // 评论库中的排序是按照时间升序排序，所以这里从末尾开始
//        String[] commentInfo = commentList.get(num - 1);
//        String time = commentInfo[4].substring(0, 10);
//        while (currentTime.equals(time) && num > 0) {
//            String comment = "objectId:" + commentInfo[0] + "\n" +
//                    "用户名：" + commentInfo[1] + "\n" +
//                    "邮箱地址：" + commentInfo[3] + "\n" +
//                    "于" + commentInfo[4] + "在文章" + commentInfo[5] + "发表评论" + "\n" +
//                    "内容为" + commentInfo[2] + "\n" +
//                    "复制此链接前去查看：https://blog.asukaxun.cc" + commentInfo[5];
//            assert master != null;
//            master.sendMessage(comment);
//            commentInfo = commentList.get(--num - 1);
//            time = commentInfo[4].substring(0, 10);
//        }
//    }
//
//    /**
//     * 回复评论
//     * 需要提供参数
//     * @param yourComment 评论内容
//     * @param objectId 需要回复评论的objectId
//     * @param articleUrl 评论所在文章url
//     * @throws IOException
//     */
//    public void reply(String yourComment, String objectId, String articleUrl, String userName) throws IOException {
//        OkHttpClient client = new OkHttpClient().newBuilder().build();
//        MediaType mediaType = MediaType.parse("application/json");
//
//        blogConfig.setComment(
//                "\"comment\": \"<p><a class=\\\"at\\\" href='#"+ objectId +"'>@"+ userName +"</a>"+ yourComment+"</p>\",");
//        blogConfig.setUrl("\"url\":\""+ articleUrl +"\",");
//        // objectId跟pid rid都一样
//        blogConfig.setPid("\"pid\":\""+ objectId +"\",");
//        blogConfig.setRid("\"rid\":\""+ objectId +"\",");
//        String comment = blogConfig.getComment();
//        System.out.println(comment);
//        String url = blogConfig.getUrl();
//        String ip = blogConfig.getIp();
//        String link = blogConfig.getLink();
//        String nick = blogConfig.getNick();
//        String mail = blogConfig.getMail();
//        String mailMd5 = blogConfig.getMailMd5();
//        String pid = blogConfig.getPid();
//        String rid = blogConfig.getRid();
//        String ua = blogConfig.getUa();
//        String info = "{" + comment + url + ip + link + nick + mail + mailMd5 + pid + rid + ua + "}";
//        RequestBody body = RequestBody.create(mediaType, info);
//        Request request = new Request.Builder()
//                .url(blogConfig.getUrl())
//                .method("POST", body)
//                .addHeader("X-LC-Id", blogConfig.getXlcId())
//                .addHeader("X-LC-Key", blogConfig.getXlcKey())
//                .addHeader("Content-Type", "application/json")
//                .build();
//        Response response = client.newCall(request).execute();
//    }
//
//    /**
//     * 删除评论
//     * @param objectId 评论的objectId
//     */
//    public void deleteComment (String objectId) throws IOException {
//        OkHttpClient client = new OkHttpClient().newBuilder()
//                .build();
//        MediaType mediaType = MediaType.parse("text/plain");
//        RequestBody body = RequestBody.create(mediaType, "");
//        Request request = new Request.Builder()
//                .url(blogConfig.getUrl() + objectId)
//                .method("DELETE", body)
//                .addHeader("X-LC-Id", blogConfig.getXlcId())
//                .addHeader("X-LC-Key", blogConfig.getXlcKey())
//                .build();
//        Response response = client.newCall(request).execute();
//    }
//}
