package com.studentmanagement.dao;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.studentmanagement.dto.StudentRequestDto;
import com.studentmanagement.dto.StudentResponseDto;

@Mapper
public interface StudentRepository {

	
	String insertData = "insert into student (student_id,student_name,class_name,register_date,status) values(#{studentId},#{studentName},#{className},#{registerDate},#{status})";

	String updateData = "update student set student_name=#{studentName},class_name=#{className},register_date=#{registerDate},status=#{status} where student_id=#{studentId}";

	String deleteData = "delete from student where student_id=#{studentId}";
	
	String selectOne = "select * from student where student_id=#{studentId} or student_name=#{studentName} or class_name=#{className}";
	
	String selectAll = "select * from student";
	
	@Select(selectOne)
	public List<StudentResponseDto> findByFilter(StudentRequestDto dto);
	
	@Select(selectAll)
	public List<StudentResponseDto> findAll();
	
	@Delete(deleteData)
	public int deleteById(StudentRequestDto dto);
	
	@Update(updateData)
	public int update(StudentRequestDto dto);
	
	@Insert(insertData)
	public int insert(StudentRequestDto dto);
}
