package com.mybot.kakaBot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mybot.kakaBot.entity.Image;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @Author xun
 * @create 2022/5/1 13:01
 */
@Mapper
@Repository
public interface ImagesMapper extends BaseMapper<Image> {

    Image selectOne();
}
