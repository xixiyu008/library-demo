package com.example.library.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.library.entity.Book;
import java.time.LocalDateTime;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

public interface BookMapper extends BaseMapper<Book> {
    @Select("SELECT * FROM book WHERE id = #{id} FOR UPDATE")
    Book selectByIdForUpdate(@Param("id") Long id);

    @Update("""
            UPDATE book
            SET available_count = available_count - 1, update_time = #{updateTime}
            WHERE id = #{id}
              AND status = 'AVAILABLE'
              AND available_count > 0
            """)
    int decrementAvailable(@Param("id") Long id, @Param("updateTime") LocalDateTime updateTime);

    @Update("""
            UPDATE book
            SET available_count = available_count + 1, update_time = #{updateTime}
            WHERE id = #{id}
              AND available_count < total_count
            """)
    int incrementAvailable(@Param("id") Long id, @Param("updateTime") LocalDateTime updateTime);
}
