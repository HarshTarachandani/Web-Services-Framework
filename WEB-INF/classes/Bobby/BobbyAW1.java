package Bobby;
import com.thinking.machines.webrock.annotations.*;
import com.thinking.machines.webrock.pojo.*;
@InjectApplicationScope
@Path("/BobbyAW1")
public class BobbyAW1
{

@AutoWired(name="xyz")
private Details myDetails;
private ApplicationScope applicationScope;
public void setApplicationScope(ApplicationScope applicationScope)
{
this.applicationScope=applicationScope;
System.out.println("Application scope object successfully set");
}
@Path("/setDataInApplicationScope")
public void setDataInApplicationScope()
{
this.applicationScope.setAttribute("xyz",new Details());
System.out.println("xyz is set");
}

public void setMyDetails(Details d)
{
this.myDetails=d;
}
@Path("/getXyzFromApplicationScope")
public void getXyzFromApplicationScope()
{
System.out.println("Name:"+this.myDetails.name+"\n Address:"+this.myDetails.address+"\n Pin code:"+this.myDetails.pin);
}

}