package com.poly.controller;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.poly.constant.SessionAttr;
import com.poly.entity.User;
import com.poly.service.EmailService;
import com.poly.service.UserService;
import com.poly.service.impl.EmailServiceImpl;
import com.poly.service.impl.UserServiceImpl;
@WebServlet( urlPatterns = {"/login", "/logout", "/register", "/forgotPass", "/changePass"})
public class UserController extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1370774722755113971L;
	/**
	 * 
	 */

	
	private UserService userService = new UserServiceImpl();
	private EmailService emailService = new EmailServiceImpl();
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HttpSession session = req.getSession();
		String path = req.getServletPath();
		switch (path) {
		case "/login":
				doGetLogin(req, resp);
			break;
		case "/register":
			doGetRegsiter(req, resp);
		break;
		case "/logout":
			doGetLogout(session, req, resp);
		break;
		case "/forgotPass":
			doGetForgotPass(req, resp);
		break;
		
		}
	}
	
	private void doGetLogin(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		RequestDispatcher requestDispatcher = req.getRequestDispatcher("/views/user/login.jsp");
		requestDispatcher.forward(req, resp);
	}
	private void doGetRegsiter(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		RequestDispatcher requestDispatcher = req.getRequestDispatcher("/views/user/register.jsp");
		requestDispatcher.forward(req, resp);
	}
	private void doGetForgotPass(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		RequestDispatcher requestDispatcher = req.getRequestDispatcher("/views/user/forgotPass.jsp");
		requestDispatcher.forward(req, resp);
	}
	
	private void doGetLogout(HttpSession session, HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		session.removeAttribute(SessionAttr.CURRENT_USER);
		resp.sendRedirect("index");
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HttpSession session = req.getSession();
		String path = req.getServletPath();
		switch (path) {
		case "/login":
				doPostLogin(session, req, resp);
			break;
		case "/register":
			doPostRegsiter(session, req, resp);
		break;
		case "/forgotPass":
			doPostForgotPass(req, resp);
			break;
		case "/changePass":
			doPostChangePass(session, req, resp);
		break;
		}
	}
	private void doPostLogin(HttpSession session, HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String username = req.getParameter("username");	
		String password = req.getParameter("password");
		
		User user = userService.login(username, password);
		if(user != null) {
			session.setAttribute(SessionAttr.CURRENT_USER, user);
			resp.sendRedirect("index");
		}else {
			resp.sendRedirect("login");
		}
	}
	private void doPostRegsiter(HttpSession session, HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String username = req.getParameter("username");	
		String password = req.getParameter("password");
		String email = req.getParameter("email");
		
		User user = userService.regsiter(username, password, email);
		if(user != null) {
			emailService.sendEmail(getServletContext(), user, "welcome");   
			session.setAttribute(SessionAttr.CURRENT_USER, user);
			resp.sendRedirect("index");
		}else {
			resp.sendRedirect("register");
		}
	}
	
	private void doPostForgotPass( HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("application/json");
		String email = req.getParameter("email");
		User userWithNewPass = userService.resetPassword(email);
		
		if (userWithNewPass != null) {
			emailService.sendEmail(getServletContext(), userWithNewPass, "forgot");
			resp.setStatus(204);
		} else {
			resp.setStatus(400);
		}
	}	
	
	private void doPostChangePass(HttpSession session, HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
	    resp.setContentType("application/json");
	    String currentPass = req.getParameter("currentPass");
	    String newPass = req.getParameter("newPass");
	    
	    User currentUser = (User) session.getAttribute(SessionAttr.CURRENT_USER);
	    
	    if (currentUser.getPassword().equals(currentPass)) {
	        currentUser.setPassword(newPass);
	        // Gọi userService để cập nhật thông tin người dùng trong cơ sở dữ liệu
	        User updateUser = userService.update(currentUser);
	        if (updateUser != null) {
	            session.setAttribute(SessionAttr.CURRENT_USER, updateUser);
	            resp.setStatus(204); // Trả về mã trạng thái thành công
	        } else {
	            resp.setStatus(400); // Trả về mã trạng thái lỗi
	        }
	    } else {
	        resp.setStatus(400); // Trả về mã trạng thái lỗi
	    }
	}

}
