import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import javax.swing.JOptionPane;

/**
 * The BigTwoClient class implements the CardGame interface and NetworkGame interface.
 * 
 * @author Panpan
 *
 */
public class BigTwoClient implements CardGame, NetworkGame {

	private Deck deck;
	private BigTwoTable table;
	private ArrayList<CardGamePlayer> playerList;
	private ArrayList<Hand> handsOnTable;
	private int currentIdx;
	
	private static boolean inFirstRound = true;
	private static int startingPlayer; // the owner of Three of Diamonds
	private Hand lastHand = null;
	private static boolean isLegal = false;
	private static boolean passed = false;
	private CardGamePlayer winner = null; // saves the player who won the game.
	
	private int numOfPlayers;
	private int playerID;
	private String playerName;

	private String serverIP;
	private int serverPort;
	private Socket sock;
	private ObjectOutputStream objectOutputStream;

	private boolean serverIsFull;
	
	/**
	 * A constructor for creating a Big Two client.
	 * 
	 */
	public BigTwoClient() {
		numOfPlayers = 4;
		playerList = new ArrayList<CardGamePlayer>();
		handsOnTable = new ArrayList<Hand>();
		
		// let player input the player name
		String name = JOptionPane.showInputDialog("Please input your name:");
		
		if(name != null && !name.isEmpty()) {
			setPlayerName(name);
			for(int i = 0; i < numOfPlayers; i++) {
				CardGamePlayer player = new CardGamePlayer();
				player.setName(""); // empty string for players not yet joined
				playerList.add(player);
			}
			// relates the table and create connection to server
			table = new BigTwoTable(this);
			makeConnection();
		}
	}

	/* (non-Javadoc)
	 * @see NetworkGame#getPlayerID()
	 */ @Override
	public int getPlayerID() { return playerID; }

	/* (non-Javadoc)
	 * @see NetworkGame#setPlayerID(int)
	 */ @Override
	public void setPlayerID(int playerID) { this.playerID = playerID; }

	/* (non-Javadoc)
	 * @see NetworkGame#getPlayerName()
	 */ @Override
	public String getPlayerName() { return playerName; }

	/* (non-Javadoc)
	 * @see NetworkGame#setPlayerName(java.lang.String)
	 */ @Override
	public void setPlayerName(String playerName) { this.playerName = playerName; }

	/* (non-Javadoc)
	 * @see NetworkGame#getServerIP()
	 */ @Override
	public String getServerIP() { return serverIP; }
	 
	/* (non-Javadoc)
	 * @see NetworkGame#getServerPort()
	 */ @Override
	public int getServerPort() { return this.serverPort; }

	/* (non-Javadoc)
	 * @see NetworkGame#setServerIP(java.lang.String)
	 */
	@Override
	public void setServerIP (String serverIP) { this.serverIP = serverIP; }

	/* (non-Javadoc)
	 * @see NetworkGame#setServerPort(int)
	 */ @Override
	public void setServerPort(int serverPort) { this.serverPort = serverPort; }

	/**
	 * This method returns the deck of cards being used.
	 * @return deck of cards being used
	 */
	public Deck getDeck() { return this.deck; }

	/**
	 * This method returns the list of players.
	 * @return a CardGamePlayer list that contains the list of players
	 */
	public ArrayList<CardGamePlayer> getPlayerList() { return this.playerList; }
	
	/**
	 * This method returns the list of hands played on the table.
	 * @return list of hands played on the table
	 */
	public ArrayList<Hand> getHandsOnTable() { return this.handsOnTable; }
	
	/**
	 * This method returns the index of the current player.
	 * @return the id of the current player
	 */
	public int getCurrentIdx() { return this.currentIdx; }
	
	/**
	 * This method returns the number of players in the player list.
	 * @return number of players in player list
	 */
	public int getNumOfPlayers() { return playerList.size(); }
	
	/**
	 * This method returns the winner of this game.
	 * @return the winner of this game
	 */
	public CardGamePlayer getWinner() { return winner; }
	
	/**
	 * This method creates an instance of a BigTwoClient.
	 */
	public static void main(String[] args) { new BigTwoClient(); }

