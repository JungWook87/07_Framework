package edu.kh.comm.member.model.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import edu.kh.comm.member.model.dao.MyPageDAO;
import edu.kh.comm.member.model.vo.Member;

@Service
public class MyPageServiceImpl implements MyPageService {
	
	@Autowired
	private MyPageDAO dao;
	
	@Autowired
	private BCryptPasswordEncoder bcrypt;

	@Override
	public int updateInfo(Member loginMember) {
		return dao.updateInfo(loginMember);
	}

	@Override
	public int memberPwCheck(int memberNo, String currentPw) {
		int result = 0;
		
		String DBPw = dao.memberPwCheck(memberNo);
		
		if(bcrypt.matches(currentPw, DBPw)) result = 1;
		
		return result;
	}

	@Override
	public int changePw(Member loginMember) {
		
		String newPwBcrypt = bcrypt.encode(loginMember.getMemberPw());
		
		loginMember.setMemberPw(newPwBcrypt);
		
		return dao.changePw(loginMember);
	}

	@Override
	public void secession(int memberNo) {
		dao.secession(memberNo);
	}
	
	
	
	
	
	
}