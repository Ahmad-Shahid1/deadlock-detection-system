import java.io.BufferedReader; // this is for reading a file line by line
import java.io.FileReader; // for opening files
import java.io.IOException; // this handles exceptions
import java.util.List; // importing to handle lists
import java.util.ArrayList; // importing this for the implementation of lists
import java.util.Map; // this import allows me to have an interface for mapping
import java.util.HashMap; // this helps in implementing maps
import java.util.LinkedList; // importing for SearchPath which is mentioned later in my code, in the book this was List L

public class DeadlockDetection{ // defining the class
	static Map<String, List<String>> outgoingArcs= new HashMap<>(); // this stores all the outgoing arcs for each node
	static Map<String, List<String>> incomingArcs= new HashMap<>(); // this similarly stores all the incoming arcs for each node
	static List<String> nodesStorage= new ArrayList<>(); // this stores all the nodes, including all the processes and the resources
	static List<String> arcsUsedAlready= new ArrayList<>(); // this line allows storing all the arcs that have been used already
	public static void main(String [] args) throws IOException{
		String inputFileName="input.txt"; // hardcoding the input file
		inputParse(inputFileName); // reading the file and building a graph
		detectingDeadlock(); // calling detectingDeadlock
	}
	static void inputParse(String inputFileName) throws IOException{ // this buiilds the input file and also builds the graph
		BufferedReader lineReader=new BufferedReader(new FileReader(inputFileName)); // opens the file
		String lineThatIsCurrent; //  stores the current line as the name suggests
		while ((lineThatIsCurrent = lineReader.readLine())!= null){ // loops through every line in the file essentially
			lineThatIsCurrent=lineThatIsCurrent.trim(); // this removes all the extra spaces
			if (lineThatIsCurrent.isEmpty())
				continue; // this helps in skipping all the blank lines
			String nodeThatIsTheSource; // this basically represents the start of an arc, can be the process or the resource 
			String nodeThatIsTheTarget; // this represents the end of an arc, can also be the process or the resource
			if (lineThatIsCurrent.contains(">")){ 
				String[] lineSplit=lineThatIsCurrent.split(">",2); // splits the line in two parts
				nodeThatIsTheSource= lineSplit[0].trim();
				nodeThatIsTheTarget= lineSplit[1].trim();
				arcAdding(nodeThatIsTheSource,nodeThatIsTheTarget);
			}
			else if (lineThatIsCurrent.contains("<")){
				String [] lineSplit= lineThatIsCurrent.split("<",2); // splitting line
				nodeThatIsTheSource= lineSplit[0].trim();
				nodeThatIsTheTarget=lineSplit[1].trim();
				arcAdding(nodeThatIsTheTarget,nodeThatIsTheSource);
			}

		}
		lineReader.close();
	}
	static void arcAdding(String nodeThatIsTheSource,String nodeThatIsTheTarget){
		addingNode(nodeThatIsTheSource);
		addingNode(nodeThatIsTheTarget);
		outgoingArcs.get(nodeThatIsTheSource).add(nodeThatIsTheTarget);
		incomingArcs.get(nodeThatIsTheTarget).add(nodeThatIsTheSource);
	}
	static void addingNode(String nameOfNode){
		if(!nodesStorage.contains(nameOfNode)){
			nodesStorage.add(nameOfNode);
			outgoingArcs.put(nameOfNode,new ArrayList<>());
			incomingArcs.put(nameOfNode,new ArrayList<>());
		}
	}
	static void detectingDeadlock(){ // this is basically the first step mentioned in the book
		for (String startingNode:nodesStorage){
			System.out.println("Starting node:"+startingNode); // following your outputs naming to make it easier for me (to write the code) and you to read and understand my code
			LinkedList<String> SearchPath= new LinkedList<>(); // list L basically
			arcsUsedAlready.clear(); // this resets all the arcs that were marked
			SearchPath.add(startingNode);
			String nodeThatIsCurrent=startingNode;
			boolean foundTheDeadlock=false;
			while(true){
				if(occuranceCount(SearchPath,nodeThatIsCurrent)>=2){
					System.out.println("\t**List already contains node "+nodeThatIsCurrent); // following your output
					System.out.println("\t***Deadlock Detected ! Node List L= "+SearchPath);
					foundTheDeadlock=true;
					break;
				}
				String nodeThatIsNext = getNextUnmarkedNode(nodeThatIsCurrent);
				if (nodeThatIsNext!=null){
					String arcThatIsMarked=nodeThatIsCurrent+"->"+nodeThatIsNext;
					arcsUsedAlready.add(arcThatIsMarked);
					System.out.println(nodeThatIsCurrent+" has an outgoing node-- following to :"+nodeThatIsNext);
					SearchPath.add(nodeThatIsNext);
					nodeThatIsCurrent=nodeThatIsNext;
				}
				else{
					if (nodeThatIsCurrent.equals(startingNode)){
						System.out.println("Path starting at"+startingNode+"contains no cycles. next node....\n");
						break;
					}
					else{
						String nodethatIsPrevious=SearchPath.get(SearchPath.size()-2);
						System.out.println(nodeThatIsCurrent+"has no outgoing nodes -- returning to:"+ nodethatIsPrevious);
						SearchPath.removeLast(); // learned about removing last node from here: https://stackoverflow.com/questions/60673939/how-can-i-remove-the-last-node-of-a-linked-list-in-java
						nodeThatIsCurrent=SearchPath.getLast();

					}
				}
			}
			if (foundTheDeadlock)
				return;
		}
		System.out.println("\n No deadlock detected");
	}
	static String getNextUnmarkedNode(String nodeThatIsCurrent){
		List<String> nodesThatAreOutgoing= outgoingArcs.get(nodeThatIsCurrent);
		for (String nodeThatIsNext:nodesThatAreOutgoing){
			String arcThatIsMarked=nodeThatIsCurrent+"->"+nodeThatIsNext;
			if(!arcsUsedAlready.contains(arcThatIsMarked)){
				return nodeThatIsNext;
			}
		}
		return null;
	}
	static int occuranceCount(List<String> pathList,String value){
		int count=0;
		for (String item:pathList){
			if (item.equals(value)){
				count=count+1;
			}
		}
		return count;
	}
}