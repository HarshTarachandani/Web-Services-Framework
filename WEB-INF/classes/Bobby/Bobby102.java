package Bobby;
import com.thinking.machines.webrock.annotations.*;
import com.thinking.machines.webrock.pojo.*;
@Path("/Bobby102")
public class Bobby102
{
@Path("/setDataInApplicationScope")
public void setDataInApplicationScope(ApplicationScope applicationScope,SessionScope ss,RequestScope rs)
{
applicationScope.setAttribute("identity","Indian(From application scope test 102)");
ss.setAttribute("Name","Harsh(From Session Scope test 102)");
rs.setAttribute("Address","B/145 Geeta Bhavan ,Indore (From Request Scope test 102)");
}
@Path("/getData")
public void getData(ApplicationScope applicationScope,SessionScope ss,RequestScope rs,ApplicationDirectory ad,Details dd)
{
System.out.println(applicationScope.getAttribute("identity"));
System.out.println(rs.getAttribute("Address"));
System.out.println(ss.getAttribute("Name"));
System.out.println(ad.getDirectory());
System.out.println("The Json after converting is:");
System.out.println(dd.name+","+dd.address+","+dd.pin);
}

}