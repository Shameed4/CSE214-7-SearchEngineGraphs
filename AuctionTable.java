
import big.data.*;
import java.util.Iterator;

import java.io.Serializable;
import java.util.HashMap;

/**
 * The AuctionTable stores a HashMap of Auction objects and allows certain
 * operations
 * 
 * @author Sean Erfan
 *
 */
public class AuctionTable implements Serializable {
	private HashMap<String, Auction> auctions;

	/**
	 * Instantiates an AuctionTable and initializes a hashmap
	 */
	public AuctionTable() {
		auctions = new HashMap<String, Auction>();
	}

	/**
	 * Instantiates an AuctionTable and initializes a hashmap
	 * 
	 * @param map The HashMap of auctions
	 */
	public AuctionTable(HashMap<String, Auction> map) {
		auctions = map;
	}

	/**
	 * Manually posts an auction, and adds it into the table.
	 * 
	 * @param auctionID The unique key for this object
	 * @param auction   The auction to insert into the table with the
	 *                  corresponding auctionID
	 * @throws IllegalArgumentException If the given auctionID is already stored
	 *                                  in the table
	 */
	public void putAuction(String auctionID, Auction auction)
	        throws IllegalArgumentException {
		if (auctions.get(auctionID) != null)
			throw new IllegalArgumentException(
			        "ERROR: There is already an Auction with this id.");
		auctions.put(auctionID, auction);
	}

	/**
	 * Get the information of an Auction that contains the given ID as key
	 * 
	 * @param auctionID The unique key for this object
	 * @return An Auction object with the given key, null otherwise
	 */
	public Auction getAuction(String auctionID) {
		return auctions.get(auctionID);
	}

	/**
	 * Simulates the passing of time. Decrease the timeRemaining of all Auction
	 * objects by the amount specified. The value cannot go below 0.
	 * 
	 * @param numHours The number of hours for time to pass
	 * @throws IllegalArgumentException Thrown when time is non-positive
	 */
	public void letTimePass(int numHours) throws IllegalArgumentException {
		if (numHours <= 0)
			throw new IllegalArgumentException("Time must be positive");
		for (String key : auctions.keySet()) {
			auctions.get(key).decrementTimeRemaining(numHours);
		}
	}

	/**
	 * Iterates over all Auction objects in the table and removes them if they
	 * are expired (timeRemaining == 0)
	 */
	public void removeExpiredAuctions() {
		Iterator<HashMap.Entry<String, Auction>> it = auctions.entrySet()
		        .iterator();
		System.out.println("Removing expired auctions...");
		while (it.hasNext()) {
			HashMap.Entry<String, Auction> entry = it.next();
			if (entry.getValue().getTimeRemaining() == 0) {
				it.remove();
			}
		}
		System.out.println("All expired auctions removed.");
	}

	/**
	 * Prints the AuctionTable in tabular form.
	 */
	public void printTable() {
		System.out.println(" Auction ID |      Bid   |        Seller         "
		        + "|          Buyer          |    Time   |  Item Info");
		System.out.println("==================================================="
		        + "============================================================"
		        + "====================");
		for (String key : auctions.keySet()) {
			System.out.println(auctions.get(key));
		}
	}

	/**
	 * Uses the BigData library to construct an AuctionTable from a remote data
	 * source.
	 * 
	 * @param url String representing the URL for the remove data source.
	 * @throws IllegalArgumentException Thrown if te URL does not represent a
	 *                                  valid datasource (can't connect or
	 *                                  invalid syntax)
	 */
	public static AuctionTable buildFromURL(String url)
	        throws IllegalArgumentException {
		DataSource ds;
		String[] times, bids, ids, sellers, buyerNames, memories, hardDrives,
		        cpus;
		try {
			ds = DataSource.connect(url).load();
			times = ds.fetchStringArray("listing/auction_info/time_left");
			bids = ds.fetchStringArray("listing/auction_info/current_bid");
			ids = ds.fetchStringArray("listing/auction_info/id_num");
			sellers = ds.fetchStringArray("listing/seller_info/seller_name");
			buyerNames = ds.fetchStringArray(
			        "listing/auction_info/high_bidder/bidder_name");
			memories = ds.fetchStringArray("listing/item_info/memory");
			hardDrives = ds.fetchStringArray("listing/item_info/hard_drive");
			cpus = ds.fetchStringArray("listing/item_info/cpu");
		}
		catch (DataSourceException e) {
			throw new IllegalArgumentException("No data found");
		}

		HashMap<String, Auction> map = new HashMap<String, Auction>();
		String info;
		for (int i = 0; i < times.length; i++) {
			info = cpus[i];
			if (!info.equals("") && !memories[i].equals(""))
				info += " - ";
			info += memories[i];
			if (!info.equals("") && !hardDrives[i].equals(""))
				info += " - ";
			info += hardDrives[i];
			if (info.length() == 0)
				info = "N/A";
			Auction a = new Auction(timeToHours(times[i]), bidToString(bids[i]),
			        ids[i], sellers[i].replace("\n", "").replace("\r", "")
			                .replace(" ", ""),
			        buyerNames[i], info.replace("\n", ""));
			map.put(ids[i], a);
		}
		return new AuctionTable(map);
	}

	/**
	 * Converts a String time with days and hours to just hours
	 * 
	 * @param time The time to convert
	 * @return The time in hours
	 */
	public static int timeToHours(String time) {
		int hours = 0;
		int dayIndex = time.indexOf("day");
		if (dayIndex != -1) {
			hours += 24
			        * Integer.parseInt(time.substring(0, time.indexOf(" ")));
			time = time.substring(dayIndex);
			time = time.substring(time.indexOf(" ") + 1);
		}
		int hourIndex = time.indexOf("h");
		if (hourIndex != -1) {
			hours += Integer.parseInt(time.substring(0, time.indexOf(" ")));
		}
		return hours;
	}

	/**
	 * Removes the dollar sign and commas from a number represented as a String.
	 * 
	 * @param num The number to remove
	 * @return The price as a double
	 */
	public static double bidToString(String num) {
		num = num.substring(1);
		for (int i = 0; i < num.length(); i++) {
			if (num.charAt(i) == ',') {
				num = num.substring(0, i) + num.substring(i + 1);
				i--;
			}
		}
		return Double.parseDouble(num);
	}
}