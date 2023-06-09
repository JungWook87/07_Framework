package edu.kh.comm.member.controller;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.kh.comm.member.model.service.MyPageService;
import edu.kh.comm.member.model.vo.Member;

@Controller
@RequestMapping("/member/myPage")
@SessionAttributes({"loginMember"})
public class MyPageController {
	
	private Logger logger = LoggerFactory.getLogger(MyPageController.class);
	
	@Autowired
	private MyPageService service;
	
	// 비밀번호 변경
	
	// 회원 탈퇴
	
	// 마이페이지 이동
	@GetMapping("/info")
	public String myPageOpen() {
		
		return "member/myPage-info";
	}
	
	// 회원 정보 수정
	@PostMapping("/info")
	public String updateInfo(@ModelAttribute("loginMember") Member loginMember,
							@RequestParam Map<String, Object> paramMap, // 요청시 전달된 파라미터를  구분하지 않고 모두 Map에 담아서 얻어옴
							String[] updateAddress,
							RedirectAttributes ra ) {
		
		// 필요한 값
		// - 닉네임
		// - 전화번호
		// - 주소 ( String[] 로 얻어와 String.join()을 이용해서 문자열로 변경)
		// - 회원 번호(Session -> 로그인한 회원 정보를 통해서 얻어옴)
		// 		--> @ModelAttribute, @SessionAttributes 필요
		
		// @SessionAttributes 의 역할 2가지
		// 1) Model에 세팅 데이터의 key값을 @SessionAttributes에 작성하면
		//    해당 key값과 같은 Model에 세팅된 데이터를 request -> session scope로 이동
		
		// 2) 기존의 session scope로 이동시킨 값을 얻어오는 역할
		// @ModelAttribute("loginMember") Member loginMember
		//					[session key]
		//	--> @SessionAttributes를 통해 session scope에 등록된 값을 얻어와
		// 		오른쪽에 작성된 Member loginMember 변수에 대입
		//      단, @SessionAttributes({"loginMember"}) 가 클래스 위에 작성되어 있어야 가능
		
		// *** 매개변수를 이용해서 session, 파라미터 데이터를 동시에 얻어올 때 문제점 ***
		
		// session에서 객체를 얻어오기 위해서 매개변수에 작성한 상태
		// -> @ModelAttribute("loginMember") Member loginMember
		
		// 파라미터의 name 속성값이 매개변수에 작성된 객체의 필드와 일치하면 
		// -> ex) name = "memberNickname"
		// session 에서 얻어온 객체의 필드에 덮어쓰기가 된다
		
		// [해결 방법] 파라미터의 name 속성을 변경해서 얻어오면 문제해결!
		// (필드명 겹쳐서 문제니까 겹치지 않게 하자)
		
		/* 내가 작성한 코드... session에 바로 수정했다.
		 * 문제점 : DB에 저장이 안되어 있어도 session에 값이 수정되었기 때문에 좋은 코드가 아니다
		 * 해결 : 받아온 Map에 session 값을 넣고 Map을 매개변수로 하여 DB 수정 후
		 * 성공시 session 값에 수정된 값을 넣고 동기화 시켜준다
		 */
//		String addr = String.join(",,", updateAddress);
//		if(addr.equals(",,,,")) addr = null;
//		
//		
//		loginMember.setMemberNickname((String)paramMap.get("memberNickname"));
//		loginMember.setMemberTel((String)paramMap.get("memberTel"));
//		loginMember.setMemberAddress(addr);
//		
//		int result = service.updateInfo(loginMember);
//		String message = "";
//		
//		if(result != 0) {
//			message = "회원 정보가 수정되었습니다";
//		} else {
//			message = "회원 정보 수정 실패";
//		}
//		
//		ra.addFlashAttribute("message", message);
		
		String addr = String.join(",,", updateAddress);
		if(addr.equals(",,,,")) addr = null;
		
		paramMap.put("memberNo", loginMember.getMemberNo());
		paramMap.put("addr", addr);
		
		int result = service.updateInfo(paramMap);
		String message = "";
		
		if(result != 0) {
			message = "회원 정보가 수정되었습니다";
			
			loginMember.setMemberNickname((String)paramMap.get("updateNickname"));
			loginMember.setMemberTel((String)paramMap.get("updateTel"));
			loginMember.setMemberAddress(addr);
			
		} else {
			message = "회원 정보 수정 실패";
		}
		
		ra.addFlashAttribute("message", message);
		
		return "redirect:info";
	}
	
	
	// 비밀 번호 변경 페이지 호출
	@GetMapping("/changePw")
	public String changePw() {
		return "member/myPage-changePw";
	}
	
