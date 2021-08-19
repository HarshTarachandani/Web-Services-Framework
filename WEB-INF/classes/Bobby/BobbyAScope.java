package Bobby;
import com.thinking.machines.webrock.annotations.*;
import com.thinking.machines.webrock.pojo.*;
@InjectApplicationScope
@Path("/BobbyAScope")
public class BobbyAScope
{
private ApplicationScope applicationScope;
public void setApplicationScope(ApplicationScope applicationScope)
{
this.applicationScope=applicationScope;
System.out.println("Application scope object successfully set");
}
@Path("/setDataInApplicationScope")
public void setDataInApplicationScope()
{
this.applicationScope.setAttribute("identity","Indian(From application scope)");
System.out.println("Title set");
}
@Path("/getDataFromApplicationScope")
public void getDataFromApplicationScope()
{
System.out.println(this.applicationScope.getAttribute("identity")+" ,by invoking getDataFromApplicationScope of BobbyAScope");
}

}