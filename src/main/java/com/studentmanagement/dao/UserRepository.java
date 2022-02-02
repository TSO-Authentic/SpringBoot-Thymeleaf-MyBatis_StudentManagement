package com.studentmanagement.dao;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.studentmanagement.dto.UserRequestDto;
import com.studentmanagement.dto.UserResponseDto;

@Mapper
public interface UserRepository {

	String insertData = "insert into user (id,name,password) values (#{id},#{name},#{password})";

	String updateData = "update user set name=#{name}, password=#{password} where id=#{id}";
	
	String deleteData = "delete from user where id=#{id}";
	
	String selectOne = "select * from user where id=#{id} or name=#{name}";
	
	String selectAll = "select * from user";
	
	@Select(selectOne)
	public List<UserResponseDto> findByFilter(UserRequestDto dto);
	
	@Select(selectAll)
	public List<UserResponseDto> findAll();
	
	@Delete(deleteData)
	public int deleteById(UserRequestDto dto);
	
	@Update(updateData)
	public int update(UserRequestDto dto);
	
	@Insert(insertData)
	public int insert(UserRequestDto dto);
}
