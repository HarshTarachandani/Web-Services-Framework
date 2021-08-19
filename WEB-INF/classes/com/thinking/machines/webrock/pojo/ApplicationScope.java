package com.thinking.machines.webrock.pojo;
import javax.servlet.*;
public class ApplicationScope
{
private ServletContext servletContext;
public ApplicationScope(ServletContext servletContext){
this.servletContext=servletContext;
}
public void setAttribute(String attribute,Object value)
{
this.servletContext.setAttribute(attribute,value);
}
public Object getAttribute(String attribute)
{
return this.servletContext.getAttribute(attribute);
}
}