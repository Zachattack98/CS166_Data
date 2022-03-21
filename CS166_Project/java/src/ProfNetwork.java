/*
 * Template JAVA User Interface
 * =============================
 *
 * Database Management Systems
 * Department of Computer Science &amp; Engineering
 * University of California - Riverside
 *
 * Target DBMS: 'Postgres'
 *
 */


import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ArrayList;

/**
 * This class defines a simple embedded SQL utility class that is designed to
 * work with PostgreSQL JDBC drivers.
 *
 */
public class ProfNetwork {

   // reference to physical database connection.
   private Connection _connection = null;

   // handling the keyboard inputs through a BufferedReader
   // This variable can be global for convenience.
   static BufferedReader in = new BufferedReader(
                                new InputStreamReader(System.in));

   /**
    * Creates a new instance of ProfNetwork
    *
    * @param hostname the MySQL or PostgreSQL server hostname
    * @param database the name of the database
    * @param username the user name used to login to the database
    * @param password the user login password
    * @throws java.sql.SQLException when failed to make a connection.
    */
   public ProfNetwork (String dbname, String dbport, String user, String passwd) throws SQLException {

      System.out.print("Connecting to database...");
      try{
         // constructs the connection URL
         String url = "jdbc:postgresql://localhost:" + dbport + "/" + dbname;
         System.out.println ("Connection URL: " + url + "\n");

         // obtain a physical connection
         this._connection = DriverManager.getConnection(url, user, passwd);
         System.out.println("Done");
      }catch (Exception e){
         System.err.println("Error - Unable to Connect to Database: " + e.getMessage() );
         System.out.println("Make sure you started postgres on this machine");
         System.exit(-1);
      }//end catch
   }//end ProfNetwork

   /**
    * Method to execute an update SQL statement.  Update SQL instructions
    * includes CREATE, INSERT, UPDATE, DELETE, and DROP.
    *
    * @param sql the input SQL string
    * @throws java.sql.SQLException when update failed
    */
   public void executeUpdate (String sql) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the update instruction
      stmt.executeUpdate (sql);

