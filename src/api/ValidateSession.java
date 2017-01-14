package api;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import db.DBConnection;

/**
 * A utility class to validate if user sends the request is the user logged in.
 */
public class ValidateSession {
	public static boolean sessionValid(HttpServletRequest request, DBConnection connection) {
		HttpSession session = request.getSession();
		String requestUser = request.getParameter("user_id");
		Object currentUser = session.getAttribute("user");
		if (currentUser == null || !currentUser.toString().equals(requestUser)) {
			return false;
		}
		return true;
	}
}
