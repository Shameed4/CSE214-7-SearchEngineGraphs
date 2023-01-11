import java.io.Serializable;

/**
 * Represents an active auction currently in the database
 * @author Sean Erfan
 *
 */
public class Auction implements Serializable {
	private int timeRemaining;
	private double currentBid;
	private String auctionID;
	private String sellerName;
	private String buyerName;
	private String itemInfo;

	/**
	 * Instantiates an Auction and initializes its variables
	 * 
	 * @param timeRemaining The time remaining in the auction
	 * @param currentBid    The value of the current bid
	 * @param auctionID     The ID of the auction
	 * @param sellerName    The seller's name
	 * @param buyerName     The buyer's name
	 * @param itemInfo      Information about the item
	 */
	public Auction(int timeRemaining, double currentBid, String auctionID,
	        String sellerName, String buyerName, String itemInfo) {
		this.timeRemaining = timeRemaining;
		this.currentBid = currentBid;
		this.auctionID = auctionID;
		this.sellerName = sellerName;
		this.buyerName = buyerName;
		this.itemInfo = itemInfo;
	}

	/**
	 * Decreases the time remaining
	 * 
	 * @param time The amount to decrease the time by
	 */
	public void decrementTimeRemaining(int time) {
		timeRemaining -= time;
		if (timeRemaining < 0)
			timeRemaining = 0;
	}

	/**
	 * Makes a new bid on this auction. If bidAmt is larger than currentBid,
	 * then the value of currentBid is replaced by bidAmt and buyerName is is
	 * replaced by bidderName.
	 * 
	 * @param bidderName The name of the bidder
	 * @param bidAmt     The amount to bid
	 * @throws ClosedAuctionException When auction is closed and no more bids
	 *                                can be placed
	 */
	public void newBid(String bidderName, double bidAmt)
	        throws ClosedAuctionException {
		if (timeRemaining == 0)
			throw new ClosedAuctionException(
			        "You can no longer bid on this item.");
		if (bidAmt > currentBid) {
			buyerName = bidderName;
			currentBid = bidAmt;
		}
	}

	/**
	 * @return A string of data members in tabular form.
	 */
	public String toString() {
		String bid = currentBid > 0
		        ? " | $" + String.format("%9s",
		                String.format("%,.2f", currentBid))
		        : String.format("%13s", "");
		return String.format("%11s", auctionID) + bid
		        + " | " + String.format("%-22s", sellerName) + "|  "
		        + String.format("%-23s", buyerName) + "|"
		        + String.format("%10s", timeRemaining + " hours") + " | "
		        + itemInfo;
	}

	/**
	 * @return the timeRemaining
	 */
	public int getTimeRemaining() {
		return timeRemaining;
	}

	/**
	 * @return the currentBid
	 */
	public double getCurrentBid() {
		return currentBid;
	}

	/**
	 * @return the auctionID
	 */
	public String getAuctionID() {
		return auctionID;
	}

	/**
	 * @return the sellerName
	 */
	public String getSellerName() {
		return sellerName;
	}

	/**
	 * @return the buyerName
	 */
	public String getBuyerName() {
		return buyerName;
	}

	/**
	 * @return the itemInfo
	 */
	public String getItemInfo() {
		return itemInfo;
	}

}
