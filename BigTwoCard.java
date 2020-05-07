/**
 * This class is a subclass of the Card class and it models a card used in a Big
 * Two card game.
 *
 * @author Panpan
 */
public class BigTwoCard extends Card {

	private static final long serialVersionUID = -3886066435694112173L;
	
	 /** Creates and returns an instance of the BigTwoCard class.
	 * 
	 * @param suit an int value between 0 and 3 representing the suit of a card
	 * @param rank an int value between 0 and 12 representing the rank of a card
	 * @see Card#suit
	 * @see Card#rank
	 */
	public BigTwoCard (int suit, int rank) {
		super(suit, rank); // call Card(int suit, int rank) from superclass
	}

	/**
	 * Compares this card with the specified card for order.
	 *
	 * @param card the card to be compared
	 * @return a negative integer, zero, or a positive integer as this card is
	 *         less than, equal to, or greater than the specified card
	 *
	 */ @Override
	public int compareTo(Card card) {

		/**
		 * According to the rules of Big Two, 0 (rank A) and 1 (rank 2) are 
		 * the 'biggest' card, so add 13 to the original index before comparing
		 * Since rank is a final variable, make a copy to do the modification
		 */
		int tRank = this.getRank();
		int cRank = card.getRank();
		if (tRank == 0 || tRank == 1) { tRank += 13; }
		if (cRank == 0 || cRank == 1) { cRank += 13; }

		/**
		 * Similar to the original compareTo() method,
		 * but use the new tRank and cRank
		 */
		if (tRank > cRank) { return 1; } 
		else if (tRank < cRank) { return -1; } 
		else if (this.suit > card.suit) { return 1; } 
		else if (this.suit < card.suit) { return -1; } 
		else { return 0; }
	}
}
