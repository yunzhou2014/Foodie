package api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import db.DBConnection;
import db.MongoDBConnection;
import db.MySQLDBConnection;

/**
 * Servlet implementation class VisitHistory
 */
@WebServlet("/history")
public class VisitHistory extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final DBConnection connection = new MySQLDBConnection();

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public VisitHistory() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// allow access only if user sends the request is the user logged in
		HttpSession session = request.getSession();
		if (!ValidateSession.sessionValid(request, connection)) {
			response.setStatus(403);
			return;
		}
		JSONArray array = null;
		String userId = (String) session.getAttribute("user");
		Set<String> visited_business_id = connection.getVisitedRestaurants(userId);
		array = new JSONArray();
		for (String id : visited_business_id) {
			array.put(connection.getRestaurantsById(id, true));
		}
		RpcParser.writeOutput(response, array);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			// allow access only if session exists
			HttpSession session = request.getSession();
			if (session.getAttribute("user") == null) {
				response.setStatus(403);
				return;
			}
			JSONObject input = RpcParser.parseInput(request);
			// allow access only if user sends the request is the user logged in
			String userId = (String) session.getAttribute("user");
			if (input.has("user_id")) {
				String requestUser = (String) input.get("user_id");
				if (userId == null || !userId.equals(requestUser)) {
					response.setStatus(403);
					return;
				}
			}
			if (input.has("visited")) {
				JSONArray array = (JSONArray) input.get("visited");
				List<String> visitedRestaurants = new ArrayList<>();
				for (int i = 0; i < array.length(); i++) {
					String businessId = (String) array.get(i);
					visitedRestaurants.add(businessId);
				}
				connection.setVisitedRestaurants(userId, visitedRestaurants);
				RpcParser.writeOutput(response, new JSONObject().put("status", "OK"));
			} else {
				RpcParser.writeOutput(response, new JSONObject().put("status", "InvalidParameter"));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	protected void doDelete(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			// allow access only if session exists
			HttpSession session = request.getSession();
			if (session.getAttribute("user") == null) {
				response.setStatus(403);
				return;
			}
			// allow access only if user sends the request is the user logged in
			JSONObject input = RpcParser.parseInput(request);
			String userId = (String) session.getAttribute("user");
			if (input.has("user_id")) {
				String requestUser = (String) input.get("user_id");
				if (userId == null || !userId.equals(requestUser)) {
					response.setStatus(403);
					return;
				}
			}
			if (input.has("visited")) {
				JSONArray array = (JSONArray) input.get("visited");
				List<String> visitedRestaurants = new ArrayList<>();
				for (int i = 0; i < array.length(); i++) {
					String businessId = (String) array.get(i);
					visitedRestaurants.add(businessId);
				}
				connection.unsetVisitedRestaurants(userId, visitedRestaurants);
				RpcParser.writeOutput(response, new JSONObject().put("status", "OK"));
			} else {
				RpcParser.writeOutput(response, new JSONObject().put("status", "InvalidParameter"));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

}
