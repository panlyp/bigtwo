
/**
 * This class is a subclass of the CardList class and it models a hand of cards.
 *
 * @author Panpan
 */
public abstract class Hand extends CardList {

	private CardGamePlayer player;

	/**
	 * This constructor builds a hand with the specified player and list of cards.
	 * @param player the player who will play this hand
	 * @param cards the list of cards
	 */
	public Hand(CardGamePlayer player, CardList cards) {
		//removeAllCards(); // need this?
		this.player = player;
		for (int i = 0; i < cards.size(); i++) {
			//System.out.println("Adding " +  cards.getCard(i) + "...");
			this.addCard(cards.getCard(i));
		}
	}

	/**
	 * This method retrieves the player of this hand.
	 * @return a Player who plays this hand.
	 */
	public CardGamePlayer getPlayer() {
		return this.player;
	}

	/**
	 * This method retrieves the top card of this hand.
	 * @return a Card object which is the top card of this hand
	*/
	public Card getTopCard(){
		// The only card is the top card
		if (this.getType().equals("Single")) {
			return getCard(0);
		}
		
		Card theTopCard = this.getCard(0);
		String[] topSuit = {"Pair", "Triple", "FullHouse", "Quad"};
		String[] topRank = {"Straight", "Flush", "StraightFlush"};
		
		for (String str : topSuit){
			if (this.getType().equals(str)) {
				for (int i = 1; i < this.size() - 1; i++) {
					if (this.getCard(i).suit > this.getCard(i-1).suit) {
						theTopCard = this.getCard(i);
					}
				}
			}
		}
		
		for (String str : topRank){
			if (this.getType().equals(str)) {
				for (int i = 1; i < this.size() - 1; i++) {
					if (this.getCard(i).rank > this.getCard(i-1).rank) {
						theTopCard = this.getCard(i);
					}
				}
			}
		}
		return theTopCard; // the card with higher suit or rank
	}
	 
	/**
	 * This method checks if this hand beats a specific hand using compareTo().
	 * This is for general comparison, and may be overridden if the type of
	 * hand has other special rules.
	 * @param hand a hand to be compared with this hand
	 * @return boolean value that specifies if this hand beats another one
	 */
	public boolean beats(Hand hand) {
		// According to the rules we are supposed to compare cards with same size
		if(this.size() != hand.size()) { return false;
		}
		//Then, check only the top cards
		return this.getTopCard().compareTo(hand.getTopCard()) == 1;
	}

	/**
	 * This abstract method returns the validity of the hand and will be overriden.
	 */
	public abstract boolean isValid();

	/**
	 * This abstract method returns the type of the hand in string form and will be overriden. 
	 */
	public abstract String getType();

}
