package com.grameenphone.hello.dbhelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


import com.grameenphone.hello.Utils.Compare;
import com.grameenphone.hello.Utils.Constant;
import com.grameenphone.hello.model.Chat;
import com.grameenphone.hello.model.ChatRoom;
import com.grameenphone.hello.model.User;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DatabaseHelper extends SQLiteOpenHelper {



    public static final String DB_NAME = "mars";
    private static final String TAG = "DatabaseHelper";

    public static String DB_PATH;
    private SQLiteDatabase database;
    private Context context;





    public DatabaseHelper(Context context){


        super(context, DB_NAME, null, 1);
        this.context = context;

        DB_PATH = context.getFilesDir().getPath() + "/databases/";
        this.database = openDatabase();

    }





    public SQLiteDatabase openDatabase(){
        if(database==null){
            createDatabase();
            Log.e(getClass().getName(), "Database created...");
        }

        return database;
    }


    private void createDatabase(){
        boolean dbExists = checkDB();
        if(!dbExists){
            this.getReadableDatabase();
            database = context.openOrCreateDatabase(DB_NAME, Context.MODE_PRIVATE, null);
            createTable();
            Log.e(getClass().getName(),"No Database");
        }

    }




    public SQLiteDatabase getDatabase(){
        String path = DB_PATH + DB_NAME;
        database = SQLiteDatabase.openDatabase(path, null,
                SQLiteDatabase.OPEN_READWRITE);
        return database;
    }







    public void createTable() {
        String CREATE_CHAT_ROOMS = "CREATE TABLE IF NOT EXISTS " + Constant.Database.TABLE_CHAT_ROOMS + "("
                + Constant.Database.Chatroom.INDEX + " VARCHAR PRIMARY KEY UNIQUE,"
                + Constant.Database.Chatroom.CHATROOM + " TEXT,"
                + Constant.Database.Chatroom.SENDER + " TEXT,"
                + Constant.Database.Chatroom.RECEIVER + " TEXT,"
                + Constant.Database.Chatroom.SENDER_UID + " TEXT,"
                + Constant.Database.Chatroom.RECEIVER_UID + " TEXT,"
                + Constant.Database.Chatroom.MSG + " TEXT,"
                + Constant.Database.Chatroom.MSG_TYPE + " TEXT,"
                + Constant.Database.Chatroom.TIMESTAMP + " TEXT,"
                + Constant.Database.Chatroom.READ_STATUS + " INTEGER DEFAULT 0"
                + ")";


        String CREATE_USER = "CREATE TABLE IF NOT EXISTS " + Constant.Database.TABLE_USER + "("
                + Constant.Database.User.UID + " VARCHAR PRIMARY KEY UNIQUE,"
                + Constant.Database.User.NAME + " TEXT,"
                + Constant.Database.User.PHONENUMBER + " TEXT,"
                + Constant.Database.User.PHOTO_URL + " TEXT,"
                + Constant.Database.User.IS_ME + " INTEGER DEFAULT 0,"
                + Constant.Database.User.FIREBASE_TOKEN + " TEXT,"
                + Constant.Database.User.USER_POINT + " INTEGER DEFAULT 0,"
                + Constant.Database.User.IS_MOD + " INTEGER DEFAULT 0"
                + ")";




        String CREATE_ROOM_LIST = "CREATE TABLE IF NOT EXISTS " + Constant.Database.TABLE_ROOM_LIST + "("
                + Constant.Database.RoomDetail.ROOM_ID + " VARCHAR PRIMARY KEY UNIQUE,"
                + Constant.Database.RoomDetail.NAME + " TEXT,"
                + Constant.Database.RoomDetail.PHOTO_URL + " TEXT,"
                + Constant.Database.RoomDetail.REQUEST + " TEXT,"
                + Constant.Database.RoomDetail.NOTIFICATION_ON_OFF + " INTEGER DEFAULT 1"
                + ")";




        try {
            database.execSQL( CREATE_CHAT_ROOMS );
            database.execSQL( CREATE_USER );
            database.execSQL( CREATE_ROOM_LIST );
            database.close();
        } catch (Exception e) {
            Log.e(getClass().getName(), "Error Creating Table");
        }
    }








    public void addUser(User user){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Constant.Database.User.UID, user.getUid());
        values.put(Constant.Database.User.NAME, user.getName());
        values.put(Constant.Database.User.PHONENUMBER, user.getPhone());
        values.put(Constant.Database.User.PHOTO_URL, user.getPhotoUrl());
        values.put(Constant.Database.User.IS_ME, 0);
        values.put(Constant.Database.User.FIREBASE_TOKEN, user.getFirebaseToken());
        values.put(Constant.Database.User.IS_MOD, user.isMod());
        values.put(Constant.Database.User.USER_POINT, user.getUserpoint());

        db.insertWithOnConflict(Constant.Database.TABLE_USER, Constant.Database.User.UID , values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    public void addMe(User user){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Constant.Database.User.UID, user.getUid());
        values.put(Constant.Database.User.NAME, user.getName());
        values.put(Constant.Database.User.PHONENUMBER, user.getPhone());
        values.put(Constant.Database.User.PHOTO_URL, user.getPhotoUrl());
        values.put(Constant.Database.User.IS_ME, 1);
        values.put(Constant.Database.User.IS_MOD, user.isMod());
        values.put(Constant.Database.User.FIREBASE_TOKEN, user.getFirebaseToken());

        values.put(Constant.Database.User.USER_POINT, user.getUserpoint());



        db.insertWithOnConflict(Constant.Database.TABLE_USER, Constant.Database.User.UID , values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }










    public void addUnreadCount(String RoomID, int count){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(Constant.Database.RoomLogCount.ROOM_ID, RoomID);
        values.put(Constant.Database.RoomLogCount.UNREAD, count);


        db.insertOrThrow(Constant.Database.TABLE_CHATROOM_LOG, Constant.Database.RoomLogCount.ROOM_ID , values);
        db.close();
    }

    public void addMessage(String roomid, Chat chat, String idx, Integer status){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Constant.Database.Chatroom.INDEX, idx);
        values.put(Constant.Database.Chatroom.CHATROOM, roomid);
        values.put(Constant.Database.Chatroom.SENDER, chat.getSender());
        values.put(Constant.Database.Chatroom.SENDER_UID, chat.getSenderUid());
        values.put(Constant.Database.Chatroom.RECEIVER, chat.getReceiver());
        values.put(Constant.Database.Chatroom.RECEIVER_UID, chat.getReceiverUid());
        values.put(Constant.Database.Chatroom.MSG, chat.getMessage());
        values.put(Constant.Database.Chatroom.READ_STATUS, status);
        values.put(Constant.Database.Chatroom.TIMESTAMP, chat.getTimestamp());
        values.put(Constant.Database.Chatroom.MSG_TYPE, chat.getMessageType());


        db.insertWithOnConflict(Constant.Database.TABLE_CHAT_ROOMS, Constant.Database.Chatroom.INDEX , values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }



    public void addMessage(Chat chat, String idx){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Constant.Database.Chatroom.INDEX, idx);
        values.put(Constant.Database.Chatroom.CHATROOM, Compare.getRoomName(chat.getSenderUid(), chat.getReceiverUid()));
        values.put(Constant.Database.Chatroom.SENDER, chat.getSender());
        values.put(Constant.Database.Chatroom.SENDER_UID, chat.getSenderUid());
        values.put(Constant.Database.Chatroom.RECEIVER, chat.getReceiver());
        values.put(Constant.Database.Chatroom.RECEIVER_UID, chat.getReceiverUid());
        values.put(Constant.Database.Chatroom.MSG, chat.getMessage());
        values.put(Constant.Database.Chatroom.READ_STATUS, chat.getReadStatus());
        values.put(Constant.Database.Chatroom.TIMESTAMP, chat.getTimestamp());
        values.put(Constant.Database.Chatroom.MSG_TYPE, chat.getMessageType());


        db.insertWithOnConflict(Constant.Database.TABLE_CHAT_ROOMS, Constant.Database.Chatroom.INDEX , values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }








    public User getMe(){

        User user = new User();



        String selectAll = "SELECT * FROM "+ Constant.Database.TABLE_USER + " WHERE "+ Constant.Database.User.IS_ME + " = 1";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectAll, null);




        try {
            if (cursor.moveToFirst()) {
                do {

                    user.setUid(cursor.getString(0));
                    user.setName(cursor.getString(1));
                    user.setPhone(cursor.getString(2));
                    user.setPhotoUrl(cursor.getString(3));
                    user.setFirebaseToken(cursor.getString(5));
                    user.setUserpoint(cursor.getInt(6));
                    user.setMod(cursor.getInt(7));


                } while (cursor.moveToNext());
            }
        } catch (Exception e){
            Log.d(TAG, "nullpointer exception");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }



        return user;

    }



    public ChatRoom getRoombyName(String Name){

        ChatRoom chatRoom = new ChatRoom();

        String selection = Constant.Database.RoomDetail.NAME+" = ? ";
        String[] selectionArgs = new String[] {Name};

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(Constant.Database.TABLE_ROOM_LIST, null,  selection, selectionArgs, null,  null, null, null);

        try {
            if (cursor.moveToFirst()) {
                do {
                    String roomid = cursor.getString(0);
                    String name = cursor.getString(1);
                    String photourl = cursor.getString(2);
                    int requeststatus = cursor.getInt(3);

                    chatRoom = new ChatRoom(roomid,name,photourl,requeststatus);




                } while ( cursor.moveToNext());
            }
        } catch (Exception e){
            Log.d(TAG, "chatroom nullpointer exception");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
                db.close();
            }
        }

        return chatRoom;

    }



    public User getUser( String uid){

        User user = new User();

        String selection = Constant.Database.User.UID+" = ? ";
        String[] selectionArgs = new String[] {uid};


        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(Constant.Database.TABLE_USER, null,  selection, selectionArgs, null,  null, null, null);




        try {
            if (cursor.moveToFirst()) {
                do {

                    user.setUid(cursor.getString(0));
                    user.setName(cursor.getString(1));
                    user.setPhone(cursor.getString(2));
                    user.setPhotoUrl(cursor.getString(3));
                    user.setFirebaseToken(cursor.getString(5));

                    user.setUserpoint(cursor.getInt(6));
                    user.setMod(cursor.getInt(7));

                } while (cursor.moveToNext());
            }
        } catch (Exception e){
            Log.d(TAG, "nullpointer exception");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }



        return user;

    }
    public User getUserbynumber( String number){

        User user = new User();

        String selection = Constant.Database.User.PHONENUMBER+" = like? ";
        String[] selectionArgs = new String[] {'%' + number + '%'};


        SQLiteDatabase db = this.getWritableDatabase();
    Cursor cursor=    db.rawQuery("SELECT * FROM users WHERE phonenumber LIKE ?", selectionArgs);
     //   Cursor cursor = db.query(Constant.Database.TABLE_USER, null,  selection, selectionArgs, null,  null, null, null);




        try {
            if (cursor.moveToFirst()) {
                do {

                    user.setUid(cursor.getString(0));
                    user.setName(cursor.getString(1));
                    user.setPhone(cursor.getString(2));
                    user.setPhotoUrl(cursor.getString(3));
                    user.setFirebaseToken(cursor.getString(5));

                } while (cursor.moveToNext());
            }
        } catch (Exception e){
            Log.d(TAG, "nullpointer exception");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }



        return user;

    }













    public ArrayList<User> getAllUser(){

        ArrayList<User> alluser = new ArrayList<>();



        String selectAll = "SELECT * FROM "+ Constant.Database.TABLE_USER + " WHERE "+ Constant.Database.User.IS_ME + " = 0";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectAll, null);




        try {
            if (cursor.moveToFirst()) {
                do {
                    User user = new User();
                    user.setUid(cursor.getString(0));
                    user.setName(cursor.getString(1));
                    user.setPhone(cursor.getString(2));
                    user.setPhotoUrl(cursor.getString(3));
                    user.setFirebaseToken(cursor.getString(5));
                    user.setUserpoint(cursor.getInt(6));

                    user.setMod(cursor.getInt(7));

                    alluser.add(user);

                } while (cursor.moveToNext());
            }
        } catch (Exception e){
            Log.d(TAG, "nullpointer exception");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }



        return alluser;

    }


    public ArrayList<User> getTopten(){

        ArrayList<User> alluser = new ArrayList<>();



        String selectAll = "SELECT * FROM "+ Constant.Database.TABLE_USER;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectAll, null);




        try {
            if (cursor.moveToFirst()) {
                do {
                    User user = new User();
                    user.setUid(cursor.getString(0));
                    user.setName(cursor.getString(1));
                    user.setPhone(cursor.getString(2));
                    user.setPhotoUrl(cursor.getString(3));
                    user.setFirebaseToken(cursor.getString(5));
                    user.setUserpoint(cursor.getInt(6));

                    user.setMod(cursor.getInt(7));

                    alluser.add(user);

                } while (cursor.moveToNext());
            }
        } catch (Exception e){
            Log.d(TAG, "nullpointer exception");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        Collections.sort(alluser,compareUserLevel);




        return alluser;

    }




    public List<String> getAllnumber(){

        ArrayList<User> alluser = new ArrayList<>();

        List<String>numbers=new ArrayList<>();

        String selectAll = "SELECT * FROM "+ Constant.Database.TABLE_USER + " WHERE "+ Constant.Database.User.IS_ME + " = 0";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectAll, null);




        try {
            if (cursor.moveToFirst()) {
                do {
                    String nUmber=cursor.getString(2);
                    numbers.add(nUmber.substring(nUmber.length()-10));



                } while (cursor.moveToNext());
            }
        } catch (Exception e){
            Log.d(TAG, "nullpointer exception");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }



        return numbers;

    }


    public void updateMsg(String chatRoomID, String chatId, Integer status ){



        String where = Constant.Database.Chatroom.CHATROOM+" = ? AND " + Constant.Database.Chatroom.INDEX+ " = ? ";
        String[] whereArgs = new String[] {chatRoomID,chatId};

        ContentValues values = new ContentValues();
        values.put(Constant.Database.Chatroom.READ_STATUS, status);

        SQLiteDatabase db = this.getWritableDatabase();
        db.update(Constant.Database.TABLE_CHAT_ROOMS, values, where, whereArgs);

        db.close();


    }


    public Chat getMsg(String chatRoomID, String senderUid, String timeStamp ){

        Chat chat = new Chat();

        String selection = Constant.Database.Chatroom.CHATROOM+" = ? AND " + Constant.Database.Chatroom.SENDER_UID+ " = ? AND " + Constant.Database.Chatroom.TIMESTAMP+ " = ? ";
        String[] selectionArgs = new String[] {chatRoomID,senderUid,timeStamp};



        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(Constant.Database.TABLE_CHAT_ROOMS, null,  selection, selectionArgs, null,  null, null, null);


        try{
            if(cursor.moveToFirst()){
                do{
                    chat = new Chat();
                    chat.setChatId(cursor.getString(0));
                    chat.setSender(cursor.getString(2));
                    chat.setReceiver(cursor.getString(3));
                    chat.setSenderUid(cursor.getString(4));
                    chat.setReceiverUid(cursor.getString(5));
                    chat.setMessage(cursor.getString(6));
                    chat.setTimestamp(Long.parseLong(cursor.getString(7)));
                    chat.setReadStatus(cursor.getInt(8));

                }while (cursor.moveToNext());
            }
        }catch (Exception e){
            Log.d(TAG,"No Chat found");
        }finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }






        return chat;
    }











    public ArrayList<Chat> getAllMsg(String senderUid, String receiverUid){
        String chatRoomID = Compare.getRoomName(senderUid,receiverUid);
        ArrayList<Chat> allChat = new ArrayList<>();


        String selection = Constant.Database.Chatroom.CHATROOM + " = ? ";
        String[] selectionArgs = new String[] {chatRoomID};

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(Constant.Database.TABLE_CHAT_ROOMS, null,  selection, selectionArgs, null,  null, null, null);





        try {
            if (cursor.moveToFirst()) {
                do {
                    Chat chat = new Chat();

                    /*

                    Log.d(TAG, "Sender name : " + cursor.getString(3));
                    Log.d(TAG, "receiver name : " + cursor.getString(2));
                    Log.d(TAG, "Sender uid : " + cursor.getString(5));
                    Log.d(TAG, "receiver uid : " + cursor.getString(4));
                    Log.d(TAG, "message : " + cursor.getString(6));
                    Log.d(TAG, "time : " + cursor.getString(8));
                    Log.d(TAG, "readstatus : " + cursor.getString(9));
                    Log.d(TAG, "type : " + cursor.getString(7));

                    */

                    chat.setChatId(cursor.getString(0));
                    chat.setSender(cursor.getString(2));
                    chat.setReceiver(cursor.getString(3));
                    chat.setSenderUid(cursor.getString(4));
                    chat.setReceiverUid(cursor.getString(5));
                    chat.setMessage(cursor.getString(6));
                    chat.setTimestamp(Long.parseLong(cursor.getString(8)));
                    chat.setReadStatus(Integer.parseInt(cursor.getString(9)) );
                    chat.setMessageType(cursor.getString(7));

                    allChat.add(chat);

                } while (cursor.moveToNext());
            }
        } catch (Exception e){
            Log.d(TAG, "nullpointer exception");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }





        return allChat;
    }




    public Chat getLastMsg(String chatRoomID){
        Chat chat = new Chat();
        String selection = Constant.Database.Chatroom.CHATROOM + " = ? ";
        String[] selectionArgs = new String[] {chatRoomID};
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(Constant.Database.TABLE_CHAT_ROOMS, null,  selection, selectionArgs, null,  null, null, null);

        if(cursor.getCount()==0){
            chat = null;
        }

        try {
            if (cursor.moveToLast()) {
                do {



                    chat.setChatId(cursor.getString(0));
                    chat.setSender(cursor.getString(2));
                    chat.setReceiver(cursor.getString(3));
                    chat.setSenderUid(cursor.getString(4));
                    chat.setReceiverUid(cursor.getString(5));
                    chat.setMessage(cursor.getString(6));
                    chat.setTimestamp(Long.parseLong(cursor.getString(8)));
                    chat.setReadStatus(Integer.parseInt(cursor.getString(9)) );

                } while (cursor.moveToNext());
            }
        } catch (Exception e){
            Log.d(TAG, "nullpointer exception");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return chat;
    }



    public Chat getMsg(String chatRoomID, String idx){
        Chat chat = new Chat();

        String selection = Constant.Database.Chatroom.CHATROOM + " = ? AND " + Constant.Database.Chatroom.INDEX + " = ?" ;
        String[] selectionArgs = {chatRoomID,idx};
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.query( Constant.Database.TABLE_CHAT_ROOMS, null,  selection, selectionArgs, null,  null, null, null);


        try{
            if(cursor.moveToFirst()){
                do{
                    chat = new Chat();
                    chat.setChatId(cursor.getString(0));
                    chat.setSender(cursor.getString(2));
                    chat.setReceiver(cursor.getString(3));
                    chat.setSenderUid(cursor.getString(4));
                    chat.setReceiverUid(cursor.getString(5));
                    chat.setMessage(cursor.getString(6));
                    chat.setTimestamp(Long.parseLong(cursor.getString(8)));
                    chat.setReadStatus(cursor.getInt(9));

                }while (cursor.moveToNext());
            }
        }catch (Exception e){
            Log.d(TAG,"No Chat found");
        }finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }




        return chat;
    }










    public String getUnreadMsgCount(String chatRoomID, String senderUid){

        String selection = Constant.Database.Chatroom.CHATROOM + " = ? AND "
                +Constant.Database.Chatroom.SENDER_UID + " != ? AND "
                +Constant.Database.Chatroom.MSG_TYPE + " != ? AND "
                +Constant.Database.Chatroom.READ_STATUS+" = ? ";

        String[] selectionArgs = new String[] {chatRoomID,senderUid, "system","0"};

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(Constant.Database.TABLE_CHAT_ROOMS, null,  selection, selectionArgs, null,  null, null, null);

        int count = 0;

        try{
            count = cursor.getCount();
        }catch (Exception e){
            Log.d(TAG,"No Chat found");
        }finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
                db.close();
            }
        }


        return count+"";
    }




























    public void addRoom(String roomId, String name, String photoUrl, int requestStatus){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Constant.Database.RoomDetail.ROOM_ID, roomId);
        values.put(Constant.Database.RoomDetail.NAME, name);
        values.put(Constant.Database.RoomDetail.PHOTO_URL, photoUrl);
        values.put(Constant.Database.RoomDetail.REQUEST, requestStatus);

        db.insertWithOnConflict(Constant.Database.TABLE_ROOM_LIST, Constant.Database.RoomDetail.ROOM_ID , values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }


    public void updateNotificationStateOfRoom(String chatRoomID, int status){



        String where = Constant.Database.RoomDetail.ROOM_ID+" = ? ";
        String[] whereArgs = new String[] {chatRoomID};

        ContentValues values = new ContentValues();
        values.put(Constant.Database.RoomDetail.NOTIFICATION_ON_OFF, status);

        SQLiteDatabase db = this.getWritableDatabase();
        db.update(Constant.Database.TABLE_ROOM_LIST, values, where, whereArgs);

        db.close();

    }




    public int roomNotificationState( String chatRoomID ){

        String selection = Constant.Database.RoomDetail.ROOM_ID+" = ? ";
        String[] selectionArgs = new String[] {chatRoomID};

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(Constant.Database.TABLE_ROOM_LIST, null,  selection, selectionArgs, null,  null, null, null);

        int state = 1;

        try{

            if(cursor.moveToFirst()){
                state = cursor.getInt(4);
            }



        } catch (Exception e){
            Log.d(TAG, "chatroom nullpointer exception");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
                db.close();
            }
        }




        return state;
    }


    public ArrayList<ChatRoom> getAllRoom(){

        ArrayList<ChatRoom> allChatrooms = new ArrayList<>();

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(Constant.Database.TABLE_ROOM_LIST, null,  null, null, null,  null, null, null);

        try {
            if (cursor.moveToFirst()) {
                do {
                    String roomid = cursor.getString(0);
                    String name = cursor.getString(1);
                    String photourl = cursor.getString(2);
                    int requeststatus = cursor.getInt(3);


                    /*

                    Log.d(TAG, "name : " + cursor.getString(1));
                    Log.d(TAG, "room id : " + cursor.getString(0));
                    Log.d(TAG, "photo : " + cursor.getString(2));
                    Log.d(TAG, "type : " + cursor.getString(3));

                    */

                    ChatRoom c = new ChatRoom(roomid,name,photourl,requeststatus);
                    allChatrooms.add(c);



                } while ( cursor.moveToNext());
            }
        } catch (Exception e){
            Log.d(TAG, "chatroom nullpointer exception");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
                db.close();
            }
        }

        return allChatrooms;

    }

    public ArrayList<ChatRoom> getAllRoombyStatus(int status){

        ArrayList<ChatRoom> allChatrooms = new ArrayList<>();

        String selection = Constant.Database.RoomDetail.REQUEST+" = ? ";
        String[] selectionArgs = new String[] {status+""};

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(Constant.Database.TABLE_ROOM_LIST, null,  selection, selectionArgs, null,  null, null, null);

        try {
            if (cursor.moveToFirst()) {
                do {
                    String roomid = cursor.getString(0);
                    String name = cursor.getString(1);
                    String photourl = cursor.getString(2);
                    int requeststatus = cursor.getInt(3);


                    /*

                    Log.d(TAG, "name : " + cursor.getString(1));
                    Log.d(TAG, "room id : " + cursor.getString(0));
                    Log.d(TAG, "photo : " + cursor.getString(2));
                    Log.d(TAG, "type : " + cursor.getString(3));

                    */

                    ChatRoom c = new ChatRoom(roomid,name,photourl,requeststatus);
                    allChatrooms.add(c);




                } while ( cursor.moveToNext());
            }
        } catch (Exception e){
            Log.d(TAG, "chatroom nullpointer exception");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
                db.close();
            }
        }

        return allChatrooms;

    }


    public ChatRoom getRoom(String chatRoomID){

        ChatRoom chatRoom = new ChatRoom();

        String selection = Constant.Database.RoomDetail.ROOM_ID+" = ? ";
        String[] selectionArgs = new String[] {chatRoomID};

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(Constant.Database.TABLE_ROOM_LIST, null,  selection, selectionArgs, null,  null, null, null);

        try {
            if (cursor.moveToFirst()) {
                do {
                    String roomid = cursor.getString(0);
                    String name = cursor.getString(1);
                    String photourl = cursor.getString(2);
                    int requeststatus = cursor.getInt(3);


                    /*

                    Log.d(TAG, "name : " + cursor.getString(1));
                    Log.d(TAG, "room id : " + cursor.getString(0));
                    Log.d(TAG, "photo : " + cursor.getString(2));
                    Log.d(TAG, "type : " + cursor.getString(3));

                    */

                    chatRoom = new ChatRoom(roomid,name,photourl,requeststatus);




                } while ( cursor.moveToNext());
            }
        } catch (Exception e){
            Log.d(TAG, "chatroom nullpointer exception");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
                db.close();
            }
        }

        return chatRoom;

    }

    public Boolean ifRoomexist(String chatRoomID){

        ChatRoom chatRoom = new ChatRoom();

        String selection = Constant.Database.RoomDetail.ROOM_ID+" = ? ";
        String[] selectionArgs = new String[] {chatRoomID};

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(Constant.Database.TABLE_ROOM_LIST, null,  selection, selectionArgs, null,  null, null, null);

        try {
            if (cursor.moveToFirst()) {
                return true;

            }
        } catch (Exception e){
            Log.d(TAG, "chatroom nullpointer exception");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
                db.close();
            }
        }

        return false;

    }









    private boolean checkDB(){
        String path = DB_PATH + DB_NAME;
        File file = new File(path);
        if(file.exists()){
            return true;
        }
        return false;
    }




    public synchronized void close(){
        if(this.database != null){
            this.database.close();
        }
    }








    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+Constant.Database.TABLE_CHAT_ROOMS);
        onCreate(db);
    }

    public static Comparator<User> compareUserLevel =
            new Comparator<User>() {
                public int compare(User other, User user1) {
                    return Long.compare(user1.getUserpoint(),other.getUserpoint());
                }
            };



}
