import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;

/**
 * The BigTwoTable class implements the CardGameTable interface.
 * 
 * @author Panpan
 */
public class BigTwoTable implements CardGameTable {
	
	private BigTwoClient game;
	private boolean[] selected;
	private int activePlayer;
	
	// Images and Size
	private Image[][] cardImages = new Image[4][13];
	private Image cardBackImage = null;
	private Image[] avatars = new Image[4];
	private Dimension cardDimension = new Dimension(82, 114);
	private Dimension playerPanelDimension = new Dimension(120, 125);
	private Dimension rowDimension = new Dimension(350, 140);
	private int avatarsWidth = 100;
	private int avatarsHeight = 135;
	
	// Frame, Panels and Buttons
	private JFrame frame = null;
	private JPanel bigTwoPanel = null;
	private JPanel[] tableRowPanel = null;
	private PlayerPanel[] playerPanel = null;
	private CardListPanel[] cardListPanel = null;
	private BigTwoPanel[][] cardPanel = null;
	private BigTwoPanel[][] cardBackPanel = null;
	private JPanel buttonsPanel;
	
	private JButton playButton = null;
	private JButton passButton = null;
	private JButton resetButton = null;
	private JTextArea msgArea = null;
	private JScrollPane msgScrollPane = null;

	private JPanel textPanel;
	private JPanel chatPanel;
	private JPanel chatInputPanel;
	private JMenuItem connectMenuItem;
	private JTextArea chatArea;
	private JLabel chatLabel;
	private JTextField chatInputField;

