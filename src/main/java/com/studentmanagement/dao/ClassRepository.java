package com.studentmanagement.dao;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.studentmanagement.dto.ClassRequestDto;
import com.studentmanagement.dto.ClassResponseDto;

@Mapper
public interface ClassRepository {

	String insertData = "insert into class(id,name) values (#{id},#{name})";

	String selectOne = "select * from class where id=#{id} or name=#{name}";
	
	String selectAll = "select * from class";
	
	@Select(selectOne)
	public List<ClassResponseDto> findById(ClassRequestDto dto);
	
	@Select(selectAll)
	public List<ClassResponseDto> findAll();
	
	@Insert(insertData)
	public int insert(ClassRequestDto dto);
}
