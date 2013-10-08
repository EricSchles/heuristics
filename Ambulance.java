import java.util.List;


public class Ambulance {

	public Ambulance() {
		this.location = new Location();
		this.peopleInside = new int[4];
		this.remainingCapacity = 4;
	}
	
	public int id;
	public Location location;
	public int remainingCapacity;
	public int[] peopleInside;
	public int time;

}
