package Bobby;
import com.thinking.machines.webrock.annotations.*;
import com.thinking.machines.webrock.pojo.*;
@Path("/Bobby103")
public class Bobby103
{
@SecuredAccess(checkPost="Validate103",guard="validate")
@Path("/secureMe")
public void secureMe(ApplicationScope applicationScope,SessionScope ss,RequestScope rs,ApplicationDirectory ad,Details dd)
{
System.out.println(applicationScope.getAttribute("identity"));
System.out.println(rs.getAttribute("Address"));
System.out.println(ss.getAttribute("Name"));
System.out.println(ad.getDirectory());
}

}