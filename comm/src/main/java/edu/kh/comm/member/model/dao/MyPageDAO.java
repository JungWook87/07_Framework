package edu.kh.comm.member.model.dao;

import java.util.HashMap;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import edu.kh.comm.member.model.vo.Member;

@Repository
public class MyPageDAO {
	
	private Logger logger = LoggerFactory.getLogger(MyPageDAO.class);
	
	@Autowired
	private SqlSessionTemplate sqlSession;

	public int updateInfo(Member loginMember) {
		
		return sqlSession.update("myPageMapper.updateInfo", loginMember);
	}

	public String memberPwCheck(int memberNo) {
		
		return sqlSession.selectOne("myPageMapper.memberPwCheck" , memberNo);
	}

	public int changePw(Member loginMember) {
		return sqlSession.update("myPageMapper.changePw", loginMember);
	}

	public void secession(int memberNo) {
		sqlSession.update("myPageMapper.secession", memberNo);
	}


}