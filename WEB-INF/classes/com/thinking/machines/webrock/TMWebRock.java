package com.thinking.machines.webrock;
import com.thinking.machines.webrock.pojo.*;
import com.thinking.machines.webrock.annotations.*;
import com.thinking.machines.webrock.model.*;
import com.thinking.machines.webrock.exceptions.*;
import java.util.*;
import java.io.*;
import java.lang.reflect.*;
import javax.servlet.*;
import javax.servlet.http.*;
import com.google.gson.*;

public class TMWebRock extends HttpServlet
{
 public void doGet(HttpServletRequest rq,HttpServletResponse rs)
 {
 try{
  ServletContext servletContext;
  File file=null;
  HttpSession httpSession;
  HttpServletRequest httpServletRequest=rq;
  PrintWriter pw=rs.getWriter();
  String servletName=rq.getRequestURL().toString();
  int indx=servletName.indexOf("TMWebRock");
  servletName=servletName.substring(indx+9);
  WebRockModel wrm=(WebRockModel)rq.getServletContext().getAttribute("WEBROCKMODEL");
  System.out.println(servletName+"is called..");
  
  Class clss;
  Method method;
  String path,forwardTo,s0="";
  Service service;
  Map map=(Map)wrm.servicesMap;
  System.out.println(map.keySet());
  while(true)
  {
   service=(Service)map.get(servletName);
   if(service==null)
   {
    rs.sendError(404,"Requested service not found in "+servletName); // or HttpServletResponse.NOT_FOUND
    return;   
   }
   else
   {
    System.out.println("Servlet :"+servletName+" found.");
    if(service.isPost==true && service.isGet==false)
    {
     rs.sendError(405);  // or HttpServletResponse.METHOD_NOT_ALLOWED
     return;   
    }
    clss=service.serviceClass;
    method=service.service;
    path=service.path;
    forwardTo=service.forwardTo;
    Object obj=clss.newInstance();
    if(s0.equals(forwardTo) && forwardTo.length()>0)
    {
     System.out.println("infinite recursive forwarding found between :  "+ s0+" and "+servletName); // or send some error.
     return;
    }

//checking for injecting operations
      injectProperty(rq,clss,obj,service,file);
//Autowired property check and apply
    autoWiredProperySetter(rq,rs,clss,obj);
//InjectRequestParameter property check and apply
    injectRequestParameter(rq,rs,clss,obj);
//checking for @RequestParameter and other parameters of the service
    Object params[]=null;
    boolean requestParameterFound=false;
    params=serviceParameterHandler(rq,rs,method);
    if(params!=null) requestParameterFound=true;
// @SecuredAccess operations
    System.out.println("securedAccess is about to invoke");
    boolean seccessfullGuardOperation=false;
    if(service.securedAccess)
    {    
     seccessfullGuardOperation=securedAccessHandler(rq,rs,method);    
     if(!seccessfullGuardOperation)
     {
       System.out.println("Some problem occured during @SecuredAccess..");
       return;
     }
    }
    Object result;
    if(requestParameterFound)
    {
     System.out.println("parametes found in service");
     for(Object pr:params)
     {
      System.out.println(pr);
     }
     System.out.println("The service is about to invoke");
     result=method.invoke(obj,params);
    }
    else
    {
     result=method.invoke(obj);
    }
    ServiceResponse sr=new ServiceResponse();
    sr.setResult(result);
    sr.setIsSuccess(true);
    sr.setException(null);
    pw.print(result);// send jason instead(#workToDo)
    }
   if(forwardTo.length()==0 || forwardTo.equals(servletName))
   {
    return; // or send some error related same servlet forwarding.
   }
   s0=servletName;
   servletName=forwardTo;
  }
 }
 catch(Exception ee){
System.out.println(ee);
System.out.println(ee.getMessage());
 }
 }

