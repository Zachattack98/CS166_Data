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
              while(usermenu) {
                System.out.println("MAIN MENU");
                System.out.println("---------");
                System.out.println("1. Goto Friend List");
                System.out.println("2. Update Profile");
                System.out.println("3. Write a new message");
                System.out.println("4. Send Friend Request");
                System.out.println(".........................");
                System.out.println("9. Log out");
                switch (readChoice()){
                   case 1: FriendList(esql); break;		//leave alone for now
                   case 2: UpdateProfile(esql, authorisedUser); break;	//includes changing password
                   case 3: NewMessage(esql); break;		//send a message to anyone on network
                   case 4: SendRequest(esql); break;	
                   
                   case 5: SearchPeople(esql); break;
                   case 6: ManageConnectionRequests(esql); break;
                   case 7: ViewFriendsProfile(esql); break;
                   case 8: ViewMessages(esql); break;
                   
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
	 String query = String.format("INSERT INTO USR (userId, password, email, contact_list) VALUES ('%s','%s','%s')", login, password, email);

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


/*      new         */
   public static String UpdateProfile(ProfNetwork esql, String authorisedUser){
	try {
		   System.out.println("\t1. update password");
		   switch (readChoice()){
		   	case 1:
		   		System.out.print("\tEnter new user password: ");
		   		String newPass = in.readLine();
		   		String query = String.format("UPDATE USR SET password = '%s' WHERE userId = '%s'", newPass, authorisedUser);
		   		esql.executeUpdate(query);
		   	break;
		   	default : System.out.println("Unrecognized choice!"); break;
		   }//end switch
		   
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
			String query = String.format("SELECT * FROM USR U WHERE U.name LIKE '%%%s%%", username);
			int userNum = esql.executeQuery(query);
		   }catch(Exception e){
			   System.err.println (e.getMessage ());
			   return null;
		    }
		return null;
    }

    /* send connection request to anyone who you want to befriend */
    public static String SendRequest(ProfNetwork esql){
	return null;
    }
	

    public static String ManageConnectionRequests(ProfNetwork esql, String authorisedUser, String[] temp_list){
	return null;
    }
	

    /*
    * Accesses the Friend List from the homepage
    * returns all users within the list or null if it's empty
    **/
   public static String FriendList(ProfNetwork esql){
      try{
	 String query = String.format("SELECT U.userId FROM USR U, CONNECTION_USR CU WHERE U.userId = CU.userId AND CU.status = 'Accepted'");
	 esql.executeQueryAndPrintResult(query);

	String query2 = String.format("SELECT U.name FROM USR"); //get name of user who the friend list your viewing belongs to

	//Show all friends accepted, not those denied!
	String[] friends = ManageConnectionRequests(userNum, query2, temp_list); //names of friends you accepted.
		
	System.out.print("\tCurrent Friends: ");	//list ALL your user request
	System.out.print("\t..................");
	for(int i = 0; i < friends.length; i++) {
		if(friends[j] == "Accepted")
			System.out.print(esql.executeQueryAndPrintResult(query));	//display each friends' name found within the list.
	}

      }catch(Exception e){
         System.err.println (e.getMessage ());
         return null;
      }
   }//end

    public static String ViewFriendsProfile(ProfNetwork esql){
	try{
		//Check whether your friend list is empty
		//String query = String.format("SELECT FROM");

		//if empty, set loop to 5; else set to 3

		String[] array_of_users;

		for (int i = 0; i < 3, i++) {
         		System.out.print("\tEnter the username whose profile you wish to view: ");
	 		System.out.print("\t..............................................");
			String user_search = in.readLine();	//at beginning of connection level, 
								//term for storing name of user you first search.

			//check if user exists in the dataset
			String query1 = String.format("SELECT U.userId FROM USR U WHERE U.name = '%s'", user_search);

			SearchPeople(esql.executeUpdate(query1)); //search for first user you want to view.

			if(i = 1) {	//after starting the connection you now check if they're in the previous user's (not your) friend list.
				//check other user's friend list
				
			}

			//Check for this user in your own friend list
			//if they are, reset connection level; else, continue


			//String[] temp_list = SendRequest(userNum); //transfer list of friends who you sent requests
	 		//int list_size = temp_list.length;	//number of friends in list

			//get the name, date of birth, and email of user
			String query2 = String.format("SELECT U.name, U.dateOfBirth, U.email FROM USR U");
			
			//get the work experiences of user
			String query3 = String.format("SELECT WE.company, WE.role, WE.location FROM USR U, WORK_EXPR WE WHERE U.userId = WE.userId");

			//get the educations of user
			String query4 = String.format("SELECT ED.institutionName, ED.major, ED.degree FROM USR U, EDUCATIONAL_DETAILS ED CU WHERE U.userId = ED.userId");

			System.out.print("\tUser's Profile: ");
	 		System.out.print("\t..................");
			esql.executeQueryAndPrintResult(query2);
			esql.executeQueryAndPrintResult(query3);
			esql.executeQueryAndPrintResult(query4);
			
			array_of_users[i] = esql.executeUpdate(query1);  //add this user's name to the array

			//You can message these users regardless of the (curent) connection level.
			System.out.print("\tWould you like to send a message to this user (y/n)?: ");
	 		System.out.print("\t......................................................");
			String user_message = in.readLine();

			while(user_message != "y" && user_message != "n") {
				System.out.print("\tInvalid input!! Try again: ");
         			String user_message = in.readLine();
  	 		}

			if(user_message == "y") {
				NewMessage(esql.executeUpdate(query1))
			}
		}

		System.out.print("\tWhich of these users would you like to send a request to, if any: ");
	 	System.out.print("\t..............................................");
		int user_request = in.readLine();	//enter user 1, 2, or 3 (chronological order of search) as the user to send the request to

		while(user_request < 1 || user_request > 3) {
			System.out.print("\tInvalid input!! Try again: ");
         		int user_request = in.readLine();
  	 	}

		if(user_request == 1) {
			SendRequest(array_of_users[0]);
		}
		else if(user_request == 2) {
			SendRequest(array_of_users[1]);
		}
		else if(user_request == 3) {
			SendRequest(array_of_users[2]);
		}
		/*else if(user_request == 4) {
			SendRequest(array_of_users[3]);
		}
		else if(user_request == 5) {
			SendRequest(array_of_users[4]);
		}*/
         	
	}catch(Exception e){
		System.err.println (e.getMessage ());
		return null;
	}
    }

    /* Send a message to another user */
    public static String NewMessage(ProfNetwork esql){
	try{
		String message = in.readLine();

	 	String query = String.format(SELECT M.msgId, M.contents FROM USR U, MESSAGE M WHERE U.userId = M.senderId AND M.status = 'Delivered' AND M.contents = '%s'", message);
		int message_box = esql.executeUpdate(query);

	 	if (message_box > 0)
			esql.executeQueryAndPrintResult(query);	//Display the contents of "your" sent message.
         	return null;
	}catch(Exception e){
		System.err.println (e.getMessage ());
		return null;
	}
    }

    /* View messages sent to you, and choose whether to delete them */
    public static String ViewMessages(ProfNetwork esql){
	try{
	 	String query = String.format(SELECT M.msgId, M.contents FROM USR U, MESSAGE M WHERE U.userId = M.receiverId AND deleteStatus != 'Deleted'");
	 	int userNum = esql.executeUpdate(query);

	 	if (userNum > 0)
			System.out.print(query);	//Display the contents of "other user's" sent message.
         	
		System.out.print("\tDo you wish to delete this message? (y/n): ");	//choose whether to delete this message, and only this message.
	 	System.out.print("\t..................");
		String delete_choice = in.readLine();

		while(delete_choice != "y" && delete_choice != "n") {
			System.out.print("\tInvalid input!! Try again: ");
         		String delete_choice = in.readLine();
  	 	}

	 	if (delete_choice == "y") {
			//Change delete status to 'Deleted' !!!!NEEDS ATTENTION!!!!
			executeUpdate
	 	}

		return null;
	}catch(Exception e){
		System.err.println (e.getMessage ());
		return null;
	}
    }
	
/*      new         */
}//end ProfNetwork
