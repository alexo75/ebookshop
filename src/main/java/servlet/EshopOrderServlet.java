package servlet;

import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;

@WebServlet("/eshoporder")
public class EshopOrderServlet extends HttpServlet {

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.println("<html>");
		out.println("<head><title>Order Response</title></head>");
		out.println("<body>");

		try (Connection conn = DriverManager.getConnection(
				"jdbc:postgresql://localhost:5432/ebookshop?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC",
				"postgres", "Milhouse7"); Statement stmt = conn.createStatement();) {
			// Step 3 & 4: Execute a SQL SELECT query and Process the query result
			// Retrieve the books' id. Can order more than one books.
			String[] ids = request.getParameterValues("id");
			if (ids != null) {
				String sqlStr;
				int count;

				// Process each of the books
				for (int i = 0; i < ids.length; ++i) {
					// Update the qty of the table books
					sqlStr = "UPDATE books SET qty = qty - 1 WHERE id = " + ids[i];
					out.println("<p>" + sqlStr + "</p>"); // for debugging
					count = stmt.executeUpdate(sqlStr);
					out.println("<p>" + count + " record updated.</p>");

					// Create a transaction record
					sqlStr = "INSERT INTO order_records (id, qty_ordered) VALUES (" + ids[i] + ", 1)";
					out.println("<p>" + sqlStr + "</p>");
					count = stmt.executeUpdate(sqlStr);
					out.println("<p>" + count + " record inserted.</p>");
					out.println("<h3>Your order for book id=" + ids[i] + " has been confirmed.</h3>");
				}
				out.println("<h3>Thank you.<h3>");
			} else {
				out.println("<h3>Please go back and select a book...</h3>");
			}
		} catch (Exception ex) {
			out.println("<p>Error: " + ex.getMessage() + "</p>");
			out.println("<p>Check Tomcat console for details.</p>");
			ex.printStackTrace();
		}

		out.println("</body></html>");
		out.close();
	}
}