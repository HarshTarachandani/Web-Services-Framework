package com.thinking.machines.webrock.pojo;
import javax.servlet.*;
import javax.servlet.http.*;
public class RequestScope
{
private HttpServletRequest request;
public RequestScope(HttpServletRequest request)
{
this.request=request;
}
public void setAttribute(String attribute,Object value)
{
this.request.setAttribute(attribute,value);
}
public Object getAttribute(String attribute)
{
return this.request.getAttribute(attribute);
}
}