	/**
	 * This constructor creates a BigTwoTable.
	 * @param game A BigTwo game to be associated with this table
	 * 
	 */
	public BigTwoTable(BigTwoClient game) {
		this.game = game;
		
		frame = new JFrame("BigTwo");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// set menuBar
		JMenuBar menuBar = new JMenuBar();
		
		JMenu gameMenu = new JMenu("Game");
		JMenu messageMenu = new JMenu("Message");
		connectMenuItem = new JMenuItem("Connect");
		connectMenuItem.addActionListener(new ConnectMenuItemListener());
		gameMenu.add(connectMenuItem);
		
		JMenuItem quitMenuItem = new JMenuItem("Quit");
		quitMenuItem.addActionListener(new QuitMenuItemListener());
		gameMenu.add(quitMenuItem);
		
		JMenuItem clearConsoleMenuItem = new JMenuItem("Clear Console Message");
		clearConsoleMenuItem.addActionListener(new ClearConsoleMenuItemListener());
		messageMenu.add(clearConsoleMenuItem);
		
		JMenuItem clearChatMenuItem = new JMenuItem("Clear Chatroom");
		clearChatMenuItem.addActionListener(new ClearChatMenuItemListener());
		messageMenu.add(clearChatMenuItem);
		
		menuBar.add(gameMenu);
		menuBar.add(messageMenu);
		
		// set msgArea and chatArea
		msgArea = new JTextArea(10,25);
		msgArea.setLineWrap(true); // auto wrap
		msgArea.setEditable(false); // user cannot edit the text
		chatArea = new JTextArea(10,25);
		chatArea.setLineWrap(true); // auto wrap
		chatArea.setEditable(false); // user cannot edit the text
		chatArea.setForeground(Color.BLUE); // set font to blue
		
		// add ScrollPanes to msgArea and chatArea
		msgScrollPane = new JScrollPane(msgArea);
		msgScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		JScrollPane chatScrollPane = new JScrollPane(chatArea);
		chatScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		
		// set menu items for message
		textPanel = new JPanel();
		textPanel.setLayout(new BorderLayout());
		chatPanel = new JPanel();
		chatPanel.setLayout(new BoxLayout(chatPanel, BoxLayout.Y_AXIS));

		//add textArea and chatArea into chatPanel
		chatPanel.add(msgScrollPane);
		chatPanel.add(chatScrollPane);
		
		//set the chatInputPanel
		chatInputPanel = new JPanel();
		//set the chatLabel
		chatLabel = new JLabel("Message:");
		//set the chatInputField
		chatInputField = new JTextField(40);
		chatInputField.addActionListener(new ChatInputListener());
		chatInputField.setEditable(false);
		//add chatLabel and chatInputField into chatInputPanel
		chatInputPanel.add(chatLabel);
		chatInputPanel.add(chatInputField);
		
		//add the chatInputPanel and into textPanel
		textPanel.add(BorderLayout.SOUTH, chatInputPanel);
		textPanel.add(BorderLayout.CENTER, chatPanel);
		
		// load images for cards, card back
		// order: Diamond, Club, Heart, Spade
		String cardFacePath = "img/cardFaceA/";
		cardBackImage = new ImageIcon(cardFacePath + "cardBack.png").getImage();
		for (int i = 0; i < 13; i++) {
			cardImages[0][i] = new ImageIcon(cardFacePath + "d" + i + ".png").getImage();
			cardImages[1][i] = new ImageIcon(cardFacePath + "c" + i + ".png").getImage();
			cardImages[2][i] = new ImageIcon(cardFacePath + "h" + i + ".png").getImage();
			cardImages[3][i] = new ImageIcon(cardFacePath + "s" + i + ".png").getImage();
		}
		
		// load images for the avatar
		for (int i = 0; i < 4; i++) {
			avatars[i] = new ImageIcon("img/avatar/P" + i + ".png").getImage();
		}
		
		// create a bigTwoPanel
		bigTwoPanel = new JPanel();
		bigTwoPanel.setLayout(new BorderLayout());
		
		// create a panel for a row in the table
		// inside a row there will be 2 panels:
		// 	1. the playerPanel that shows the player's information
		// 	2. the cardListPanel that shows the current cards owned by a player
		tableRowPanel = new JPanel[5]; // 4 players + 1 row for showing last hand
		for(int i = 0; i < tableRowPanel.length; i++) {
			tableRowPanel[i] = new JPanel();
			// set border color and background color to dark blue
			tableRowPanel[i].setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(107, 103, 132)));
			tableRowPanel[i].setBackground(new Color(44, 51, 96));
			// use FlowLayout to place elements on left
			tableRowPanel[i].setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));
			//tableRowPanel[i].setLayout(new FlowLayout(FlowLayout.LEFT));
		}
		
		// set the playerPanels
		playerPanel = new PlayerPanel[game.getNumOfPlayers()];
		for(int i = 0; i < playerPanel.length; i++) {
			playerPanel[i] = new PlayerPanel(i);
			playerPanel[i].setPreferredSize(playerPanelDimension);
		}
		
		// set the cardListPanels
		// for the cardListPanels, it either shows the card face or card back
		// there is an extra panel for the hand played by a player in the last round
		cardListPanel = new CardListPanel[game.getNumOfPlayers() + 1];
		for(int i = 0; i < cardListPanel.length; i++) {
			cardListPanel[i] = new CardListPanel(i);
			cardListPanel[i].setPreferredSize(rowDimension);
			cardListPanel[i].setDisplayCards(new CardList()); // should be empty at first
		}
		
		// set the BigTwoPanels for card
		cardPanel = new BigTwoPanel[13][4];
		for(int i = 0; i < 13; i++) {
			for(int j = 0; j < 4; j++) {
				// for active players, cards are clickable
				cardPanel[i][j] = new BigTwoPanel(i, j, false);
				cardPanel[i][j].addMouseListener(cardPanel[i][j]);
				cardPanel[i][j].setPreferredSize(cardDimension);
			}
		}
		
		// set the BigTwoPanels for card back
		cardBackPanel = new BigTwoPanel[13][4];
		for(int i = 0; i < 13; i++) {
			for(int j = 0; j < 4; j++) {
				// card backs are not clickable, so set only the dimensions
				cardBackPanel[i][j] = new BigTwoPanel(i, j, true);
				cardBackPanel[i][j].setPreferredSize(cardDimension);
			}
		}

		// add the playerPanels and cardListPanels to tableRowPanel
		for(int i = 0; i < game.getNumOfPlayers(); i++) {
			tableRowPanel[i].add(playerPanel[i]);
			tableRowPanel[i].add(cardListPanel[i]);
		}
		// lastly, the row for last hand
		tableRowPanel[tableRowPanel.length - 1].add(cardListPanel[cardListPanel.length - 1]);
		
		// add Play, Pass and Reset buttons to game
		playButton = new JButton("Play");
		playButton.addActionListener(new PlayButtonListener());
		passButton = new JButton("Pass");
		passButton.addActionListener(new PassButtonListener());
		resetButton = new JButton("Reset");
		resetButton.addActionListener(new ResetButtonListener());
		
		//set the buttonsPanel for "play" and "pass" buttons
		buttonsPanel = new JPanel();
		buttonsPanel.add(playButton);
		buttonsPanel.add(passButton);
		buttonsPanel.add(resetButton);

		// add components into the bigTwoPanel
		JPanel allTablesPanel = new JPanel();
		allTablesPanel.setLayout(new BoxLayout(allTablesPanel, BoxLayout.Y_AXIS));
		for(int i = 0; i < tableRowPanel.length; i++) {
			allTablesPanel.add(tableRowPanel[i]);
		}
		bigTwoPanel.add(BorderLayout.CENTER, allTablesPanel);
		bigTwoPanel.add(BorderLayout.SOUTH, buttonsPanel);
		
		//add components into the frame
		frame.setJMenuBar(menuBar); // the menu bar
		frame.add(BorderLayout.EAST, textPanel); // the msgArea
		frame.add(BorderLayout.CENTER, bigTwoPanel); // the big table
		
		frame.pack();
		frame.setResizable(false);
		frame.setVisible(true);
	}
	
	/**
	 * This method sets the index of the active player.
	 * @param activePlayer the current player
	 * 
	 */
	public void setActivePlayer(int activePlayer) {
		if (activePlayer < 0 || activePlayer >= game.getPlayerList().size()) {
			this.activePlayer = -1;
		} else {
			this.activePlayer = activePlayer;
		}
	}
	
	/**
	 * This method gets an array of indices of the cards selected.
	 * @return selectedIdx the array of indices of the cards selected.indices of the cards selected
	 * 
	 */
	public int[] getSelected() {
		int[] selectedIdx = new int[selected.length];
		for(int i = 0; i < selected.length; i++) { selectedIdx[i] = selected[i] ? i : -1; }
		return selectedIdx;
	}
	
	/** 
	 * This method resets the selected list of selected cards.
	 * 
	 */
	public void resetSelected() {
		selected = new boolean[52 / game.getNumOfPlayers()];
	}

	/**
	 * This method repaints the GUI.
	 * 
	 */
	public void repaint() {
		
		// disable buttons if the player is not the active player
		if (!(activePlayer == game.getPlayerID()) || !game.isPlaying()) {
			disable();
		} else { enable(); }
		
		// manages the cardLists of players
		for(int i = 0; i < cardListPanel.length - 1; i++) {
			if (game.getPlayerList().get(i).getCardsInHand().size() != 0) {
				// show cardLists of the player
				cardListPanel[i].setDisplayCards(game.getPlayerList().get(i).getCardsInHand());
			} else {
				// when the player play a hand that will make him win the game
				// clear the cardListPanel for him
				cardListPanel[i].setDisplayCards(new CardList());
			}
		}
		
		// manages the table (last hand played)
		if (game.getHandsOnTable().size() == 0) {
			// at the start of the game, no cards should be shown in the row for lastHand
			cardListPanel[cardListPanel.length - 1].setDisplayCards(new CardList());
		} else {
			// otherwise just show the lastHand
			cardListPanel[cardListPanel.length - 1].setDisplayCards(game.getHandsOnTable().get(game.getHandsOnTable().size() - 1));
		}
		
		// initialize the selections
		resetSelected();
		
		// repaint the panel
		frame.repaint();
		
	}
	
	/**
	 * This method prints specified string to the message area of the GUI
	 * @param String to be printed
	 */
	public void printMsg(String msg) {
		msgArea.append(msg + "\n");
		// set caret position so that user will not need to scroll
		// to see the new messages
		int msgLength = msgArea.getDocument().getLength();
		msgArea.setCaretPosition(msgLength);
	}

	/**
	 * This method prints specified string to the chatroom area of the GUI
	 * @param String to be printed
	 */
	public void printChatMsg(String msg) {
		chatArea.append(msg + "\n");
		// set caret position so that user will not need to scroll
		// to see the new messages
		int msgLength = chatArea.getDocument().getLength();
		chatArea.setCaretPosition(msgLength);
	}
	
	/**
	 * This method clears the message area of the GUI.
	 */
	public void clearMsgArea() {
		msgArea.setText("");
	}
	
	/**
	 * This method clears the message area of the GUI.
	 */
	public void clearChatArea() {
		chatArea.setText("");
	}
	
	/**
	 * This method resets the GUI.
	 */
	public void reset() {
		clearMsgArea();
		bigTwoPanel.repaint();
		enable();
	}

	/**
	 * This method enables user interactions with the GUI.
	 */
	public void enable() {
		playButton.setEnabled(true);
		passButton.setEnabled(true);
		resetButton.setEnabled(true);
		bigTwoPanel.setEnabled(true);
	}
	
	/**
	 * This method disables user interactions with the GUI.
	 */
	public void disable() {
		playButton.setEnabled(false);
		passButton.setEnabled(false);
		resetButton.setEnabled(false);
		bigTwoPanel.setEnabled(false);
	}


	/**
	 * This method enables connection.
	 */
	public void enableConnect() {
		connectMenuItem.setEnabled(true);
	}
	
	/**
	 * This method disables connection.
	 */
	public void disableConnect() {
		connectMenuItem.setEnabled(false);
	}
	
	/**
	 * This method enables chat function.
	 */
	public void enableChat() {
		chatInputField.setEditable(true);
	}
	
	/**
	 * This method disables chat function.
	 */
	public void disableChat() {
		chatInputField.setEditable(false);
	}
	
	/**
	 * This inner class models the panel that shows the player's information,
	 * including avatars and names.
	 *
	 */
	class PlayerPanel extends JPanel {
		
		private int playerID;
		public PlayerPanel(int playerID) { this.playerID = playerID; }
		
		public void paintComponent(Graphics g) {
		    
			String playerName = game.getPlayerList().get(playerID).getName();
		    
			if (playerName != "") { // Only when player have joined the game
		    	// align the name at the center of the playerPanel
		    	int x = (int) (getWidth() - g.getFontMetrics().stringWidth(playerName)) / 2;
		    	g.setColor(new Color(255, 231, 0));
				g.drawString(playerName, x, 10);
			
				g.drawImage(avatars[playerID], 10, 15, avatarsWidth, avatarsHeight, this);
			}
		}
	}
	
	/**
	 * This inner class models the panel for displaying the 
	 * card list.
	 *
	 */
	class CardListPanel extends JLayeredPane {

		private int playerID;
		private BigTwoPanel[] displayCardPanel;
		
		public CardListPanel(int playerID) { this.playerID = playerID; }
		
		public void setDisplayCards (CardList displayCards) {
			displayCardPanel = new BigTwoPanel[displayCards.size()];
			//for each card in hand
			for(int i = 0; i < displayCards.size(); i++) {
				//check if the CardListPanel is activePlayer or it is the table
				if (playerID == game.getPlayerID() || playerID == game.getNumOfPlayers()) {
					// show the card face when the player is activePlayer
					displayCardPanel[i] = cardPanel[displayCards.getCard(i).getRank()][displayCards.getCard(i).getSuit()];
				} else {
					//show card back
					displayCardPanel[i] = cardBackPanel[displayCards.getCard(i).getRank()][displayCards.getCard(i).getSuit()];
				}
				
				//set the corresponding index of the card in Player's hand to idx of the cardPanel
				cardPanel[displayCards.getCard(i).getRank()][displayCards.getCard(i).getSuit()].activePlayer = i;
			}
		}
		
		public void paintComponent(Graphics g) {
			removeAll();
			g.setColor(Color.WHITE); // default color for texts in the panel
	
			for(int i = 0, x = 0; i < displayCardPanel.length; i++, x += 17) {
				// make the card 'float' if it is selected
				if ((playerID == activePlayer) && selected[i]) {
					displayCardPanel[i].setBounds(x, 0, (int) cardDimension.getWidth(), (int) cardDimension.getHeight());
				} else {
					displayCardPanel[i].setBounds(x, 20, (int) cardDimension.getWidth(), (int) cardDimension.getHeight());
				}
				add(displayCardPanel[i], new Integer(i)); // add it into the panel
			}
			
			// show who played the last hand
			if (game.getNumOfPlayers() == playerID) {
				if (displayCardPanel.length > 0) {
					String lastHandPlayerName = 
							game.getHandsOnTable().get(game.getHandsOnTable().size() - 1).getPlayer().getName();
					g.drawString("Played by " + lastHandPlayerName, 0, 10);
				}
			}
		}
	}
	
	/**
	 * This inner class models the panel of a BigTwo Card.
	 *
	 */
	private class BigTwoPanel extends JPanel implements MouseListener {
		private int rank;
		private int suit;
		private int activePlayer = -1;
		private boolean isCardBack;
		
		public BigTwoPanel(int rank, int suit, boolean isCardBack) {
			this.rank = rank;
			this.suit = suit;
			this.isCardBack = isCardBack;
		}
		
		public void paintComponent(Graphics g) {
			// super.paintComponent(g);
			if (isCardBack) {
				g.drawImage(cardBackImage, 0, 0, getWidth(), getHeight(), this);
			} else {
				g.drawImage(cardImages[suit][rank], 0, 0, getWidth(), getHeight(), this);
			}
		}

		public void mouseClicked(MouseEvent e) {
			// only the owner of the set of cards can make selection
			if(game.getPlayerID() == game.getCurrentIdx()) {
				selected[activePlayer] = !selected[activePlayer]; // toggle selection
			}
			bigTwoPanel.repaint(); // repaint panel
		}
		
		public void mousePressed(MouseEvent e) {}
		public void mouseReleased(MouseEvent e) {}
		public void mouseEntered(MouseEvent e) {}
		public void mouseExited(MouseEvent e) {}
	}
	
	/**
	 * This inner class implements the ActionListener interface,
	 * which will handle button-click events for the “PLay” button.
	 *
	 */
	class PlayButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			for (boolean isSelected : selected) {
				if (isSelected) {
					game.makeMove(game.getCurrentIdx(), getSelected());
					return;
				}
			}
			
			// prompts warning if the player did not select any card
			JOptionPane.showMessageDialog(frame, "You must select at least 1 card.", 
					"Invalid Move!", JOptionPane.WARNING_MESSAGE);
		}
	}
	
	/**
	 * This is an inner class that implements the ActionListener interface,
	 * which will handle button-click events for the “Pass” button.
	 *
	 */
	class PassButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			resetSelected(); // reset selection despite cards chosen
			// check if pass is available for this round
			game.makeMove(game.getCurrentIdx(), getSelected()); 
		}
	}
	
	/**
	 * This is an inner class that implements the ActionListener interface,
	 * which will handle button-click events for the “Reset” button.
	 *
	 */
	class ResetButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			resetSelected();
		}
	}
	
	/**
	 * This inner class implements the ActionLisenter interface,
	 * which will handle menu-item-click events for the "Connect" menu item.
	 * 
	 */
	class ConnectMenuItemListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			game.makeConnection();	
		}
	}
	
	/**
	 * This inner class implements the ActionListener interface,
	 * which will handle menu-item-click events for the “Quit” menu item.
	 *
	 */
	class QuitMenuItemListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			// After user click this, confirmation is required
			 int confirmQuit = JOptionPane.showConfirmDialog(frame, 
					 "Are you sure?", "Quit Game", JOptionPane.YES_NO_OPTION);
			 if (confirmQuit == JOptionPane.YES_OPTION) {
				 System.exit(0);
			 }
		}
	}
	
	/**
	 * This inner class implements the ActionListener interface,
	 * which will handle menu-item-click events for the 
	 * “Clear Console” menu item.
	 *
	 */
	class ClearConsoleMenuItemListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			clearMsgArea();
		}
	}
	
	/**
	 * This inner class implements the ActionListener interface,
	 * which will handle menu-item-click events for the 
	 * “Clear Chat Message” menu item.
	 *
	 */
	class ClearChatMenuItemListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			clearChatArea();
		}
	}
	
	/**
	 * This inner class implements the ActionListener interface,
	 * which will validate the message and send type MSG to BigTwoServer.
	 *
	 */
	class ChatInputListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			// Send if player had enter some messages 
			if(!chatInputField.getText().isEmpty() || chatInputField.getText() == null) {
				game.sendMessage(new CardGameMessage(CardGameMessage.MSG, -1, chatInputField.getText()));
			}
			
			// After pressing enter, clear the input field
			chatInputField.setText("");
		}
	}
}

