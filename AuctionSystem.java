
/**
 * 
 * @author Sean Erfan ID:114763583 Recitation:02
 */

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Scanner;

/**
 * The AuctionSystem allows the user to interact with the database and provides
 * functionality to save and load an AuctionTable
 * 
 * @author Sean Erfan
 *
 */
public class AuctionSystem implements Serializable {
	public static Scanner s = new Scanner(System.in);
	public AuctionTable table;
	public String username;

	/**
	 * Instantiates an AuctionSystem without initializing any variables
	 */
	public AuctionSystem() {
	}

	/**
	 * Runs the program on behalf of the user
	 */
	public void runSystem() {
		username = promptNextString("Please select a username: ");
		showMenu();
		String input = promptNextString("Please select an option: ").trim()
		        .toUpperCase();
		while (!input.equals("Q")) {
			switch (input) {
			case "D":
				String url = promptNextString("Please enter a URL: ");
				AuctionTable temp;
				try {
					System.out.println("\nLoading...");
					temp = AuctionTable.buildFromURL(url);
					table = temp;
					System.out.println("Auction data loaded successfully!");
				}
				catch (IllegalArgumentException e) {
					System.out.println(e.getMessage());
				}
				break;
			case "A":
				System.out.println("\nCreating new Auction as " + username);
				String id = promptNextString("Please enter an Auction ID: ");
				int time = promptNextInt(
				        "Please enter an Auction time (hours): ");
				String info = promptNextString("Please enter some Item Info: ");
				table.putAuction(id,
				        new Auction(time, 0, id, username, "", info));
				System.out.println("\nAuction " + id + " inserted into table.");
				break;
			case "B":
				String id2 = promptNextString("Please enter an Auction id: ");
				Auction a = table.getAuction(id2);
				System.out.println();
				if (a == null)
					System.out.println("ERROR: Auction does not exist");
				else {
					boolean isOpen = a.getTimeRemaining() > 0;
					double bid = a.getCurrentBid();
					if (isOpen) {
						System.out.println("Auction " + id2 + " is OPEN");
						System.out
						        .println(
						                "\tCurrent Bid: "
						                        + (bid == 0 ? "None"
						                                : "$ " + String.format(
						                                        "%,.2f", bid))
						                        + "\n");
						double newBid = promptNextDouble(
						        "What would you like to bid?: ");
						try {
							a.newBid(username, newBid);
							if (a.getCurrentBid() != bid)
								System.out.println("Bid accepted.");
							else
								System.out.println("Bid was not accepted.");
						}
						catch (ClosedAuctionException e) {
							// will never happen because isOpen was checked
						}
					}
					else {
						System.out.println("Auction " + id2 + " is CLOSED");
						System.out
						        .println(
						                "\tCurrent Bid: "
						                        + (bid == 0 ? "None"
						                                : "$ " + String.format(
						                                        "%,.2f", bid))
						                        + "\n");
						System.out
						        .println("You can no longer bid on this item.");
					}
				}
				break;
			case "I":
				Auction a2 = table.getAuction(
				        promptNextString("Please enter an Auction ID: "));
				if (a2 == null)
					System.out.println("No auction found");
				else
					System.out.println("\nAuction " + a2.getAuctionID()
					        + "\n\tSeller: " + a2.getSellerName()
					        + "\n\tBuyer: " + a2.getBuyerName() + "\n\tTime: "
					        + a2.getTimeRemaining() + " hours\n\tInfo: "
					        + a2.getItemInfo());
				break;
			case "P":
				table.printTable();
				break;
			case "R":
				table.removeExpiredAuctions();
				break;
			case "T":
				int timeChange = promptNextInt("How many hours should pass: ");
				System.out.println("\nTime passing...");
				try {
					table.letTimePass(timeChange);
					System.out.println("Auction times updated.");
				}
				catch (IllegalArgumentException e) {
					System.out.println(e);
				}
				break;
			default:
				System.out.println("ERROR: Not an option!");
				break;
			}
			showMenu();
			input = promptNextString("Please select an option: ").trim()
			        .toUpperCase();
		}
		System.out.println("\nWriting Auction Table to file...");
		FileOutputStream file;
		try {
			file = new FileOutputStream("auctions.obj");
			ObjectOutputStream outStream = new ObjectOutputStream(file);
			outStream.writeObject(table);
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// the following line will save the object in the file
		System.out.println("Done!\nGoodbye.");
	}

	/**
	 * Prompts the user for a username and runs the program on the user's behalf
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		// http://tinyurl.com/nbf5g2h
		// http://tinyurl.com/p7vub89
		System.out.println("Starting...");
		AuctionSystem s = new AuctionSystem();
		try {
			FileInputStream file = new FileInputStream("auctions.obj");
			ObjectInputStream inStream = new ObjectInputStream(file);
			s.table = (AuctionTable) inStream.readObject();
			System.out.println("Loading previous Auction Table...");

		}
		catch (Exception e) {
			System.out.println("No previous auction table detected.");
			System.out.println("Creating new table...\n");

		}
		s.runSystem();
	}

	/**
	 * Prints the menu
	 */
	public static void showMenu() {
		System.out.println("\nMenu:\n    (D) - Import Data from URL\n"
		        + "    (A) - Create a New Auction\n"
		        + "    (B) - Bid on an Item\n"
		        + "    (I) - Get Info on Auction\n"
		        + "    (P) - Print All Auctions\n"
		        + "    (R) - Remove Expired Auctions\n"
		        + "    (T) - Let Time Pass\n" + "    (Q) - Quit\n");
	}

	/**
	 * Prints out a message and returns the String input
	 * 
	 * @param msg The message to print.
	 * @return The user input
	 */
	public static String promptNextString(String msg) {
		System.out.print(msg);
		return s.nextLine();
	}

	/**
	 * Prints out a message and returns a integer input. If the input is not a
	 * integer, it will try again.
	 * 
	 * @param msg The message to print
	 * @return The user input
	 */
	public static int promptNextInt(String msg) {
		int i;
		while (true) {
			try {
				System.out.print(msg);
				i = Integer.parseInt(s.nextLine());
			}
			catch (NumberFormatException e) {
				System.out.println("Must enter a number");
				continue;
			}
			break;
		}
		return i;
	}

	/**
	 * Prints out a message and returns a double input. If the input is not a
	 * double, it will try again.
	 * 
	 * @param msg The message to print
	 * @return The user input
	 */
	public static double promptNextDouble(String msg) {
		double d;
		while (true) {
			try {
				System.out.print(msg);
				d = Double.parseDouble(s.nextLine());
			}
			catch (NumberFormatException e) {
				System.out.println("Must enter a number");
				continue;
			}
			break;
		}
		return d;
	}
}
