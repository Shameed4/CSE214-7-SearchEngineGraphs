/**
 * The ClosedAuctionException class indicates that the Auction is closed when
 * trying to bid
 * 
 * @author Sean Erfan
 *
 */
public class ClosedAuctionException extends Exception {
	/**
	 * @param msg The message to be sent when exception is called
	 */
	public ClosedAuctionException(String msg) {
		super(msg);
	}
}
