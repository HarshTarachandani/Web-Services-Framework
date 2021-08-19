package com.thinking.machines.webrock;
import com.thinking.machines.webrock.pojo.*;
import com.thinking.machines.webrock.annotations.*;
import com.thinking.machines.webrock.model.*;
import javax.servlet.http.*;
import java.util.*;
import java.util.stream.*;
import java.io.*;
import java.lang.reflect.*;
import java.lang.annotation.*;
public class TMWebRockStartup extends HttpServlet
{
public void init()
{
String packagePrefix=getServletContext().getInitParameter("SERVICE_PACKAGE_PREFIX");

String className="";
try{
File folder = new File("C:/tomcat9/webapps/TMWEBRocks/WEB-INF/classes/"+packagePrefix+"/");
System.out.println("packagePrefix is:"+packagePrefix);
File[] listOfFiles = folder.listFiles();
if(listOfFiles==null) 
{
	System.out.println("No such package found");
	return;
}
WebRockModel webRockModel=new WebRockModel();
Map<Integer,ArrayList<Service>> onStartupMap=new HashMap<>();
for (File file : listOfFiles) {
if (file.isFile()) {
if(file.getName().endsWith(".class"))
{
className=file.getName().substring(0,file.getName().length()-6);
System.out.println(packagePrefix+"."+className);
Class c=Class.forName(packagePrefix+"."+className);
System.out.println("class found...");
Path pathC=(Path)c.getAnnotation(Path.class);
Path pathM;
if(pathC==null) continue;
System.out.println(pathC.value()+"=annoted");
Method[] methods=c.getDeclaredMethods();
//finding annoted methods and inserting in map starts form here
Service service;
GET get,getForClass;
POST post,postForClass;
Forward forward;
OnStartup onStartup;
InjectApplicationScope ias;
InjectSessionScope iss;
InjectApplicationDirectory iad;
InjectRequestScope irs;
InjectRequestParameter irp;
SecuredAccess sac;
getForClass=(GET)c.getAnnotation(GET.class); //checking if the whole class is annoted as get or post.
postForClass=(POST)c.getAnnotation(POST.class);

Object obj=c.newInstance();
for(Method m:methods)
{
pathM=(Path)m.getAnnotation(Path.class);
if(pathM!=null)
{
service=new Service();
service.serviceClass=c;
service.path=pathC.value()+pathM.value();
service.service=m;
onStartup=m.getAnnotation(OnStartup.class);


if(onStartup!=null)  // case of startup Servlet
{
int priority=onStartup.priority();
System.out.println(priority);
ArrayList<Service> list;
if(onStartupMap.get(priority)==null)
{
list=new ArrayList<>();
}
else
{
list=onStartupMap.get(priority);
}
list.add(service);
onStartupMap.put(priority,list);
continue;
}


get=(GET)m.getAnnotation(GET.class);
if(get!=null) service.isGet=true;
post=(POST)m.getAnnotation(POST.class);
if(post!=null) service.isPost=true;
forward=(Forward)m.getAnnotation(Forward.class);
if(forward!=null) service.forwardTo=(String)forward.value();

if(getForClass!=null) service.isGet=true;
if(postForClass!=null) service.isPost=true;

//in case the service require scope and directory

ias=(InjectApplicationScope)c.getAnnotation(InjectApplicationScope.class);
if(ias!=null) service.injectApplicationScope=true;
	
iss=(InjectSessionScope)c.getAnnotation(InjectSessionScope.class);
if(iss!=null) service.injectSessionScope=true;

iad=(InjectApplicationDirectory)c.getAnnotation(InjectApplicationDirectory.class);
if(iad!=null) service.injectApplicationDirectory=true;

irs=(InjectRequestScope)c.getAnnotation(InjectRequestScope.class);
if(irs!=null) service.injectRequestScope=true;

sac=(SecuredAccess)m.getAnnotation(SecuredAccess.class);
if(sac!=null) service.securedAccess=true;

webRockModel.servicesMap.put(service.path,service);
}}
}
}
}
System.out.println("---------------------------------------");
getServletContext().setAttribute("WEBROCKMODEL",webRockModel);
System.out.println(getServletContext().getAttribute("WEBROCKMODEL"));

ArrayList<Integer> priorityList=new ArrayList<>(onStartupMap.keySet());
Collections.sort(priorityList);
for(int i:priorityList)
{
for(Service srv:onStartupMap.get(i))
{
srv.service.invoke(srv.serviceClass.newInstance());
}
}

}catch(Exception ee)
{
System.out.println(ee);
}}

}