	/* (non-Javadoc)
	 * @see NetworkGame#makeConnection()
	 */ @Override
	public void makeConnection() {
		String serverInput = "127.0.0.1";
		String portInput = "2396";
		//serverInput = JOptionPane.showInputDialog("Please enter the server's IP address:");
		//portInput = JOptionPane.showInputDialog("Please enter the port:");
		
		if(serverInput != null && !serverInput.isEmpty()) {
			setServerIP(serverInput);
			if(portInput != null && !portInput.isEmpty()) {
				setServerPort(Integer.parseInt(portInput));
				
				try {
					sock = new Socket(serverIP, serverPort);
					// create an OOS to send messages to server
					objectOutputStream = new ObjectOutputStream(sock.getOutputStream());
					// create a thread for incoming messages from server
					Runnable job = new ServerHandler();
					Thread msgThread = new Thread(job);
					msgThread.start();
					// send JOIN message to server with this player name
					sendMessage(new CardGameMessage(CardGameMessage.JOIN, -1, playerName));
				} // print error message if connection cannot be made
				  catch (UnknownHostException e) {
					table.printMsg("Cannot connect to the server.");
					lostConnection();
				} catch (IOException e) {
					table.printMsg("Cannot connect to the server.");
					lostConnection();
				}
			}
		}
	}
	
	/**
	 * This method changes the behavior of the table
	 * when the player lost the connection to the BigTwoServer.
	 * 
	 */
	private void lostConnection() {	
		table.disable();
		table.disableChat(); // cannot use the chat function
		table.enableConnect(); // make the connect menu item clickable again
	}

	/* (non-Javadoc)
	 * @see NetworkGame#parseMessage(GameMessage)
	 */ @Override
	public void parseMessage(GameMessage message) {
		switch (message.getType()) {
		
			case CardGameMessage.JOIN:
				// add new player (update name) and repaint GUI
				playerList.get(message.getPlayerID()).setName((String) message.getData());
				if(message.getPlayerID() == playerID) {
					table.printMsg("Connected to /" + serverIP + ":" + serverPort);
					// send "READY" to the server with playerID and data being -1 and null
					sendMessage(new CardGameMessage(CardGameMessage.READY, -1, null));
				}
				table.repaint();
				break;
			
			case CardGameMessage.READY:
				// send to the server the playerID and data being -1 and null
				table.printMsg(getPlayerList().get(message.getPlayerID()).getName() + " is ready.");
				table.repaint();
				break;
				
			case CardGameMessage.PLAYER_LIST:
				// set playerID of local player
				setPlayerID(message.getPlayerID());
				// update names in player list
				String[] playerNames = (String[]) message.getData();
				for(int i = 0; i < numOfPlayers; i++) {
					if(playerNames[i] != null) {
						playerList.get(i).setName(playerNames[i]);
					}
				}
				// disable "Connect" but enable the GUI and chat area
				table.repaint();
				table.disable();
				table.enableChat();
				table.disableConnect();
				break;
				
			case CardGameMessage.FULL:
				serverIsFull = true;
				// display message in the text area showing that server is full
				table.printMsg("Server is full.");
				lostConnection();
				break;
				
			case CardGameMessage.QUIT:
				// stop if the game is in progress
				if(isPlaying()) {
					table.setActivePlayer(-1);
					table.disable();
					clearAll();
				}			
				// show message about who quit the game
				table.printMsg(getPlayerList().get(message.getPlayerID()).getName() + " quits.");
				// remove the player
				playerList.get(message.getPlayerID()).setName("");
				// send READY to server
				sendMessage(new CardGameMessage(CardGameMessage.READY, -1, null));
				// repaint GUI
				table.repaint();
				break;

			case CardGameMessage.START:
				// start a new game (shuffled)
				start((BigTwoDeck) message.getData());
				table.repaint();
				break;
				
			case CardGameMessage.MOVE:
				// check moves made, with an int[] of selection
				checkMove(message.getPlayerID(), (int[]) message.getData());
				table.repaint();
				break;
				
			case CardGameMessage.MSG:
				// display message in chat area
				table.printChatMsg((String) message.getData());
				break;
				
			default:
				table.printMsg("INVALID TYPE: " + message.getType());
				break;
		}
	}

	/* (non-Javadoc)
	 * @see NetworkGame#sendMessage(GameMessage)
	 */
	@Override
	public void sendMessage(GameMessage message) {
		try {
			objectOutputStream.writeObject(message);
		} catch(Exception ex) {
			table.printMsg("Message is not sent due to an error.");
			ex.printStackTrace();
		}
	}
	