      // close the instruction
      stmt.close ();
   }//end executeUpdate

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and outputs the results to
    * standard out.
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQueryAndPrintResult (String query) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the query instruction
      ResultSet rs = stmt.executeQuery (query);

      /*
       ** obtains the metadata object for the returned result set.  The metadata
       ** contains row and column info.
       */
      ResultSetMetaData rsmd = rs.getMetaData ();
      int numCol = rsmd.getColumnCount ();
      int rowCount = 0;

      // iterates through the result set and output them to standard out.
      boolean outputHeader = true;
      while (rs.next()){
	 if(outputHeader){
	    for(int i = 1; i <= numCol; i++){
		System.out.print(rsmd.getColumnName(i) + "\t");
	    }
	    System.out.println();
	    outputHeader = false;
	 }
         for (int i=1; i<=numCol; ++i)
            System.out.print (rs.getString (i) + "\t");
         System.out.println ();
         ++rowCount;
      }//end while
      stmt.close ();
      return rowCount;
   }//end executeQuery

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and returns the results as
    * a list of records. Each record in turn is a list of attribute values
    *
    * @param query the input query string
    * @return the query result as a list of records
    * @throws java.sql.SQLException when failed to execute the query
    */
   public List<List<String>> executeQueryAndReturnResult (String query) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the query instruction
      ResultSet rs = stmt.executeQuery (query);

      /*
       ** obtains the metadata object for the returned result set.  The metadata
       ** contains row and column info.
       */
      ResultSetMetaData rsmd = rs.getMetaData ();
      int numCol = rsmd.getColumnCount ();
      int rowCount = 0;

      // iterates through the result set and saves the data returned by the query.
      boolean outputHeader = false;
      List<List<String>> result  = new ArrayList<List<String>>();
      while (rs.next()){
          List<String> record = new ArrayList<String>();
         for (int i=1; i<=numCol; ++i)
            record.add(rs.getString (i));
         result.add(record);
      }//end while
      stmt.close ();
      return result;
   }//end executeQueryAndReturnResult

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and returns the number of results
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQuery (String query) throws SQLException {
       // creates a statement object
       Statement stmt = this._connection.createStatement ();

       // issues the query instruction
       ResultSet rs = stmt.executeQuery (query);

       int rowCount = 0;

       // iterates through the result set and count nuber of results.
       if(rs.next()){
          rowCount++;
       }//end while
       stmt.close ();
       return rowCount;
   }

   /**
    * Method to fetch the last value from sequence. This
    * method issues the query to the DBMS and returns the current
    * value of sequence used for autogenerated keys
    *
    * @param sequence name of the DB sequence
    * @return current value of a sequence
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int getCurrSeqVal(String sequence) throws SQLException {
	Statement stmt = this._connection.createStatement ();

	ResultSet rs = stmt.executeQuery (String.format("Select currval('%s')", sequence));
	if (rs.next())
		return rs.getInt(1);
	return -1;
   }

   /**
    * Method to close the physical connection if it is open.
    */
   public void cleanup(){
      try{
         if (this._connection != null){
            this._connection.close ();
         }//end if
      }catch (SQLException e){
         // ignored.
      }//end try
   }//end cleanup

   /**
    * The main execution method
    *
    * @param args the command line arguments this inclues the <mysql|pgsql> <login file>
    */
   public static void main (String[] args) {
      if (args.length != 3) {
         System.err.println (
            "Usage: " +
            "java [-classpath <classpath>] " +
            ProfNetwork.class.getName () +
            " <dbname> <port> <user>");
         return;
      }//end if

      Greeting();
      ProfNetwork esql = null;
      try{
         // use postgres JDBC driver.
         Class.forName ("org.postgresql.Driver").newInstance ();
         // instantiate the ProfNetwork object and creates a physical
         // connection.
         String dbname = args[0];
         String dbport = args[1];
         String user = args[2];
         esql = new ProfNetwork (dbname, dbport, user, "");

         boolean keepon = true;
         while(keepon) {
            // These are sample SQL statements
            System.out.println("MAIN MENU");
            System.out.println("---------");
            System.out.println("1. Create user");
            System.out.println("2. Log in");
            System.out.println("9. < EXIT");
            String authorisedUser = null;
            switch (readChoice()){
               case 1: CreateUser(esql); break;
               case 2: authorisedUser = LogIn(esql); break;
               case 9: keepon = false; break;
               default : System.out.println("Unrecognized choice!"); break;
            }//end switch
            if (authorisedUser != null) {
              boolean usermenu = true;
              
              //output user profile
              String userprofile1 = String.format("SELECT U.userId, U.email, U.name, U.dateOfBirth FROM USR U WHERE U.userId = '%s';", authorisedUser);
              int returnprofile = esql.executeQueryAndPrintResult(userprofile1);	//2.1.1
              
              
              while(usermenu) {
                System.out.println("MAIN MENU");
                System.out.println("---------");
                System.out.println("1. View Friends List");
                System.out.println("2. Update Profile");
                System.out.println("3. Write a new message");
                System.out.println("4. Send 5 Friend Requests (if new user)");
                System.out.println("5. Search People");
                System.out.println("6. Manage Connection Requests");
                System.out.println("7. View Profiles & Send Connection Requests");
                System.out.println("8. View Messages and Option to Delete");
                System.out.println(".........................");
                System.out.println("9. Log out");
                switch (readChoice()){
                   case 1: FriendList(esql, authorisedUser); break;		//leave alone for now
                   case 2: UpdateProfile(esql, authorisedUser); break;	//includes changing password
                   case 3: NewMessage(esql, authorisedUser); break;		//send a message to anyone on network
                   case 4: SendFiveRequest(esql, authorisedUser); break;	
                   
                   case 5: SearchPeople(esql); break;
                   case 6: ManageConnectionRequests(esql, authorisedUser); break;
                   case 7: ViewFriendsProfile(esql, authorisedUser); break;
                   case 8: ViewMessages(esql, authorisedUser); break;
                   
                   case 9: usermenu = false; break;
                   default : System.out.println("Unrecognized choice!"); break;
                }
              }
            }
         }//end while
      }catch(Exception e) {
         System.err.println (e.getMessage ());
      }finally{
         // make sure to cleanup the created table and close the connection.
         try{
            if(esql != null) {
               System.out.print("Disconnecting from database...");
               esql.cleanup ();
               System.out.println("Done\n\nBye !");
            }//end if
         }catch (Exception e) {
            // ignored.
         }//end try
      }//end try
   }//end main

   public static void Greeting(){
      System.out.println(
         "\n\n*******************************************************\n" +
         "              User Interface      	               \n" +
         "*******************************************************\n");
   }//end Greeting

   /*
    * Reads the users choice given from the keyboard
    * @int
    **/
   public static int readChoice() {
      int input;
      // returns only if a correct value is given.
      do {
         System.out.print("Please make your choice: ");
         try { // read the integer, parse it and break.
            input = Integer.parseInt(in.readLine());
            break;
         }catch (Exception e) {
            System.out.println("Your input is invalid!");
            continue;
         }//end try
      }while (true);
      return input;
   }//end readChoice

   /*
    * Creates a new user with privided login, passowrd and phoneNum
    * An empty block and contact list would be generated and associated with a user
    **/
   public static void CreateUser(ProfNetwork esql){
      try{
         System.out.print("\tEnter user login: ");
         String login = in.readLine();
         System.out.print("\tEnter user password: ");
         String password = in.readLine();
         System.out.print("\tEnter user email: ");
         String email = in.readLine();

	 //Creating empty contact\block lists for a user
	 String query = String.format("INSERT INTO USR (userId, password, email) VALUES ('%s','%s','%s')", login, password, email);

         esql.executeUpdate(query);
         System.out.println ("User successfully created!");
      }catch(Exception e){
         System.err.println (e.getMessage ());
      }
   }//end

   /*
    * Check log in credentials for an existing user
    * @return User login or null is the user does not exist
    **/
   public static String LogIn(ProfNetwork esql){
      try{
         System.out.print("\tEnter user login: ");
         String login = in.readLine();
         System.out.print("\tEnter user password: ");
         String password = in.readLine();

         String query = String.format("SELECT * FROM USR WHERE userId = '%s' AND password = '%s'", login, password);
         int userNum = esql.executeQuery(query);
	 if (userNum > 0)
		return login;
     return null;
      }catch(Exception e){
         System.err.println (e.getMessage ());
         return null;
      }
   }//end

   
   public static String FriendList(ProfNetwork esql, String authorisedUser){
	   try {
	   String query = String.format("SELECT C1.connectionId FROM CONNECTION_USR C1 WHERE C1.userId = '%s' AND C1.status = 'Accept' UNION SELECT C2.userId FROM CONNECTION_USR C2 WHERE C2.connectionId = '%s' AND C2.status = 'Accept';", authorisedUser, authorisedUser);
       int userNum = esql.executeQueryAndPrintResult(query);
	   } catch(Exception e){
		   System.err.println (e.getMessage ());
		   return null;
	    }
	   
	   return null;
   }
