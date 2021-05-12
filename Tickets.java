

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;

@SuppressWarnings("serial")
public class Tickets extends JFrame implements ActionListener {

	// class level member objects
	Dao dao = new Dao(); // for CRUD operations
	Boolean chkIfAdmin = null;

	// Main menu object items
	private JMenu mnuFile = new JMenu("File");
	private JMenu mnuAdmin = new JMenu("Admin");
	private JMenu mnuTickets = new JMenu("Tickets");

	// Sub menu item objects for all Main menu item objects
	JMenuItem mnuItemExit;
	JMenuItem mnuItemUpdate;
	JMenuItem mnuItemDelete;
	JMenuItem mnuItemOpenTicket;
  JMenuItem mnuItemUpdateTicket;
	JMenuItem mnuItemViewTicket;
  JMenuItem mnuItemCloseTicket;
  int usrid;

	public Tickets(Boolean isAdmin, int uid) {
    usrid= uid;
		chkIfAdmin = isAdmin;
		createMenu();
		prepareGUI();

	}
  

	private void createMenu() {

		/* Initialize sub menu items **************************************/

		// initialize sub menu item for File main menu
		mnuItemExit = new JMenuItem("Exit");
		// add to File main menu item
		mnuFile.add(mnuItemExit);
    if(chkIfAdmin){
      	// initialize first sub menu items for Admin main menu
		mnuItemUpdate = new JMenuItem("Update Ticket");
		// add to Admin main menu item
		mnuAdmin.add(mnuItemUpdate);

		// initialize second sub menu items for Admin main menu
		mnuItemDelete = new JMenuItem("Delete Ticket");
		// add to Admin main menu item
		mnuAdmin.add(mnuItemDelete);

    }
	
		// initialize first sub menu item for Tickets main menu
		mnuItemOpenTicket = new JMenuItem("Open Ticket");
		// add to Ticket Main menu item
		mnuTickets.add(mnuItemOpenTicket);

		// initialize second sub menu item for Tickets main menu
		mnuItemViewTicket = new JMenuItem("View Ticket");
		// add to Ticket Main menu item
		mnuTickets.add(mnuItemViewTicket);
    if(!chkIfAdmin){
      mnuItemUpdateTicket = new JMenuItem("Update Ticket");
		// add to Ticket Main menu item
		mnuTickets.add(mnuItemUpdateTicket);
    }
		// initialize any more desired sub menu items below
		mnuItemCloseTicket = new JMenuItem("Close Ticket");
		mnuTickets.add(mnuItemCloseTicket);
		/* Add action listeners for each desired menu item *************/
		mnuItemExit.addActionListener(this);
    if(chkIfAdmin){
      mnuItemUpdate.addActionListener(this);
		  mnuItemDelete.addActionListener(this);
    }else{
      mnuItemUpdateTicket.addActionListener(this);
    }
		mnuItemOpenTicket.addActionListener(this);
		mnuItemViewTicket.addActionListener(this);
    mnuItemCloseTicket.addActionListener(this);

		 /*
		  * continue implementing any other desired sub menu items (like 
		  * for update and delete sub menus for example) with similar 
		  * syntax & logic as shown above*
		 */


	}

	private void prepareGUI() {

		// create JMenu bar
		JMenuBar bar = new JMenuBar();
		bar.add(mnuFile); // add main menu items in order, to JMenuBar
    if(chkIfAdmin){
      bar.add(mnuAdmin);
    }
		bar.add(mnuTickets);
		// add menu bar components to frame
		setJMenuBar(bar);

		addWindowListener(new WindowAdapter() {
			// define a window close operation
			public void windowClosing(WindowEvent wE) {
				System.exit(0);
			}
		});
		// set frame options
		setSize(400, 400);
		getContentPane().setBackground(Color.LIGHT_GRAY);
		setLocationRelativeTo(null);
		setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// implement actions for sub menu items
		if (e.getSource() == mnuItemExit) {
			System.exit(0);
		} else if (e.getSource() == mnuItemOpenTicket) {

			// get ticket information
			String ticketName = JOptionPane.showInputDialog(null, "Enter your name");
			String ticketDesc = JOptionPane.showInputDialog(null, "Enter a ticket description");
      String priority = JOptionPane.showInputDialog(null, "Enter the ticket priority");
			// insert ticket information to database

			int id = dao.insertRecords(usrid, ticketName, ticketDesc, priority);

			// display results if successful or not to console / dialog box
			if (id != 0) {
				System.out.println("Ticket ID : " + id + " created successfully!!!");
				JOptionPane.showMessageDialog(null, "Ticket id: " + id + " created");
			} else
				System.out.println("Ticket cannot be created!!!");
		}

		else if (e.getSource() == mnuItemViewTicket) {

			// retrieve all tickets details for viewing in JTable
			try {
        JTable jt;
				// Use JTable built in functionality to build a table model and
				// display the table model off your result set!!!
        if (chkIfAdmin){
          jt = new JTable(ticketsJTable.buildTableModel(dao.readRecords()));
        }else{
          jt = new JTable(ticketsJTable.buildTableModel(dao.readRecords(usrid)));
        }
				
				jt.setBounds(30, 40, 200, 400);
				JScrollPane sp = new JScrollPane(jt);
				add(sp);
				setVisible(true); // refreshes or repaints frame on screen

			} catch (SQLException e1) {
        System.out.println("Here15");
				e1.printStackTrace();
			}
		}
		/*
		 * continue implementing any other desired sub menu items (like for update and
		 * delete sub menus for example) with similar syntax & logic as shown above
		 */
    else if (e.getSource() == mnuItemUpdate | e.getSource() == mnuItemUpdateTicket){
      	// get ticket information
	    int ticket = Integer.parseInt(JOptionPane.showInputDialog(null, "Enter the id of the ticket you want to pdate"));
			String ticketDesc = JOptionPane.showInputDialog(null, "Enter an updated ticket description");
      dao.updateRecords(ticket, ticketDesc);
      
    }

    else if (e.getSource() == mnuItemDelete){
      int ticket = Integer.parseInt(JOptionPane.showInputDialog(null, "Enter the id of the ticket you want to delete"));
      dao.deleteRecords(ticket);
    }

    else if (e.getSource() == mnuItemCloseTicket){
      int ticket = Integer.parseInt(JOptionPane.showInputDialog(null, "Enter the id of the ticket you want to close"));
      dao.updateRecords(ticket, java.time.LocalDate.now());
    }
	}

    

}