	/* (non-Javadoc)
	 * @see CardGame#start()
	 */
	@Override
	public void start(Deck deck) {
		// resets the table
		clearAll();	
		table.reset();
		
		this.deck = deck;

		// give cards to the 4 players after shuffling
		for (int i = 0; i < getDeck().size(); i++) {
			if (getCurrentIdx() >= getPlayerList().size()) { this.currentIdx = 0; }
			getPlayerList().get(getCurrentIdx()).addCard(getDeck().getCard(i));
			// find out who has the Three of Diamonds
			if (getDeck().getCard(i).getRank() == 2 && getDeck().getCard(i).getSuit() == 0) {
				// save for later use, he will play the first round
				startingPlayer = getCurrentIdx();	
			}
			this.currentIdx++;
		}
		
		// sort the cards for players
		for (CardGamePlayer player : getPlayerList()) { 
			player.sortCardsInHand(); 
		}
			
		// we should start the game by the startingPlayer
		this.currentIdx = startingPlayer;
		table.enable();
		table.setActivePlayer(currentIdx);
		
		table.repaint();
		table.printMsg(getPlayerList().get(currentIdx).getName() + " plays the first round!");
		
	}
	
	/**
	 * This methods will reset the table (player's cards and the table for hands).
	 * 
	 */
	private void clearAll() {
		handsOnTable = new ArrayList<Hand>();
		for(int i = 0; i < numOfPlayers; i++) {
			playerList.get(i).removeAllCards();
		}
	}

	/**
	 * This method will send the MOVE message to the server
	 * with the specified playerID using the set of cards.
	 * @param playerID the player who is going to make a move
	 * @param cardIdx the list of cards involved in the move
	 */
	public void makeMove(int playerID, int[] cardIdx) {
		sendMessage(new CardGameMessage(CardGameMessage.MOVE, playerID, cardIdx));
	}

	/**
	 * This method checks the move made by a player.
	 * @param playerID the player who made a move
	 * @param cardIdx the list of cards involved in the move
	 */
	public void checkMove(int playerID, int[] cardIdx) {
		// reset pass and legal status
		passed = false;
		isLegal = false;

		// get user input and turn it to a CardList
		Hand hand = null;
		CardGamePlayer player = playerList.get(currentIdx);
		CardList cards = player.play(cardIdx);
		
		// if no cards are played (i.e. passed)
		if(cards == null) {
			// the first player should not pass
			// the player who played the last hand on table cannot pass
			if(handsOnTable.isEmpty() || player == lastHand.getPlayer()) {
				table.printMsg("You must play a hand in this round!!!");
			} else {
				isLegal = true;
	            passed = true;
	            table.printMsg("{Pass}");
	            nextPlayer();
			}
		} else {	
			// check validity of the hand
			hand = composeHand(player, cards);

	        if (hand != null) { // as long as the hand is valid:
	            table.printMsg("{" + hand.getType() + "} " + hand.toString());
		        
	            if (inFirstRound) {
		        	isLegal = true; // 1) the 1st player can play anything
		        	inFirstRound = false; // end of first round
		        } else if (player == lastHand.getPlayer()) {
	            	isLegal = true; // 2) the player from last round can play anything
	            }
	            if (lastHand != null && hand.beats(lastHand)) {
	            	isLegal = true; // 3) and it can beat the last hand
	            } // the move is legal
	        } 
	        
	        if (!isLegal) { // prompts warning and reset selection for illegal moves
	        	table.printMsg(cards.toString() + " is not a legal move!!!");
				table.resetSelected();
	        } else {
	        	if (!passed) { // remove cards by playHand() if not passed
		            playHand(player, hand, hand);
		            if(endOfGame()) { // any winner?
		            	end(); // prints end game message
		            	return;
		            } 
				}
	        }
		}

		// continue game and show the next player
		table.setActivePlayer(currentIdx);
		player = playerList.get(currentIdx);
		if (isLegal) { table.printMsg("\nIt's " + player.getName() + "'s turn..."); }
		
		// refresh GUI
		table.repaint();
		return;
	}
	
	/**
	 * This method lets the current player to play a hand, adds the hand to handsOnTable 
	 * and then remove cards from the player. The method will also pass the round to the
	 * next player.
	 * @param player the player of this round
	 * @param cards cards used in this hand
	 * @param hand the hand played by this player
	 */
	private void playHand(CardGamePlayer player, CardList cards, Hand hand) {
		// save this as the last hand played
		this.lastHand = hand;
		// since the hand is played, remove cards from player
		player.removeCards(cards); 
		// add hand to the handsOnTable		
		getHandsOnTable().add(hand);	
		// next player's turn
		this.nextPlayer();
	}
	