// Rest of the functions definition go in here
	public static String UpdateProfile(ProfNetwork esql, String authorisedUser){
	try {
		   System.out.println("\t1. update password");
		   System.out.println("\t2. Exit");
		   switch (readChoice()){
		   	case 1:
		   		System.out.print("\tEnter new user password: ");
		   		String newPass = in.readLine();
		   		String query = String.format("UPDATE USR SET password = '%s' WHERE userId = '%s';", newPass, authorisedUser);
		   		esql.executeUpdate(query);
		   	 break;
		   	case 2:
		   		System.out.println("Exiting Update Profile");
		   	 break;
		   	default : System.out.println("Unrecognized choice!"); break;
		   }//end switch
		   
	   }catch(Exception e){
		   System.err.println (e.getMessage ());
		   return null;
	    }
		return null;
    }
	public static void NewMessage(ProfNetwork esql, String authorisedUser){
        try{
                String query = "SELECT M.msgId FROM MESSAGE M WHERE M.msgId = (SELECT MAX(M2.msgId) FROM MESSAGE M2)";

                List<List<String>> messageId = new ArrayList<List<String>>(); //create new array list
                messageId = esql.executeQueryAndReturnResult(query); //newmessageId that is one higher than nax in the current table

                int temp_Id = Integer.parseInt(messageId.get(0).get(0));

                //System.out.print("Enter your name as the sender: ");
                //String sender = in.readLine();

                System.out.print("Enter name of person who you're messaging: ");
                String receiver = in.readLine();

                System.out.println("Enter your message below:");
                System.out.println(".........................");
                String message = in.readLine(); //enter the contents your message, no more than 500 characters

                if(receiver.length() != 0 && message.length() != 0) { //check if anything is inputted for sender, receiver, and message box
                        String query2 = String.format("INSERT INTO MESSAGE (msgId, senderId, receiverId, contents, sendTime, deleteStatus, status) VALUES ('%s', '%s', '%s', '%s', '3/17/2022 14:02', 0, 'Sent')", temp_Id+1, authorisedUser, receiver, message); //update message with inputted contents
                        esql.executeUpdate(query2);
                }
        }catch(Exception e){
                System.err.println (e.getMessage ());
        }
    }

	public static String SendFiveRequest(ProfNetwork esql, String authorisedUser){
		try {
			System.out.println("HELLO");
			String query = String.format("SELECT C1.connectionId FROM CONNECTION_USR C1 WHERE C1.userId = '%s' AND C1.status = 'Accept' UNION SELECT C2.userId FROM CONNECTION_USR C2 WHERE C2.connectionId = '%s' AND C2.status = 'Accept';", authorisedUser, authorisedUser);
            List<List<String>> friends = new ArrayList<List<String>>();
            friends = esql.executeQueryAndReturnResult(query);
			//System.out.println(friends);
            //System.out.println("HELLO2");
			if(!friends.isEmpty()) {
				System.out.println("Hey! You have friends! Go talk to them!");
				return null;
			}
			//System.out.println("HELLO3");
			for(int i = 0; i < 5; i++) {
				System.out.println("\t1. Send Connection Request");
				System.out.println("\t2. Stop Adding Friends");
				switch(readChoice()) {
					case 1:
						System.out.print("\tEnter Valid Friend Name: ");
						String friendName = in.readLine();
						String queryAddConnection = String.format("insert into CONNECTION_USR values ('%s', '%s', 'Request');", authorisedUser, friendName);
                    	esql.executeUpdate(queryAddConnection);
                    	System.out.println("Connection Request Sent");
					 break;
					case 2:
						i = 5;
					 break;
					default: System.out.println("Unrecognized choice!"); 
					 break;
				}
				
			}
			System.out.println("HELLO4");
		}catch(Exception e){
			   System.err.println (e.getMessage ());
			   return null;
		    }
		return null;
	}
	public static String SearchPeople(ProfNetwork esql){
		try {
			System.out.print("\tEnter username: ");
			String username = in.readLine();
			String query = String.format("SELECT U.email, U.name FROM USR U WHERE U.userId = '%s';", username); //2.1.3
			int userNum = esql.executeQueryAndPrintResult(query);
			String query1 = String.format("SELECT W.userId, W.company, W.role, W.location, W.startDate, W.endDate FROM WORK_EXPR W WHERE W.userId = '%s';", username);
    		String query2 = String.format("SELECT E.userId, E.instituitionName, E.major, E.degree, E.startDate, E.enddate FROM EDUCATIONAL_DETAILS E WHERE E.userId = '%s';", username);
    		int userNum1 = esql.executeQueryAndPrintResult(query1);
    		int userNum2 = esql.executeQueryAndPrintResult(query2);
		   }catch(Exception e){
			   System.err.println (e.getMessage ());
			   return null;
		    }
		return null;
	}
	public static void ManageConnectionRequests(ProfNetwork esql, String authorisedUser){
        try{
                //the one with connectionId is the one whose receiving the pending requests
                String query = String.format("SELECT C.userId FROM CONNECTION_USR C WHERE C.connectionId = '%s' AND C.status = 'Request'", authorisedUser);

                List<List<String>> user_list = new ArrayList<List<String>>(); //create new array list

                user_list = esql.executeQueryAndReturnResult(query);
                int list_size = user_list.size();       //number of user with a pending request

                System.out.printf("\tYou have %d Pending Requests!\n", list_size);

                esql.executeQueryAndPrintResult(query); //print all user in the list

                for(int i = 0; i < list_size; i++) {
                        //choose whether to accept or reject the next request
                        System.out.printf("\tDo you wish to accept (a) or reject (r) request %d (ignore is anything else)?: ", i+1);
                        String request_choice = in.readLine();

                        switch(request_choice) {
                                case "a":
                                        String query2 = String.format("UPDATE CONNECTION_USR SET status = 'Accept' WHERE userId = '%s' AND connectionID = '%s'", user_list.get(i).get(0), authorisedUser); //friend request accepted
                                        esql.executeUpdate(query2);
                                        break;

                                case "r":
                                        String query3 = String.format("UPDATE CONNECTION_USR SET status = 'Reject' WHERE userId = '%s' AND connectionID = '%s'", user_list.get(i).get(0), authorisedUser);  //friend request denied
                                        esql.executeUpdate(query3);
                                        break;
                                default:
                                        break;
                        }
                }
      }catch(Exception e){
         System.err.println (e.getMessage ());
      }
   }
	public static String ViewFriendsProfile(ProfNetwork esql, String authorisedUser){
        try {
                int connectionLevel = 0;
                String usernameConnection = authorisedUser;
                while(connectionLevel <= 3 && connectionLevel >= 0) {
                	System.out.println("\t1. View Friends of Currently Viewing Profile");
                    System.out.println("\t2. View Own Profile");
                    System.out.println("\t3. View Profile");
                    System.out.println("\t4. Exit");
//                    System.out.println("\t4. Send Connection Request");
	                switch(readChoice()) {
	                        case 1:
	                        		String currConnectionLvl = String.format("Current Connection Level: %d", connectionLevel);
	                        		System.out.println(currConnectionLvl);
	                                String query = String.format("SELECT C1.connectionId FROM CONNECTION_USR C1 WHERE C1.userId = '%s' AND C1.status = 'Accept' UNION SELECT C2.userId FROM CONNECTION_USR C2 WHERE C2.connectionId = '%s' AND C2.status = 'Accept';", usernameConnection, usernameConnection);
	                                int userNum = esql.executeQueryAndPrintResult(query);
	                                break;
	                        case 2:
	                        		String query1 = String.format("SELECT U.email, U.name, U.dateOfBirth FROM USR U WHERE U.userId = '%s';", authorisedUser);
	                        		String query2 = String.format("SELECT W.userId, W.company, W.role, W.location, W.startDate, W.endDate FROM WORK_EXPR W WHERE W.userId = '%s';", authorisedUser);
	                        		String query3 = String.format("SELECT E.userId, E.instituitionName, E.major, E.degree, E.startDate, E.enddate FROM EDUCATIONAL_DETAILS E WHERE E.userId = '%s';", authorisedUser);
	                        		int userNum1 = esql.executeQueryAndPrintResult(query1);
	                        		int userNum2 = esql.executeQueryAndPrintResult(query2);
	                        		int userNum3 = esql.executeQueryAndPrintResult(query3);
	                        		break;
	                        case 3:
	                        	connectionLevel += 1;
	                        	if(connectionLevel > 3) {
	                        		break;
	                        	}
	                        	String currConnectionLvl3 = String.format("Current Connection Level: %d", connectionLevel);
                        		System.out.println(currConnectionLvl3);
	                        	System.out.print("\tEnter friend name: ");
	                        	usernameConnection = in.readLine();
	                        	String query4 = String.format("SELECT U.email, U.name, U.dateOfBirth FROM USR U WHERE U.userId = '%s';", usernameConnection); //2.1.2
	                    		String query5 = String.format("SELECT W.userId, W.company, W.role, W.location, W.startDate, W.endDate FROM WORK_EXPR W WHERE W.userId = '%s';", usernameConnection);
	                    		String query6 = String.format("SELECT E.userId, E.instituitionName, E.major, E.degree, E.startDate, E.enddate FROM EDUCATIONAL_DETAILS E WHERE E.userId = '%s';", usernameConnection);
	                    		int userNum4 = esql.executeQueryAndPrintResult(query4);
	                    		int userNum5 = esql.executeQueryAndPrintResult(query5);
	                    		int userNum6 = esql.executeQueryAndPrintResult(query6);
	                    		
	                    		boolean keepon = true;
	                    		while(keepon) {
	                    			System.out.println("1. Send them a connection request");
	                    			System.out.println("2. Send them a message");
	                    			System.out.println("3. exit profile");
	                    			switch(readChoice()) {
	                    				case 1: 
	                    					String query7 = String.format("insert into CONNECTION_USR values ('%s', '%s', 'Request');", authorisedUser, usernameConnection);
	        	                        	int userNum7 = esql.executeQuery(query7);
	        	                        	System.out.println("Connection Request Sent");
	                    				 break;
	                    				case 2:
	                    					List<List<String>> messageId = new ArrayList<List<String>>(); //create new array list
	                    					String queryMsg = "SELECT M.msgId FROM MESSAGE M WHERE M.msgId = (SELECT MAX(M2.msgId) FROM MESSAGE M2)";
	                    	                messageId = esql.executeQueryAndReturnResult(queryMsg); //newmessageId that is one higher than nax in the current table
	                    	                int temp_Id = Integer.parseInt(messageId.get(0).get(0));
	                    	                System.out.println("Enter your message below:");
	                    	                System.out.println(".........................");
	                    	                String message = in.readLine(); //enter the contents your message, no more than 500 characters
	                    	                        String queryInsertMessage = String.format("INSERT INTO MESSAGE (msgId, senderId, receiverId, contents, sendTime, deleteStatus, status) VALUES ('%s', '%s', '%s', '%s', '3/17/2022 14:02', 0, 'Sent')", temp_Id+1, authorisedUser, usernameConnection, message); //update message with inputted contents
	                    	                        esql.executeUpdate(queryInsertMessage);
	                    				 break;
	                    				case 3: keepon = false; break;
	                    				default: System.out.println("Unrecognized choice!"); break;
	                    				 
	                    			}
	                    		}
	                    		
	                    		break;
	                    		
	                        case 4:
	                        	connectionLevel = -1;	//exit
	                        	System.out.println("Exiting View Friends");
	                         break;
//	                        case 4:
//	                        	System.out.println("\tEnter friend name to send connection request to: ");
//	                        	String username2 = in.readLine();
//	                        	String query7 = String.format("insert into CONNECTION_USR values ('%s', '%s', 'Request');", authorisedUser, username2);
//	                        	int userNum7 = esql.executeQuery(query7);
//	                         break;
	            		   	default : System.out.println("Unrecognized choice!"); break;
	                }
                }
                if(connectionLevel > 3) {
                	System.out.println("Connection Level > 3, You are not allowed this information, goodbye");
                }

        }catch(Exception e){
                   System.err.println (e.getMessage ());
                   return null;
            }

        return null;
}
	/* View messages and choose whether to delete them */
	public static void ViewMessages(ProfNetwork esql, String authorisedUser){
        try{
              //0, both users still have message in their inboxs
              //1, sender deleted message but receiver has not
              //2, receiver deleted message but sender has not
              //3, both users have deleted message from inbox
              String query = String.format("SELECT M.msgId, M.contents FROM MESSAGE M WHERE M.receiverId = '%s' AND (M.deleteStatus = 0 OR M.deleteStatus = 1)", authorisedUser);

              String query1 = String.format("SELECT M.msgId, M.contents FROM MESSAGE M WHERE M.senderId = '%s' AND (M.deleteStatus = 0 OR M.deleteStatus = 2)", authorisedUser);

                List<List<String>> message1_list = new ArrayList<List<String>>(); //create new array list
                message1_list = esql.executeQueryAndReturnResult(query);
                int list_size1 = message1_list.size();       //number of user with a pending request

                List<List<String>> message2_list = new ArrayList<List<String>>(); //create new array list
                message2_list = esql.executeQueryAndReturnResult(query1);
                int list_size2 = message2_list.size();       //number of user with a pending request

                //user is considered the receiver
                for(int i = 0; i < list_size1; i++) {
                        System.out.printf("\n%s", message1_list.get(i).get(0));
                        System.out.printf("\t%s", message1_list.get(i).get(1));
                        System.out.printf("\nDo you wish to delete message %s (y for yes; anything else is no)?: ", i+1);    //choose whether to delete this message, and only this message.
                        String delete_choice = in.readLine();

                        switch(delete_choice) {

                                case "y":
                                        //when neither user has deleted message
                                        String query2 = String.format("UPDATE MESSAGE SET deleteStatus = 2 WHERE receiverId = '%s' AND deleteStatus = 0", authorisedUser);
                                        //when sender has already deleted message
                                        String query3 = String.format("UPDATE MESSAGE SET deleteStatus = 3 WHERE receiverId = '%s' AND deleteStatus = 1", authorisedUser);

                                        esql.executeUpdate(query2);
                                        esql.executeUpdate(query3);
                                        break;
                                default:
                                        break;
                        }
                }

		//user is now considered the sender
                for(int i = 0; i < list_size2; i++) {
                        System.out.printf("\n%s", message2_list.get(i).get(0));
                        System.out.printf("\t%s", message2_list.get(i).get(1));
                        System.out.printf("\nDo you wish to delete message %s (y for yes; anything else is no)?: ", i+1);    //choose whether to delete this message, and only this message.
                        String delete_choice2 = in.readLine();

                        switch(delete_choice2) {

                                case "y":
                                        //when neither user has deleted message
                                        String query4 = String.format("UPDATE MESSAGE SET deleteStatus = 1 WHERE senderId = '%s' AND deleteStatus = 0", authorisedUser);
                                        //when sender has already deleted message
                                        String query5 = String.format("UPDATE MESSAGE SET deleteStatus = 3 WHERE senderId = '%s' AND deleteStatus = 2", authorisedUser);

                                        esql.executeUpdate(query4);
                                        esql.executeUpdate(query5);
                                        break;
                                default:
                                        break;
                        }
                }
        }catch(Exception e){
                System.err.println (e.getMessage ());
        }
   }
	
}//end ProfNetwork
