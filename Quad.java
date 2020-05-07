
/**
 * This class is a subclass of the Hand class and it models a hand of Quad cards in a Big Two card game.
 * 
 * @author Panpan
 */
public class Quad extends Hand {
	//private static final long serialVersionUID = -3886066435694112173L;
	
	/**
	 * This constructor creates an instance of the Quad class.
	 * @param player player of this hand
	 * @param cards cards in this hand
	 */
	public Quad (CardGamePlayer player, CardList cards) {
		super(player, cards);
	}
	
	/**
	 * Check the hand of Quad to see if it is valid
	 * @return boolean value that specifies validity of the hand
	 */ @Override
	public boolean isValid() {
		if (this.size() != 5) { return false; }
		
		this.sort(); // do the sorting first

		//find the quadruplet
		if (this.getCard(0).getRank() == this.getCard(1).getRank()) {
			//quadruplet is formed by first 4 cards
			for (int i = 0; i < 3; i++) {
				if (this.getCard(i).getRank() != this.getCard(i+1).getRank()) {
					return false;
				}
			}
		} else {
			//quadruplet is formed by last four cards
			for (int i = 1; i < 4; i++) {
				if (this.getCard(i).getRank() != this.getCard(i+1).getRank()) {
					return false;
				}
			}
		}
		
		//4 cards in quadruplet have the same rank
		return true;
	}
	 
	/**
	 * This method retrieves the top card of this hand. The card in 
	 * the quadruplet with the highest suit in a quad is referred to 
	 * as the top card of this quad.
	 * @return a Card object which is the top card of this hand
	*/ @Override
	public Card getTopCard(){
		this.sort(); // do the sorting first
		/* Like FullHouse, check where the quadruplet is.
		 * If it is formed by first 4 cards, top card is the 4th one.
		 * Else, top card is the last card
		 */
		return this.getCard(0).getRank() == this.getCard(1).getRank() 
				? this.getCard(3) : this.getCard(4);
		}
		
	/**
	 * This method checks if this Quad beats a specific hand
	 * @param hand a hand to be compared with this hand
	 * @return boolean value that specifies if this hand beats another one
	 */
	public boolean beats(Hand hand){
		String type = hand.getType();
		// A quad always beats any straights, flushes and full houses.
		if (type == "Straight" || type == "Flush" || type == "FullHouse") { return true; }
		
		/* A quad having a top card with a higher rank beats a quad having 
		a top card with a lower rank. */
		if (type == "Quad") {
			if (this.getTopCard().getRank() > hand.getTopCard().getRank()) { return true; }
		}
		return false;
	}
	
	/**
	 * This method returns the type of the hand in string form.
	 * @return string that specifies the type of hand
	 */ @Override
	public String getType() {
		return "Quad";
	}
	
}
