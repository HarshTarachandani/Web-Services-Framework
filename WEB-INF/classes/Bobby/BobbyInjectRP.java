package Bobby;

import com.thinking.machines.webrock.annotations.*;
import com.thinking.machines.webrock.pojo.*;
@Path("/BobbyInjectRP")
public class BobbyInjectRP
{
@InjectRequestParameter("requestedInjectionParameter")
private float myDetails;

public void setMyDetails(float d)
{
this.myDetails=d;
System.out.println("requested injection operation for requestParameter is done");
}
@Path("/chalao")
public void chalao()
{
this.nonAnnotedMethod(myDetails);
}
public void nonAnnotedMethod(float myDetails)
{
System.out.println("The injected request parameter after calling non annoted method from annoted method is :"+this.myDetails);
}

}