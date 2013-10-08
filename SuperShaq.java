import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class SuperShaq {

	public static Person[] inputPatientData = new Person[300];
	public static Hospital[] hospitals = new Hospital[5];
	public static List<Ambulance> ambulances = new ArrayList<Ambulance>();

	public static Location[] finalCenters = new Location[5];
	
	public static HashMap<Location, int[]> ambulancePaths = new HashMap<Location,int[] >(5);
	
	public static int dist(Location a, Location b) {
		return Math.abs(a.x - b.x) + Math.abs(a.y - b.y);
	}
	
	public static int dist(Location a, Person b) {
		return Math.abs(a.x - b.location.x) + Math.abs(a.y - b.location.y);
	}
	
	public static int dist(Person a, Person b) {
		return Math.abs(a.location.x - b.location.x) + Math.abs(a.location.y - b.location.y);
	}
	
	public static int closestHospital(Location[] a, Person p) {
		
		int min1= Math.min(dist(a[0],p), dist(a[1],p));
		int min2= Math.min(min1, dist(a[2],p));
		int min3= Math.min(min2, dist(a[3],p));
		int min4= Math.min(min3, dist(a[4],p));
		
		for(int i=0;i<5;i++){
			if(dist(a[i],p)==min4)
				return i;
		}
		return -1;
	}
	

	private static Location[] findNewCenters(List<List<Person>> clusters) {
			Location[] newCenters = new Location[5];
		
			for(int i =0; i<5;i++){
				List<Person> cluster = clusters.get(i);
				int sumx = 0;
				int sumy = 0;
				for(Person p: cluster){
					sumx+=p.location.x;
					sumy+=p.location.y;
				}
				newCenters[i] = new Location();
				newCenters[i].x= sumx/cluster.size();
				newCenters[i].y= sumy/cluster.size();
			}
		return newCenters;
	}
	
	
	
	
	public static List<List<Person>> clusterMain(Person[] points) {
		
		List<List<Person>> clusters = new ArrayList<List<Person>>(5);

		Comparator<Person> xComparatorAscen = new Comparator<Person>() {
			public int compare(Person a, Person b) {
				if (a.location.x < b.location.x)
					return 1;
				else {
					if (a.location.x > b.location.x)
						return -1;
					else
						return 0;
				}
			}
		};

		Comparator<Person> yComparatorAscen = new Comparator<Person>() {
			public int compare(Person a, Person b) {
				if (a.location.y < b.location.y)
					return 1;
				else {
					if (a.location.y > b.location.y)
						return -1;
					else
						return 0;
				}
			}
		};

		Comparator<Person> TTDComparatorDesc = new Comparator<Person>() {
			public int compare(Person a, Person b) {
				if (a.rescueTime > b.rescueTime)
					return 1;
				else {
					if (a.rescueTime < b.rescueTime)
						return -1;
					else
						return 0;
				}
			}
		};

		Person[] sortedByTTD = points;
		Arrays.sort(sortedByTTD, TTDComparatorDesc);

		Person[] sortedByX = new Person[150];
		Person[] sortedByY = new Person[150];

		System.arraycopy(sortedByTTD, 0, sortedByX, 0, 150);
		System.arraycopy(sortedByTTD, 0, sortedByY, 0, 150);

		Arrays.sort(sortedByX, xComparatorAscen);
		Arrays.sort(sortedByY, yComparatorAscen);

		Location[] initialCenters = { sortedByX[0].location,
				sortedByX[149].location, sortedByY[0].location,
				sortedByY[149].location, sortedByX[74].location };
		
		finalCenters = initialCenters;
		
		for(int i=0;i<5;i++){
			clusters.add(new ArrayList<Person>());
		}
		
		
		boolean centerChanged = true;
		while(centerChanged){
			
			List<List<Person>> newClusters = new ArrayList<List<Person>>(5);
			
			for(int i=0;i<5;i++){
				newClusters.add(new ArrayList<Person>());
			}
			
			centerChanged = false;
			for(int i = 0; i <150; i++){
				int clusterToAddTo = closestHospital(finalCenters, sortedByTTD[i]);
				newClusters.get(clusterToAddTo).add(sortedByTTD[i]);
			}
			
			finalCenters = findNewCenters(newClusters);
			
			for (int i = 0; i < 5; i++) {
                if ((initialCenters[i].x - finalCenters[i].x >2) || (initialCenters[i].y - finalCenters[i].y >2)) {
                    centerChanged = true;    
                }                 
			}
			initialCenters = finalCenters;
			clusters = newClusters;
		}

		return clusters;
	}

	

	public static void StartTheMagic() {

		List<List<Person>> clusters = clusterMain(inputPatientData);
		
		
		Collections.sort(clusters, new Comparator<List<Person>>() {
			@Override
            public int compare(List<Person> a, List<Person> b) {
                if(a.size()<b.size())
                	return 1;
                else{
                	if(a.size()>b.size())
                		return -1;
                	else
                		return 0;
                }  
            }
		});
		
		Arrays.sort(hospitals, new Comparator<Hospital>() {
			@Override
            public int compare(Hospital a, Hospital b) {
                if(a.numOfAmbulance<b.numOfAmbulance)
                	return 1;
                else{
                	if(a.numOfAmbulance>b.numOfAmbulance)
                		return -1;
                	else
                		return 0;
                }  
            }
		});
		
		int j=0;
		int offset = 0 ;
		for(List<Person> cluster : clusters ){
			
			for(int i=0;i<hospitals[j].numOfAmbulance;i++){
				boolean pathFound = false; 
				ambulances.get(offset).location = hospitals[j].location;
				offset++;
				
				
				while(!pathFound){
					Random rand = new Random(); 
					
					ArrayList<Person> tempPath = new ArrayList<Person>();
					boolean found4 = false;
					while(true){
						if(tempPath.size() == 4 || cluster.size() == 0){
							break;
						}
						int idtry = rand.nextInt(cluster.size());
						Person p = cluster.get(idtry);
						if(p.underConsideration == false && p.rescued == false){
							p.underConsideration = true;
							tempPath.add(p);
							cluster.remove(idtry);		
						}
					}
					List<List<Person>> possiblePaths =  permute(tempPath);
					
					for(List<Person> pl : possiblePaths){
						for(Person pp : pl ){
							System.out.println("Path for ambulance " + offset + pp.id + " " + pp.location.x + " " + pp.location.y);
						}
					}		
				}
			}	
		}		
	}
	
	
	public static List<List<Person>> permute(ArrayList <Person> people) {
	    List<List<Person>> allPaths = new ArrayList<List<Person>>();
		
	    if(people ==null){
	    	return null;
	    }
	    
	    for(Person p : people ){
	    	ArrayList <Person> temp = new ArrayList<Person>(people);
			temp.remove(p);
			for(List<Person> pl : permute(temp)){
				List<Person> newPath = new ArrayList<Person>();
				newPath.add(p);
				newPath.addAll(pl);
				allPaths.add(newPath);
			}
		}
	    
	    return allPaths;
	}

	private static List<Person> getBestPath(List<Person> tempPath,
			Ambulance ambulance) {
		
		int startTime = ambulance.time;
		int minRescueTime = tempPath.get(0).rescueTime;
		for(Person p : tempPath){
			if(p.rescueTime < minRescueTime)
				minRescueTime= p.rescueTime;
		}
		
		
		
		
		return null;
	}

	public static void main(String[] args) {

		try {
			BufferedReader reader = new BufferedReader(
					new FileReader(
							"C:\\Users\\Akshay\\eclipse-workspace\\AmbulanceProblem\\src\\input.txt"));

			String line = null;
			line = reader.readLine();
			int count = 0;
			while ((line = reader.readLine()) != null) {

				String[] parts = line.split(",");

				inputPatientData[count] = new Person();
				inputPatientData[count].id = count;
				inputPatientData[count].rescued = false;
				inputPatientData[count].location.x = Integer.parseInt(parts[0]);
				inputPatientData[count].location.y = Integer.parseInt(parts[1]);
				inputPatientData[count].rescueTime = Integer.parseInt(parts[2]);

				count++;
				if (count == 300) {
					break;
				}
			}

			reader.readLine();
			reader.readLine();
			reader.readLine();

			count = 0;
			int ambId = 0;
			while ((line = reader.readLine()) != null) {
				System.out.println(line);
				hospitals[count] = new Hospital();
				hospitals[count].id = count;
				hospitals[count].numOfAmbulance = Integer.parseInt(line);
				hospitals[count].initialAmbulanceList = new int[hospitals[count].numOfAmbulance];
				for (int i = 0; i < hospitals[count].numOfAmbulance; i++) {
					Ambulance ambulance = new Ambulance();
					ambulance.id = ambId;
					ambulances.add(ambulance);
					hospitals[count].initialAmbulanceList[i] = ambId;
					ambId++;
				}
				count++;
			}

			StartTheMagic();
			
			reader.close();
		} catch (Exception e) {
			System.out.println(e);
			e.printStackTrace();
		}
		
	}

}