 public boolean securedAccessHandler(HttpServletRequest rq,HttpServletResponse rs,Method method)
 {
  try
  {
  SecuredAccess securedAccess=(SecuredAccess)method.getAnnotation(SecuredAccess.class);
  String className=securedAccess.checkPost();
  String methodName=securedAccess.guard();
  if(className.length()==0 || methodName.length()==0)
  {
    System.out.println("@SecuredAccess require checkPost and guard with a string.");
    return false;
  }
  Class clss=Class.forName(className);
  Object checkPost=clss.newInstance();
  Method methods[]=clss.getDeclaredMethods();
  Field fields[]=clss.getDeclaredFields();
  Method guard=null;
  for(Method m:methods)
  {
   if(m.getName().equals(methodName)) guard=m;
  }
  File file=null;
  Service service=new Service();
  //preparing service object to perform different operations
  InjectApplicationScope ias;
  InjectSessionScope iss;
  InjectApplicationDirectory iad;
  InjectRequestScope irs;
//  InjectRequestParameter irp;
  ias=(InjectApplicationScope)clss.getAnnotation(InjectApplicationScope.class);
  if(ias!=null) service.injectApplicationScope=true;
  
  iss=(InjectSessionScope)clss.getAnnotation(InjectSessionScope.class);
  if(iss!=null) service.injectSessionScope=true;

  iad=(InjectApplicationDirectory)clss.getAnnotation(InjectApplicationDirectory.class);
  if(iad!=null) service.injectApplicationDirectory=true;

  irs=(InjectRequestScope)clss.getAnnotation(InjectRequestScope.class);
  if(irs!=null) service.injectRequestScope=true;
//checking and performing injecting operations
  injectProperty(rq,clss,checkPost,service,file);
//Autowired property check and apply
    autoWiredProperySetter(rq,rs,clss,checkPost);
//InjectRequestParameter property check and apply
    injectRequestParameter(rq,rs,clss,checkPost);
//checking for @RequestParameter
    Object params[]=null;
    boolean requestParameterFound=false;
    params=serviceParameterHandler(rq,rs,guard);
    if(params!=null) requestParameterFound=true;
    System.out.println("The guard name is:"+guard.getName());
    if(requestParameterFound)
    {
     System.out.println("parametes found in guard provided in @securedAccess annotaion");
     for(Object pr:params)
     {
      System.out.println(pr);
     }
     System.out.println("The guard provided in @securedAccess annotaion is about to invoke");
     try{
     guard.invoke(checkPost,params);
     }catch(Exception e) 
     {
      rs.sendError(404,e.getMessage()); 
      System.out.println(e);
      return false;
     }
    }
    else
    {
     try{
     System.out.println("No parametes found in guard provided in @securedAccess annotaion");
     System.out.println("The guard provided in @securedAccess annotaion is about to invoke");
     guard.invoke(checkPost);
     }catch(Exception e) 
     {
      rs.sendError(404,e.getMessage()); 
      System.out.println(e);
      return false;
     }
   }
}catch(Exception ee){
System.out.println(ee+"is the final exception of securedAccess...");
System.out.println(ee.getMessage());
return false;
 }
   return true;
 }// secureAccessHandler ends here

