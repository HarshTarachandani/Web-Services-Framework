package Bobby;
import com.thinking.machines.webrock.annotations.*;
import com.thinking.machines.webrock.pojo.*;
@InjectApplicationScope
@Path("/BobbyAW2")
public class BobbyAW2
{

@AutoWired(name="xyz")
private Integer myDetails;
private ApplicationScope applicationScope;
public void setApplicationScope(ApplicationScope applicationScope)
{
this.applicationScope=applicationScope;
System.out.println("Application scope object successfully set");
}
@Path("/setDataInApplicationScope")
public void setDataInApplicationScope()
{
this.applicationScope.setAttribute("xyz",99999230);
System.out.println("xyz is set");
}

public void setMyDetails(Integer d)
{
this.myDetails=d;
}
@Path("/getXyzFromApplicationScope")
public void getXyzFromApplicationScope()
{
System.out.println("Unique id from BobbyAW2 is:"+this.myDetails);
}

}