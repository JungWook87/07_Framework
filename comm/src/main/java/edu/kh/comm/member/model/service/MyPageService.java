package edu.kh.comm.member.model.service;

import edu.kh.comm.member.model.vo.Member;

public interface MyPageService {

	public abstract int updateInfo(Member loginMember);

	public abstract int memberPwCheck(int memberNo, String currentPw);

	public abstract int changePw(Member loginMember);

	public abstract void secession(int memberNo);


}