package Bobby;
import com.thinking.machines.webrock.annotations.*;
@Path("/BobbyChhapri")
public class BobbyStart
{
@OnStartup(priority=0)
@Path("/naam")
public void naam()
{
	System.out.println("Naam:Bobby Start 0.");
}
@Path("/dostKaNaam")
@OnStartup(priority=0)
public void dostKaNaam()
{
	System.out.println("dost ka naam:Chintu Start 0.");
}
@Path("/pata")
@OnStartup(priority=5)
public void pata()
{
	System.out.println("C/99 Sant Nagar,Shipla apartment k samne,agar road [Start priority=5].");
}
}