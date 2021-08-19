package Bobby;
import com.thinking.machines.webrock.annotations.*;
@Path("/BobbyRP")
public class BobbyRP
{
@Path("/getId")
public void getId(@RequestParameter("identity") String id)
{
	System.out.println("Your identity fetched from Request Scope is:"+id);
}
}