 public void autoWiredProperySetter(HttpServletRequest rq,HttpServletResponse rs,Class clss,Object obj)
 {
   try{
    AutoWired aw;
    Field fields[]=clss.getDeclaredFields();    
    for(Field field:fields)
    {
      aw=(AutoWired)field.getAnnotation(AutoWired.class);
      if(aw!=null)
      {
       String attributeName=aw.name();
       if(attributeName.length()==0) 
       {
         System.out.println("Nothing found in Auto wired annotaion,require String.");
         continue;
       }
       Object attribute=null;
       String autoWiredPropertySetterName="";
       autoWiredPropertySetterName="set"+field.getName().substring(0,1).toUpperCase()+field.getName().substring(1);
       System.out.println("setter for autowired property is: "+autoWiredPropertySetterName);
       boolean autoWiredPropertyFound=false;
       if(rq.getAttribute(attributeName)!=null)
       {
        attribute=rq.getAttribute(attributeName);
        autoWiredPropertyFound=true;
       }
       else if(rq.getSession().getAttribute(attributeName)!=null)
       {
        attribute=rq.getSession().getAttribute(attributeName);
        autoWiredPropertyFound=true;
       }
       else if(getServletContext().getAttribute(attributeName)!=null)
       {
        attribute=getServletContext().getAttribute(attributeName);
        autoWiredPropertyFound=true;
       }
       if(autoWiredPropertyFound)
       {
        String type1=attribute.getClass().getName();
        String type2=field.getType().getName();
        System.out.println("attribute is of type :"+type1);
        System.out.println("field is of type :"+type2);
        if(!type2.equals(type1))
        {
          System.out.println("autowired property to be set and asked were of different types..");
          return;
        }
       }
       System.out.println(autoWiredPropertySetterName);
       Method methods[]=clss.getDeclaredMethods();
       for(Method tempMethod:methods)
       {
        if(tempMethod.getName().equals(autoWiredPropertySetterName))
        {
         if(!tempMethod.getReturnType().getName().equals("void"))
         {
          System.out.println("The method :"+autoWiredPropertySetterName+" should be of return type =void...");
         }
        System.out.println("Attribute is"+attribute);
         tempMethod.invoke(obj,attribute);
        }
       }
     
      }

    }
 }catch(Exception ee)
 {
  System.out.println(ee+"Exception by Autowired property");
  System.out.println(ee.getMessage());
 }
}
 public void injectRequestParameter(HttpServletRequest rq,HttpServletResponse rs,Class clss,Object obj)
 {
   try{
    InjectRequestParameter irp;
    Field fields[]=clss.getDeclaredFields();    
    for(Field field:fields)
    {
      irp=(InjectRequestParameter)field.getAnnotation(InjectRequestParameter.class);
      if(irp!=null)
      {
       String parameterName=irp.value();
       if(parameterName.length()==0) 
       {
         System.out.println("Nothing found in InjectRequestParameter annotaion,require String.");
         continue;
       }
       String parameter=null;
       Object param=null;
       String requestParameterPropertySetterName="";
       requestParameterPropertySetterName="set"+field.getName().substring(0,1).toUpperCase()+field.getName().substring(1);
       boolean requestParameterPropertyFound=false;
       if(rq.getParameter(parameterName)!=null)
       {
        parameter=rq.getParameter(parameterName);
        requestParameterPropertyFound=true;
       }
       if(requestParameterPropertyFound)
       {
        String type=field.getType().getName();

      if(type.equalsIgnoreCase("INT"))
      {
       param=Integer.parseInt(parameter);
      }
      else if(type.equalsIgnoreCase("LONG"))
      {
       param=Long.parseLong(parameter); 
      }
      else if(type.equalsIgnoreCase("SHORT"))
      { 
       param=Short.parseShort(parameter);
      }
      else if(type.equalsIgnoreCase("BYTE"))
      {
       param=Byte.parseByte(parameter);
      } 
      else if(type.equalsIgnoreCase("FLOAT"))
      {
       param=Float.parseFloat(parameter);
      }
      else if(type.equalsIgnoreCase("DOUBLE"))
      {
       param=Double.parseDouble(parameter);
      }
      else if(type.equalsIgnoreCase("CHAR"))
      {
       param=parameter.charAt(0);
      }
      else if(type.equalsIgnoreCase("BOOLEAN"))
      {
       param=Boolean.parseBoolean(parameter);
      } 
      else if(type.equalsIgnoreCase("java.lang.String"))
      {
       param=parameter;
      }

        System.out.println("field is of type :"+type);
       }
       System.out.println(requestParameterPropertySetterName);
       Method methods[]=clss.getDeclaredMethods();
       for(Method tempMethod:methods)
       {
        if(tempMethod.getName().equals(requestParameterPropertySetterName))
        {
         if(!tempMethod.getReturnType().getName().equals("void"))
         {
          System.out.println("The method :"+requestParameterPropertySetterName+" should be of return type =void...");
         }
        System.out.println("Attribute for InjectRequestParameter is"+parameter);
         tempMethod.invoke(obj,param);
        }
       }
     
      }

    }
 }catch(Exception ee)
 {
  System.out.println(ee+"Exception by injectRequestParameter");
  System.out.println(ee.getMessage());
 }
}
public void injectProperty(HttpServletRequest rq,Class clss,Object obj,Service service,File file)
{
  try{
    if(service.injectApplicationDirectory)
    {
     String realPath=getServletContext().getRealPath(".");
     realPath=realPath+"WEB-INF"+File.separator+"classes"+File.separator;
     file=new File(realPath);
     ApplicationDirectory appD=new ApplicationDirectory(file);
     Method methods[]=clss.getDeclaredMethods();
     for(Method tempMethod:methods)
     {
      if(tempMethod.getName().equals("setApplicationDirectory"))
      {
        if(!tempMethod.getReturnType().getName().equals("void"))
        {
          System.out.println("The method :setApplicationDirectory() should be of return type =void...");
        }
        tempMethod.invoke(obj,appD);
      }
     }
    }



    if(service.injectSessionScope)
    {
     SessionScope sessionScope=new SessionScope(rq.getSession());
     Method methods[]=clss.getDeclaredMethods();
     for(Method tempMethod:methods)
     {
      if(tempMethod.getName().equals("setSessionScope"))
      {
        if(!tempMethod.getReturnType().getName().equals("void"))
        {
          System.out.println("The method :setSessionScope() should be of return type =void...");
        }
        tempMethod.invoke(obj,sessionScope);
      }
     }
    }



    if(service.injectRequestScope)
    {
     RequestScope requestScope=new RequestScope(rq);
     Method methods[]=clss.getDeclaredMethods();
     String name;
     for(Method tempMethod:methods)
     {
      name=tempMethod.getName();
      if(name.equals("setRequestScope"))
      {
        System.out.println(name.equals("setRequestScope"));
        System.out.println("SetterFound...");
        System.out.println(name);
        if(!tempMethod.getReturnType().getName().equals("void"))
        {
          System.out.println("The method :setRequestScope() should be of return type =void...");
        }
        System.out.println("just about to invoke");
        tempMethod.invoke(obj,requestScope);
      }
     }
    }


    if(service.injectApplicationScope)
    {
     ApplicationScope applicationScope=new ApplicationScope(getServletContext());
     Method methods[]=clss.getDeclaredMethods();
     for(Method tempMethod:methods)
     {
      if(tempMethod.getName().equals("setApplicationScope"))
      {
        if(!tempMethod.getReturnType().getName().equals("void"))
        {
          System.out.println("The method :setApplicationScope() should be of return type =void...");
        }
        tempMethod.invoke(obj,applicationScope);
        System.out.println("Setter invoked");
      }
     }
    }
  }catch(Exception ee)
  {
   System.out.println(ee+"Exception by injectProperty");
   System.out.println(ee.getMessage());
  }
}

public Object[] serviceParameterHandler(HttpServletRequest rq,HttpServletResponse rs,Method method)
{
  try
  {
    Parameter parameters[]=method.getParameters();
    Object []params=new Object[parameters.length];
    RequestParameter requestParameter;
    boolean requestParameterAnnotationFound=false;
    int i=0;
    for(Parameter parameter:parameters)
    {
     String type=parameter.getType().getName();
     requestParameter=(RequestParameter)parameter.getAnnotation(RequestParameter.class);
     if(requestParameter!=null) requestParameterAnnotationFound=true;
     if(!requestParameterAnnotationFound)
     {
      if(type.equalsIgnoreCase("com.thinking.machines.webrock.pojo.ApplicationScope"))
      {
       params[i]=new ApplicationScope(getServletContext());
      }
      else if(type.equalsIgnoreCase("com.thinking.machines.webrock.pojo.SessionScope"))
      {
       params[i]=new SessionScope(rq.getSession());
      }
      else if(type.equalsIgnoreCase("com.thinking.machines.webrock.pojo.RequestScope"))
      {
       params[i]=new RequestScope(rq);
      }
      else if(type.equalsIgnoreCase("com.thinking.machines.webrock.pojo.ApplicationDirectory"))
      {
       String realPath=getServletContext().getRealPath(".");
       realPath=realPath+"WEB-INF"+File.separator+"classes"+File.separator;
       File ff=new File(realPath);
       params[i]=new ApplicationDirectory(ff);
      }

// in case of json
     
     else
     {
      BufferedReader br=rq.getReader();
      System.out.println("after getting reader:"+rq.getReader());
      StringBuffer b=new StringBuffer();
      String d;
      while(true)
      {
       d=br.readLine();
       if(d==null) break;
       b.append(d);
      }
      //toComplete json is not converting in object
      String rawData=b.toString();
      Gson gson=new Gson();
      System.out.println("Rawdata is:"+rawData);
      params[i]=gson.fromJson(rawData,parameter.getType());
      System.out.println("the type of the object form the json is :"+parameter.getType()+" and eg:");
      }
      System.out.println("param "+i+" is "+params[i]);
      i++;
      continue;
     }
    

     String parameterName=requestParameter.value();
     if(parameterName.length()==0)
     {
      System.out.println("required String in annotation @RequestParameter,found nothing... ");
      return null;
     }
     String val=rq.getParameter(parameterName);
     if(val==null)
     {
      System.out.println("cannot find the requested parameter in request scope...");
      rs.sendError(404,"cannot find the requested parameter in request scope...");
      return null;
     }
     
     if(type.equalsIgnoreCase("INT"))
     {
      params[i]=Integer.parseInt(val);
     }
     else if(type.equalsIgnoreCase("LONG"))
     {
      params[i]=Long.parseLong(val); 
     }
     else if(type.equalsIgnoreCase("SHORT"))
     {
      params[i]=Short.parseShort(val);
     }
     else if(type.equalsIgnoreCase("BYTE"))
     {
      params[i]=Byte.parseByte(val);
     }
     else if(type.equalsIgnoreCase("FLOAT"))
     {
      params[i]=Float.parseFloat(val);
     }
     else if(type.equalsIgnoreCase("DOUBLE"))
     {
      params[i]=Double.parseDouble(val);
     }
     else if(type.equalsIgnoreCase("CHAR"))
     {
      params[i]=val.charAt(0);
     }
     else if(type.equalsIgnoreCase("BOOLEAN"))
     {
      params[i]=Boolean.parseBoolean(val);
     } 
     else if(type.equalsIgnoreCase("java.lang.String"))
     {
      params[i]=val;
     } 
     i++;
    }
    System.out.println("final count of all parameters is:"+i);
    if(i==parameters.length) return params;
    return null;
 }catch(Exception ee)
  {
   System.out.println(ee+"Exception by serviceParameterHandler");
   System.out.println(ee.getMessage());
  }
return null;
}
public void doPost(HttpServletRequest rq,HttpServletResponse rs)
{
doGet(rq,rs);
}
}
