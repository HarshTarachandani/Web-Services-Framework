package com.thinking.machines.webrock.pojo;
import java.lang.annotation.*;
import java.lang.reflect.*;  
public class Service implements java.io.Serializable
{
public Class serviceClass;
public String path;
public Method service;
public boolean isPost;
public boolean isGet;
public String forwardTo;
public boolean injectApplicationDirectory;
public boolean injectSessionScope;
public boolean injectApplicationScope;
public boolean injectRequestScope;
public boolean injectRequestParameter;
public boolean securedAccess;
private Annotation[] annotations;
public Service()
{
	isGet=false;
	isPost=false;
	forwardTo="";
	injectApplicationDirectory=false;
	injectSessionScope=false;
	injectApplicationScope=false;
	injectRequestScope=false;
	injectRequestParameter=false;
	securedAccess=false;
}
public void setAnnotations(Annotation[] annotations)
 {
  this.annotations=annotations; 
 }
 public Annotation[] getAnnotations()
 {
  return this.annotations;
 }



}