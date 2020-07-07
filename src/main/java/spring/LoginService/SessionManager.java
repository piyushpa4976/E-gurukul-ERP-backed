package spring.LoginService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

public class SessionManager implements HandlerInterceptor {
	// This method is called before the controller

	@Autowired
	private Login_service login_service;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {

		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Methods", "POST, PUT, GET, OPTIONS, DELETE");
		response.setHeader("Access-Control-Allow-Headers",
				"Content-Type, Access-Control-Allow-Headers,Access-Control-Allow-Origin, Authorization, X-Requested-With,observe");
		response.setHeader("Access-Control-Max-Age", "3600");
		response.setHeader("Access-Control-Allow-Credentials", "true");
		response.setHeader("Access-Control-Expose-Headers", "Authorization");
		response.addHeader("Access-Control-Expose-Headers", "responseType");
		response.addHeader("Access-Control-Expose-Headers", "observe");
		System.out.println("Request Method: " + request.getMethod());
		if ((request.getMethod().equalsIgnoreCase("OPTIONS"))) {

			response.setHeader("Access-Control-Allow-Origin", "*");
			response.setHeader("Access-Control-Allow-Methods", "POST,GET,DELETE,PUT");
			response.setHeader("Access-Control-Max-Age", "3600");
			response.setHeader("Access-Control-Allow-Headers", "Access-Control-Expose-Headers"
					+ "Authorization, content-type," + "useremail"
					+ ",Access-Control-Allow-Headers, Origin,Accept, Content-Type, Access-Control-Request-Method, Access-Control-Request-Headers,Access-Control-Allow-Origin,access-control-request-method,accept,origin,authorization,x-requested-with,responseType,observe");
			response.setStatus(HttpServletResponse.SC_OK);
			
			return true;
		}

		String token = request.getHeader("Authorization");
		String email = request.getHeader("useremail");

//		System.out.println(email);
//		System.out.println(token);

		try {
			token = token.replace("Bearer ", "");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		boolean permission = login_service.authorizationcheck(email, token);

	
		if (permission) {
			return true;
		} else {
			response.setStatus(HttpStatus.UNAUTHORIZED.value());
			return false;
			// Above code will send a 401 with no response body.
			// If you need a 401 view, do a redirect instead of
			// returning false.
			// response.sendRedirect("/401"); // assuming you have a handler mapping for 401

		}
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {

	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {

	}
}