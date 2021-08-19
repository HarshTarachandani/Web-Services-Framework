package com.thinking.machines.webrock;
import java.io.*;
import javax.servlet.http.*;
public class test extends HttpServlet
{
 public void doGet(HttpServletRequest rq,HttpServletResponse rs)
 {
 try{
		PrintWriter pw=rs.getWriter();
		pw.print("Bhosdike");

	}catch(Exception ee)
	{
		System.out.println(ee);
	}
}
}