package com.thinking.machines.webrock.pojo;
import javax.servlet.*;
import javax.servlet.http.*;
public class SessionScope
{
private HttpSession httpSession;
public SessionScope(HttpSession httpSession){
this.httpSession=httpSession;
}
public void setAttribute(String attribute,Object value)
{
this.httpSession.setAttribute(attribute,value);
}
public Object getAttribute(String attribute)
{
return this.httpSession.getAttribute(attribute);
}
}