	// 비밀 번호 변경
	@PostMapping("/changePw")
	public String changePw(
					@ModelAttribute("loginMember") Member loginMember,
					// @RequestParam("currentPw") String currentPw,
					// @RequestParam("newPw") String newPw,
					@RequestParam Map<String, Object> paramMap,
					RedirectAttributes ra) {
		/* 세션 건들지 말자
		int memberNo = loginMember.getMemberNo();
		
		int memberPwCheck = service.memberPwCheck(memberNo, currentPw);
		
		int result = 0;
		String message = "";
		
		if(memberPwCheck == 1) {
			
			loginMember.setMemberPw(newPw);
			
			result = service.changePw(loginMember);
			
			if(result != 0) message = "비밀번호 변경 성공";
			else message = "비밀번호 변경 실패";
		
		} else {
			message = "현재 비밀번호가 일치하지 않습니다";
		}
		*/
		
		// 로그인된 회원의 번호를 paramMap 추가
		paramMap.put("memberNo", loginMember.getMemberNo());
		
		int result = service.changePw(paramMap);
		String message = null;
		
		if(result == 1) message = "비밀번호 변경 성공";
		else message = "비밀번호 변경 실패";
		
		ra.addFlashAttribute("message", message);
		
		return "redirect:/";
	}
	
	
	// 회원 탈퇴 페이지 호출
	@GetMapping("/secession")
	public String secession() {
		return "member/myPage-secession";
	}
	
	// 회원 탈퇴 
	@PostMapping("/secession")
	public String secession(@ModelAttribute("loginMember") Member loginMember,
							RedirectAttributes ra,
							SessionStatus status,
							HttpServletRequest req,
							HttpServletResponse resp) {
		// session 값 없애기
		// 쿠키 삭제
		// Cookie cookie = new Cookie("saveId", "");
		// cookie.setMaxAge(0);
		// cookie.setPath(req.getContextPath());
		// resp.addCookie(cookie);
		
		int result = service.secession(loginMember);
		
		String message = "";
		
		if(result == -1) {
			message = "비밀번호가 일치하지 않습니다";
			
		} else if(result == 0) {
			
			message = "회원 탈퇴 실패";
			
		} else {
			
			message = "회원탈퇴가 정상적으로 이뤄졌습니다.";
			
			// session 없애기
			status.setComplete();
			
			// 쿠키 삭제
			Cookie cookie = new Cookie("saveId", "");
			cookie.setMaxAge(0);
			cookie.setPath(req.getContextPath());
			resp.addCookie(cookie);
			
		}
		
		ra.addFlashAttribute("message", message);
		
		
		return "redirect:/";
	}
	
	@GetMapping("/profile")
	public String updateProfile() {
		return "member/myPage-profile";
	}
	
	@PostMapping("/profile")
	public String updateProfile( @ModelAttribute("loginMember") Member loginMember,
								@RequestParam("uploadImage") MultipartFile uploadImage, /* 업로드 파일*/
								@RequestParam Map<String, Object> map,
								HttpServletRequest req, /* 파일 저장 경로 탐색용 */
								RedirectAttributes ra) throws IOException {
		
		// 경로 작성하기
		
		// 1) 웹 접근 경로 (/comm/resources/images/memberProfile/)
		String webPath = "/resources/images/memberProfile/";
		
		// 2) 서버 저장 폴더 경로
		// C:\workspace\07_Framework\comm\src\main\webapp\resources\images\memberProfile
		String folderPath = req.getSession().getServletContext().getRealPath(webPath);
		
		// 경로 2개, 이미지, delete, 회원번호 map에 담기
		// delete는 이미 맵에 들어가져 있는 상태
		map.put("webPath",webPath);
		map.put("folderPath",folderPath);
		map.put("uploadImage",uploadImage);
		map.put("memberNo",loginMember.getMemberNo());
		
		int result = service.updateProfile(map);
		
		String message = null;
		
		if(result > 0) {
			message = "프로필 이미지가 변경되었습니다";
			
			// DB - 세션 동기화
			loginMember.setProfileImage((String)map.get("profileImage"));
			
		} else {
			message = "프로필 이미지 변경 실패";
		}
		
		ra.addFlashAttribute("message", message);
		
		return "redirect:profile";
		
	}
	
	
}
	
























