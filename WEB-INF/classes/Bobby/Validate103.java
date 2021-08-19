package Bobby;
import com.thinking.machines.webrock.annotations.*;
import com.thinking.machines.webrock.pojo.*;
import com.thinking.machines.webrock.exceptions.*;
@InjectSessionScope
public class Validate103
{
@InjectRequestParameter("xyz")
private String irp;
private SessionScope sessionScope;
public void setIrp(String irp)
{
this.irp=irp;
}
public void setSessionScope(SessionScope sessionScope)
{
this.sessionScope=sessionScope;
}
public void validate(ApplicationScope as,SessionScope ss,@RequestParameter("xyz") String xyz) throws ServiceException
{
try{
String str=(String)ss.getAttribute("Name");
if(str==null) throw new ServiceException("Unathorized access,unable to proceed...");
System.out.println(str+" from validate guard of Validate103 checkpost............test 1 for SessionScope  parameters done");
System.out.println(xyz+" from validate guard of Validate103 checkpost............test 2 for @RequestParameter done");
System.out.println(sessionScope.getAttribute("Name")+" from validate guard of Validate103 checkpost............test 3 for InjectSessionScope   done");
System.out.println(irp+" from validate guard of Validate103 checkpost............test 4 for @InjectRequestParameter done");

}catch(Exception ee)
{
	System.out.println(ee);
	throw new ServiceException(ee+" from ServiceException");
}
}}