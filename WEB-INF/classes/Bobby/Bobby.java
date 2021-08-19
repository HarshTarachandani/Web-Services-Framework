package Bobby;
import com.thinking.machines.webrock.annotations.*;
@Path("/Bobby")
public class Bobby
{
@Path("/first")
@Forward("/Bobby/second")
public String first()
{
	return "first method got called...";
}
@Path("/second")
@OnStartup(priority=1)
public void second()
{
	System.out.println("Bobby priority =1");
}
@Path("/secondSecond")
@OnStartup(priority=2)
public void secondSecond()
{
	System.out.println("Bobby priority =2");
}
@Path("/third")
public String third()
{
	return "third method got called...";
}
@Path("/fourth")
@OnStartup(priority=4)
public void fourth()
{
	System.out.println("Bobby priority =4");
}
}