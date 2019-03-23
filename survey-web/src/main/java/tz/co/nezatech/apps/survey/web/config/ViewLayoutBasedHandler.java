package tz.co.nezatech.apps.survey.web.config;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import tz.co.nezatech.apps.survey.model.User;
import tz.co.nezatech.apps.survey.repository.UserRepository;

@Component
public class ViewLayoutBasedHandler extends HandlerInterceptorAdapter {

	private static final Logger logger = LoggerFactory.getLogger(ViewLayoutBasedHandler.class);

	@Autowired
	UserRepository userRepository;

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		/*
		 * logger.debug(""); logger.debug("==========");
		 * logger.debug("afterCompletion"); logger.debug("Handler: " + handler);
		 * logger.debug("Request: " + request.getRequestURI());
		 */
		if (ex != null) {
			logger.debug("Exception: " + ex);
		}
		// logger.debug("==========");

		super.afterCompletion(request, response, handler, ex);
	}

	@Override
	public void afterConcurrentHandlingStarted(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		/*
		 * logger.debug(""); logger.debug("==========");
		 * logger.debug("afterConcurrentHandlingStarted"); logger.debug("Handler: " +
		 * handler); logger.debug("Request: " + request.getRequestURI());
		 * logger.debug("==========");
		 */
		super.afterConcurrentHandlingStarted(request, response, handler);
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		/*
		 * logger.debug(""); logger.debug("=========="); logger.debug("preHandle");
		 * logger.debug("Handler: " + handler); logger.debug("Request: " +
		 * request.getRequestURI()); logger.debug("==========");
		 */
		return super.preHandle(request, response, handler);
	}

	@Override
	public void postHandle(final HttpServletRequest req, final HttpServletResponse res, final Object handler,
			final ModelAndView mv) throws Exception {
		/*
		 * logger.debug(""); logger.debug("=========="); logger.debug("postHandle");
		 * logger.debug("Handler: " + handler); logger.debug("Request: " +
		 * req.getRequestURI()); logger.debug("==========");
		 */
		String contextPath = req.getContextPath();

		if (mv != null) {
			String view = mv.getViewName();
			logger.debug("Page: " + view);
			if (view != null && (view.startsWith("forward:") || view.startsWith("redirect:"))) {
				return;
			}
			mv.addObject("contextPath", contextPath);
			String uri = req.getRequestURI();
			List<BreadCrumb> breadcrumbs = new ArrayList<BreadCrumb>();
			try {
				if (uri.startsWith(contextPath)) {
					uri = uri.split(contextPath)[1];
				}
				String arr[] = uri.split("/");
				String path = "";
				for (String part : arr) {
					if (part.isEmpty()) {
						continue;
					}
					path += "/" + part;
					breadcrumbs.add(new BreadCrumb(part, path));
				}
				logger.debug(breadcrumbs.toString());
				mv.addObject("breadcrumbs", breadcrumbs);
			} catch (Exception e) {
				e.printStackTrace();
			}
			String q = req.getQueryString();
			try {
				if (q != null) {
					String arr[] = q.split("&");
					for (String kv : arr) {
						String tokens[] = kv.split("=");
						if (tokens.length > 1) {
							mv.addObject(tokens[0], tokens[1]);
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			CsrfToken token = (CsrfToken) req.getAttribute("_csrf");
			if (token != null) {
				String csrfHeader = token.getHeaderName();
				String csrfParam = token.getParameterName();
				String csrfToken = token.getToken();
				mv.addObject("csrfHeader", csrfHeader);
				mv.addObject("csrfParam", csrfParam);
				mv.addObject("csrfToken", csrfToken);
			}
			// error
			if (view.contains("error")) {
				String errorMsg = "";
				int httpErrorCode = getErrorCode(req);
				logger.debug("ErrorCode: " + httpErrorCode);
				switch (httpErrorCode) {
				case 400: {
					errorMsg = "Http Error Code: 400. Bad Request";
					break;
				}
				case 401: {
					errorMsg = "Http Error Code: 401. Unauthorized";
					break;
				}
				case 404: {
					errorMsg = "Http Error Code: 404. Resource not found";
					break;
				}
				case 405: {
					errorMsg = "Http Error Code: 405. Method not allowed";
					break;
				}
				case 500: {
					errorMsg = "Http Error Code: 500. Internal Server Error";
					break;
				}
				default:
					errorMsg = "ErrorCode: " + errorMsg;
				}
				logger.debug("Error msg: " + errorMsg);
				if (!errorMsg.isEmpty()) {
					mv.addObject("errorMsg", errorMsg);
				}
			}

			Principal p = req.getUserPrincipal();
			if (p != null && p.getName() != null) {
				String username = p.getName();
				User user = userRepository.getAll("username", username).get(0);
				if (user != null) {
					user.setPassword(null);
					if (user.isResetOn() && !view.contains("/changepwd")) {
						res.sendRedirect("" + contextPath + "/pwd/changepwd");
					}
					if (view.contains("/changepwd")) {
						mv.addObject("cpUser", user);
					} else {
						mv.addObject("user", user);
					}
				}
			}
		}

	}

	private int getErrorCode(HttpServletRequest httpRequest) {
		return (Integer) httpRequest.getAttribute("javax.servlet.error.status_code");
	}
}

class BreadCrumb {
	String label, url;

	public BreadCrumb(String label, String url) {
		super();
		this.label = label;
		this.url = url;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public String toString() {
		return "BreadCrumb [label=" + label + ", url=" + url + "]";
	}

}
