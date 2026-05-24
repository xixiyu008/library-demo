package com.example.library.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.library.entity.BorrowRecord;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface BorrowRecordMapper extends BaseMapper<BorrowRecord> {
    @Select("SELECT * FROM borrow_record WHERE id = #{id} FOR UPDATE")
    BorrowRecord selectByIdForUpdate(@Param("id") Long id);
}
