package Bobby;
import com.thinking.machines.webrock.annotations.*;
import com.thinking.machines.webrock.pojo.*;
@InjectApplicationScope
@Path("/BobbyAW")
public class BobbyAW
{
@AutoWired(name="identity")
private String myIdentity;
public void setMyIdentity(String i)
{
this.myIdentity=i;
System.out.println("Autowired property is set as identity:"+this.myIdentity);
}
@Path("/getIdentityFromApplicationScope")
public void getIdentityFromApplicationScope()
{
System.out.println(this.myIdentity+" (successful execution of AutoWired property example.)");
}

}