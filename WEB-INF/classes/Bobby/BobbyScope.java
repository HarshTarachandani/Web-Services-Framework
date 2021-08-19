package Bobby;
import com.thinking.machines.webrock.annotations.*;
import com.thinking.machines.webrock.pojo.*;
@InjectRequestScope
@Path("/BobbyScope")
public class BobbyScope
{
private RequestScope requestScope;
public void setRequestScope(RequestScope requestScope)
{
this.requestScope=requestScope;
System.out.println("Request scope object successfully set");
}
@Forward("/BobbyScope/getDataFromRequestScope")
@Path("/setDataInRequestScope")
public void setDataInRequestScope()
{
this.requestScope.setAttribute("title","HOD");
System.out.println("Title set");
}
@Path("/getDataFromRequestScope")
public void getDataFromRequestScope()
{
System.out.println("Result :"+this.requestScope.getAttribute("title"));
}

}