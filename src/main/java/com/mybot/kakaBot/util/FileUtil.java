//package com.mybot.kakaBot.util;
//
//import com.mybot.kakaBot.config.MinioConfig;
//import io.minio.*;
//import io.minio.http.Method;
//import io.minio.messages.Bucket;
//import io.minio.messages.DeleteError;
//import io.minio.messages.DeleteObject;
//import io.minio.messages.Item;
//import lombok.SneakyThrows;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.Resource;
//import java.io.InputStream;
//import java.util.ArrayList;
//import java.util.LinkedList;
//import java.util.List;
//import java.util.concurrent.TimeUnit;
//
///**
// * @Author xun
// * @create 2022/7/28 23:00
// */
//@Slf4j
//@Component
//public class FileUtil {
//
//    @Resource
//    MinioConfig minioConfig;
//    @Resource
//    MinioClient minioClient;
//
//
//    /**
//     * 检查存储桶是否存在
//     *
//     * @param bucketName 存储桶名称
//     */
//    @SneakyThrows
//    public boolean bucketExists(String bucketName) {
//        boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
//        if (found) {
//            log.info("{} exists", bucketName);
//        } else {
//            log.info("{} does not exist", bucketName);
//        }
//        return found;
//    }
//
//    /**
//     * 创建存储桶
//     *
//     * @param bucketName 存储桶名称
//     */
//    @SneakyThrows
//    public boolean makeBucket(String bucketName) {
//        boolean flag = bucketExists(bucketName);
//        if (!flag) {
//            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
//            return true;
//        } else {
//            return false;
//        }
//    }
//
//    /** 列出所有存储桶名称 */
//    @SneakyThrows
//    public List<String> listBucketNames() {
//        List<Bucket> bucketList = listBuckets();
//        List<String> bucketListName = new ArrayList<>();
//        for (Bucket bucket : bucketList) {
//            bucketListName.add(bucket.name());
//        }
//        return bucketListName;
//    }
//
//    /** 列出所有存储桶 */
//    @SneakyThrows
//    public List<Bucket> listBuckets() {
//        return minioClient.listBuckets();
//    }
//
//    /**
//     * 删除存储桶
//     *
//     * @param bucketName 存储桶名称
//     */
//    @SneakyThrows
//    public boolean removeBucket(String bucketName) {
//        boolean flag = bucketExists(bucketName);
//        if (flag) {
//            Iterable<Result<Item>> myObjects = listObjects(bucketName);
//            for (Result<Item> result : myObjects) {
//                Item item = result.get();
//                // 有对象文件，则删除失败
//                if (item.size() > 0) {
//                    return false;
//                }
//            }
//            // 删除存储桶，注意，只有存储桶为空时才能删除成功。
//            minioClient.removeBucket(RemoveBucketArgs.builder().bucket(bucketName).build());
//            flag = bucketExists(bucketName);
//            return !flag;
//        }
//        return false;
//    }
//
//    /**
//     * 列出存储桶中的所有对象名称
//     *
//     * @param bucketName 存储桶名称
//     */
//    @SneakyThrows
//    public List<String> listObjectNames(String bucketName) {
//        List<String> listObjectNames = new ArrayList<>();
//        boolean flag = bucketExists(bucketName);
//        if (flag) {
//            Iterable<Result<Item>> myObjects = listObjects(bucketName);
//            for (Result<Item> result : myObjects) {
//                Item item = result.get();
//                listObjectNames.add(item.objectName());
//            }
//        } else {
//            listObjectNames.add("存储桶不存在");
//        }
//        return listObjectNames;
//    }
//
//    /**
//     * 列出存储桶中的所有对象
//     *
//     * @param bucketName 存储桶名称
//     */
//    @SneakyThrows
//    public Iterable<Result<Item>> listObjects(String bucketName) {
//        boolean flag = bucketExists(bucketName);
//        if (flag) {
//            return minioClient.listObjects(ListObjectsArgs.builder().bucket(bucketName).build());
//        }
//        return null;
//    }
//
//
//
//    /**
//     * 文件访问路径
//     *
//     * @param bucketName 存储桶名称
//     * @param objectName 存储桶里的对象名称
//     * @return
//     */
//    @SneakyThrows
//    public String getObjectUrl(String bucketName, String objectName) {
//        boolean flag = bucketExists(bucketName);
//        String url = "";
//        if (flag) {
//            url =
//                    minioClient.getPresignedObjectUrl(
//                            GetPresignedObjectUrlArgs.builder()
//                                    .method(Method.GET)
//                                    .bucket(bucketName)
//                                    .object(objectName)
//                                    .expiry(2, TimeUnit.MINUTES)
//                                    .build());
//            log.info("url：{}", url);
//        }
//        return url;
//    }
//
//    /**
//     * 删除一个对象
//     *
//     * @param bucketName 存储桶名称
//     * @param objectName 存储桶里的对象名称
//     */
//    @SneakyThrows
//    public boolean removeObject(String bucketName, String objectName) {
//        boolean flag = bucketExists(bucketName);
//        if (flag) {
//            minioClient.removeObject(
//                    RemoveObjectArgs.builder().bucket(bucketName).object(objectName).build());
//            return true;
//        }
//        return false;
//    }
//
//    /**
//     * 以流的形式获取一个文件对象
//     *
//     * @param bucketName 存储桶名称
//     * @param objectName 存储桶里的对象名称
//     */
//    @SneakyThrows
//    public InputStream getObject(String bucketName, String objectName) {
//        boolean flag = bucketExists(bucketName);
//        try {
//            if (flag) {
//                StatObjectResponse statObject = statObject(bucketName, objectName);
//                if (statObject != null && statObject.size() > 0) {
//                    return minioClient.getObject(
//                            GetObjectArgs.builder().bucket(bucketName).object(objectName).build());
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//    /**
//     * 获取对象的元数据
//     *
//     * @param bucketName 存储桶名称
//     * @param objectName 存储桶里的对象名称
//     */
//    @SneakyThrows
//    public StatObjectResponse statObject(String bucketName, String objectName) {
//        boolean flag = bucketExists(bucketName);
//        if (flag) {
//            return minioClient.statObject(
//                    StatObjectArgs.builder().bucket(bucketName).object(objectName).build());
//        }
//        return null;
//    }
//
//    /**
//     * 删除指定桶的多个文件对象,返回删除错误的对象列表，全部删除成功，返回空列表
//     *
//     * @param bucketName 存储桶名称
//     * @param objectNames 含有要删除的多个object名称的迭代器对象
//     */
//    @SneakyThrows
//    public boolean removeObject(String bucketName, List<String> objectNames) {
//        boolean flag = bucketExists(bucketName);
//        if (flag) {
//            List<DeleteObject> objects = new LinkedList<>();
//            for (String objectName : objectNames) {
//                objects.add(new DeleteObject(objectName));
//            }
//            Iterable<Result<DeleteError>> results =
//                    minioClient.removeObjects(
//                            RemoveObjectsArgs.builder().bucket(bucketName).objects(objects).build());
//            for (Result<DeleteError> result : results) {
//                DeleteError error = result.get();
//                log.info("Error in deleting object {}: {}", error.objectName(), error.message());
//            }
//        }
//        return true;
//    }
//
//    /**
//     * 以流的形式获取一个文件对象（断点下载）
//     *
//     * @param bucketName 存储桶名称
//     * @param objectName 存储桶里的对象名称
//     * @param offset 起始字节的位置
//     * @param length 要读取的长度 (可选，如果无值则代表读到文件结尾)
//     */
//    @SneakyThrows
//    public InputStream getObject(String bucketName, String objectName, long offset, Long length) {
//        boolean flag = bucketExists(bucketName);
//        if (flag) {
//            StatObjectResponse statObject = statObject(bucketName, objectName);
//            if (statObject != null && statObject.size() > 0) {
//                return minioClient.getObject(
//                        GetObjectArgs.builder()
//                                .bucket(bucketName)
//                                .object(objectName)
//                                .offset(offset)
//                                .length(length)
//                                .build());
//            }
//        }
//        return null;
//    }
//
//    /**
//     * 上传文件
//     *
//     * @param bucketName  bucket名称
//     * @param objectName  文件名称
//     * @param stream      文件流
//     * @throws Exception
//     */
//    @SneakyThrows
//    public void uploadFile(String bucketName, String objectName, InputStream stream) throws Exception {
//        MinioClient minioClient = MinioClient
//                .builder()
//                .endpoint(minioConfig.getEndpoint())
//                .credentials(minioConfig.getAccessKey(), minioConfig.getSecretKey())
//                .build();
//        minioClient.putObject(PutObjectArgs.builder().bucket(bucketName).object(objectName).stream(
//                        stream, -1, 10485760)
//                .contentType("application/octet-stream")
//                .build());
//    }
//}
