import java.util.ArrayList;


public class Person {

		
		public Person() {
			this.location = new Location();
			this.peopleNearby = new int[300];
			this.rescued = false;
			this.underConsideration = false;
		}
		
		public int id;
		public Location location;
		public int rescueTime;
		public boolean underConsideration;
		public boolean rescued;
		public int[] peopleNearby;
}
