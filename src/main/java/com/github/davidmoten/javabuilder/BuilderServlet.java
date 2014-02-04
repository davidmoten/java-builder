package com.github.davidmoten.javabuilder;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class BuilderServlet extends HttpServlet {

	private static final long serialVersionUID = -5448993933103327534L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String code = req.getParameter("code");
		ByteArrayInputStream bytes = new ByteArrayInputStream(code.getBytes());
		String result = new Generator().generate(bytes);
		resp.setContentType("text/plain");
		resp.getWriter().write(result);
	}
}
