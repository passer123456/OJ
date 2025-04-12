package com.onlinejudge.mapper;

import java.util.List;

import org.apache.ibatis.annotations.*;

import com.onlinejudge.entity.User;

@Mapper
public interface UserMapper 
{
	// @Insert("INSERT INTO userData (username, password, role, avatar, bio) VALUES (#{username}, #{password}, #{role}, #{avatar}, #{bio})")
    // @Options(useGeneratedKeys = true, keyProperty = "userId")
    // int insertUser(User user);

    // @Select("SELECT * FROM userData WHERE user_id = #{userId}")
    // User selectUserById(int userId);

    // @Select("SELECT * FROM userData WHERE username = #{username}")
    // User selectUserByUsername(String username);

    // @Update("UPDATE userData SET password = #{password}, role = #{role}, avatar = #{avatar}, bio = #{bio} WHERE user_id = #{userId}")
    // int updateUser(User user);

    // @Delete("DELETE FROM userData WHERE user_id = #{userId}")
    // int deleteUser(int userId);

    List<User> selectAllUsers();
}
