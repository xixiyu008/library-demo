package com.example.library.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.library.entity.Reader;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface ReaderMapper extends BaseMapper<Reader> {
    @Select("SELECT * FROM reader WHERE id = #{id} FOR UPDATE")
    Reader selectByIdForUpdate(@Param("id") Long id);
}