	/**
	 * This method makes the Big Two game progress by letting the next
	 * player to play the round.
	 */
	private void nextPlayer() {
		if (getCurrentIdx() < 0 || getCurrentIdx() == 3) {
			this.currentIdx = -1; 
		}
		this.currentIdx++;
	}

	/**
	 * This method returns a valid hand from the specified list of cards of the player.
	 * Returns null when no valid hand can be composed from the specified list of cards.
	 * @param player the current player
	 * @param cards the list of cards of the player
	 * @return a valid hand or null
	 */
	public static Hand composeHand(CardGamePlayer player, CardList cards) {
		Hand hand = null;
		
		// if it is the first round, the player must play the Three of Diamonds
		if (inFirstRound) {
			Card diamond3 = new Card(0,2);
			if (!cards.contains(diamond3)) { return null; }
			// go to the following to check validity
		}

		if (cards.size() == 1) { 
			hand = new Single(player, cards); 
		}  else if (cards.size() == 2) { 
			hand = new Pair(player, cards); 
		} else if (cards.size() == 3) { 
			hand = new Triple(player, cards); 
		} else if (cards.size() == 5) { 
			hand = new StraightFlush(player, cards); 
			if (!hand.isValid()) {
				hand = new Quad(player, cards);	
				if (!hand.isValid()) {
					hand = new FullHouse(player, cards);
					if (!hand.isValid()) {
						hand = new Flush(player, cards);
						if (!hand.isValid()) {
							new Straight(player, cards);
						}
					}
				}
			}
		}
		return (hand == null || !hand.isValid()) ? null : hand;
	}

	/* (non-Javadoc)
	 * @see CardGame#isPlaying()
	 */
	public boolean isPlaying() {
		// game is not in progress if somebody wins
		if(endOfGame()) { return false; } 
		// game is not in progress if someone lost connection
		for(int i = 0; i < numOfPlayers; i++) {
			if(playerList.get(i).getName() == "") {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * This method checks if any of the player in the game has 0 card left and
	 * records the winner of this game.
	 * @return whether the game will end
	 */
	public boolean endOfGame() {
		for (CardGamePlayer player : getPlayerList()) {
			// the player who has no cards left will be the winner
			if (player.getNumOfCards() == 0) { 
				winner = player;
				return true; 
			}
		}
		return false;
	}
	
	/**
	 * This method will create a dialog box at the end of the game,
	 * showing the game results.
	 */
	private void end() {
		clearAll(); // remove all cards from hands
		// disable GUI
		table.disable();
		table.repaint();

		String endMsg = "\nGame ends";
		for(CardGamePlayer player : this.playerList) {
			// if the player is the winner, do not print number of cards
			if (player == winner) {
				endMsg += "\n" + winner.getName() + " wins the game.";
			} else { // prints number of cards in hand
				if (player.getNumOfCards() == 1) {
					endMsg += "\n" + player.getName() + " has 1 card in hand.";
				} else {
					endMsg += "\n" + player.getName() + " has " + player.getNumOfCards() + " cards in hand.";
				}
			}
		}
		
		JOptionPane.showMessageDialog(null, endMsg);
		this.sendMessage(new CardGameMessage(CardGameMessage.READY, -1, null));
	}

	/**
	 * This inner class implements the Runnable interface.
	 *
	 */
	class ServerHandler implements Runnable {
		private ObjectInputStream inputStream;
		
		public ServerHandler() {
			try {
				inputStream = new ObjectInputStream(sock.getInputStream());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		public void run() {
			CardGameMessage message;
			try {
				// wait for messages
				while((message = (CardGameMessage) inputStream.readObject()) != null) {
					parseMessage(message);
				}
			} catch(Exception ex) {
				ex.printStackTrace();
				// check why the connection is lost
				// because server is full?
				if(serverIsFull) { serverIsFull = false; }
				else { // other reasons:
					if(isPlaying()) { // if game is still in progress
						table.setActivePlayer(-1);
						table.disable();
					}
					// remove all players (in local player's table)
					for(int i = 0; i < numOfPlayers; i++) {
						playerList.get(i).setName("");
					}
					table.clearMsgArea();
					table.clearChatArea();
					
					JOptionPane.showMessageDialog(null, "Connection is lost.");
					table.repaint();
					lostConnection();				
				}
			}
		}
	}
}
