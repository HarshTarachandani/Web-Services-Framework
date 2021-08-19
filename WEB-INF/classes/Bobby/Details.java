package Bobby;
public class Details implements java.io.Serializable
{
public String name,address;
public int pin;
public Details()
{
 name="Harsh";
 address="B/11 vivekanand colony.";
 pin=456010;
}
public void setName(String name)
{
this.name=name;
}
public void setAddress(String address)
{
this.address=address;
}
public void setPin(int pin)
{
this.pin=pin;
}
}