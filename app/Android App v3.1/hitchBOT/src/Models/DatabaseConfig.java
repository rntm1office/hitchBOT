package Models;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseConfig extends SQLiteOpenHelper{
	
	public static final String TABLE_HTTPPOSTQUEUE = "HttpPostQueue";
	public static final String COLUMN_POSTID = "_id";
	public static final String COLUMN_URI = "URI";
	public static final String COLUMN_UPLOAD_TO_SERVER = "UploadToServer";
	public static final String COLUMN_UPLOADTYPE = " UploadType";
	public static final String COLUMN_DATE = "DateCreated";
	public static final String COLUMN_HEADER = "PostHeaders";
	public static final String COLUMN_BODY = "PostBody";
	
	public static final String TABLE_FILEUPLOAD = "FileUpload";
	public static final String COLUMN_FILEID = "_id";
	public static final String COLUMN_FILEURI = "URI";
	public static final String COLUMN_UPLOADED_TO_SERVER = "UploadToServer";
	public static final String COLUMN_FILETYPE = " UploadType";
	public static final String COLUMN_DATECREATED = "DateCreated";
	
	private static final String DATABASE_NAME = "hitchBOT.db";
	private static final int DATABASE_VERSION = 3;
	
	//because writing to sqlite across threads sucks
	private static final ReadWriteLock rwLock = new ReentrantReadWriteLock(true);
	private static DatabaseConfig db;
	
	
	private static String TAG = "Database";
	
	public DatabaseConfig(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	 private static final String HTTPPOST_TABLE_CREATE = "CREATE TABLE "
		      + TABLE_HTTPPOSTQUEUE + "(" + COLUMN_POSTID
		      + " INTEGER PRIMARY KEY AUTOINCREMENT, " 
		      + COLUMN_URI + " TEXT," 
		      + COLUMN_UPLOAD_TO_SERVER + " INTEGER," 
		      + COLUMN_UPLOADTYPE + " INTEGER,"
		      +COLUMN_DATE +" TEXT,"
		      +COLUMN_HEADER + " TEXT,"
		      + COLUMN_BODY + " TEXT"+ ");";

	 private static final String FILEUPLOAD_TABLE_CREATE = "CREATE TABLE "
		      + TABLE_FILEUPLOAD + "(" + COLUMN_FILEID
		      + " INTEGER PRIMARY KEY AUTOINCREMENT, " 
		      + COLUMN_FILEURI + " TEXT," 
		      + COLUMN_UPLOADED_TO_SERVER+ " INTEGER," 
		      + COLUMN_FILETYPE+ " INTEGER,"
		      +COLUMN_DATECREATED +" TEXT"+");";
	 
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(HTTPPOST_TABLE_CREATE);
		db.execSQL(FILEUPLOAD_TABLE_CREATE);
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_HTTPPOSTQUEUE);	
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_FILEUPLOAD);		
        onCreate(db);		

	}
	
	
	public List<FileUploadDb> serverFileUploadQueue()
	{
		
		List<FileUploadDb> databasePosts = new ArrayList<FileUploadDb>();
		SQLiteDatabase database = null;
		try
		{
			beginReadLock();
			String queryString = "SELECT * FROM " + TABLE_FILEUPLOAD + " WHERE " + COLUMN_UPLOADED_TO_SERVER+ " = 0";
			SQLiteDatabase db = this.getReadableDatabase();
			Cursor cursor = db.rawQuery(queryString, null);
			if(cursor.moveToFirst())
			{
				do{
					FileUploadDb fileUpload = new FileUploadDb();
					fileUpload.setID(Integer.parseInt(cursor.getString(0)));
					fileUpload.setUri(cursor.getString(1));
					Log.i(TAG, cursor.getString(1));
					fileUpload.setUploadToServer(Integer.parseInt(cursor.getString(2)));
					fileUpload.setFileType((Integer.parseInt(cursor.getString(3))));
					fileUpload.setDateCreated(cursor.getString(4));
					
					databasePosts.add(fileUpload);
					
				}while(cursor.moveToNext());
				cursor.close();

			}
		}catch (Exception e)
		{
			
		}
		finally{
			if(database!= null)
			{
	               try
	                {
	           		database.close();
	                }
	                catch (Exception e) {}
			}
			endReadLock();
		}
		return databasePosts;
		
	}
	
	public List<HttpPostDb> serverPostUploadQueue()
	{
		
		List<HttpPostDb> databasePosts = new ArrayList<HttpPostDb>();
		SQLiteDatabase database = null;
		try
		{
			beginReadLock();
			String queryString = "SELECT * FROM " + TABLE_HTTPPOSTQUEUE + " WHERE " + COLUMN_UPLOAD_TO_SERVER+ " = 0";
			SQLiteDatabase db = this.getReadableDatabase();
			Cursor cursor = db.rawQuery(queryString, null);
			if(cursor.moveToFirst())
			{
				do{
					HttpPostDb htpD = new HttpPostDb();
					htpD.setID(Integer.parseInt(cursor.getString(0)));
					htpD.setUri(cursor.getString(1));
					htpD.setUploadToServer(Integer.parseInt(cursor.getString(2)));
					htpD.setUploadType((Integer.parseInt(cursor.getString(3))));
					htpD.setDateCreated(cursor.getString(4));
					htpD.setSerializedHeader(cursor.getString(5));
					htpD.setSerializedBody(cursor.getString(6));
					databasePosts.add(htpD);
					
				}while(cursor.moveToNext());
				cursor.close();

			}
		}catch (Exception e)
		{
			
		}
		finally{
			if(database!= null)
			{
	               try
	                {
	           		database.close();
	                }
	                catch (Exception e) {}
			}
			endReadLock();
		}
		return databasePosts;
		
	}
	
	public void addItemToQueue(HttpPostDb httpPost)
	{
		SQLiteDatabase database = null;

		try
		{
			beginWriteLock();
			database = this.getWritableDatabase();
			ContentValues newRecord = new ContentValues();
			
			newRecord.put(COLUMN_URI, httpPost.getUri());
			newRecord.put(COLUMN_UPLOAD_TO_SERVER, httpPost.getUploadToServer());
			newRecord.put(COLUMN_DATE, httpPost.getDateCreated());
			newRecord.put(COLUMN_UPLOADTYPE, httpPost.getUploadType());
			newRecord.put(COLUMN_HEADER, httpPost.getSerializedHeader());
			newRecord.put(COLUMN_BODY, httpPost.getSerializedBody());
			Log.i(TAG, httpPost.getSerializedBody());

			database.insert(TABLE_HTTPPOSTQUEUE, null, newRecord);
		}catch (Exception e)
		{
			
		}
		finally{
			if(database!= null)
			{
	               try
	                {
	                    database.close();
	                }
	                catch (Exception e) {}
			}
			endWriteLock();
		}
		
	}
	
	public void addItemToQueue(FileUploadDb fileUpload)
	{
		SQLiteDatabase database = null;

		try
		{
			beginWriteLock();
			database = this.getWritableDatabase();
			ContentValues newRecord = new ContentValues();
			
			newRecord.put(COLUMN_FILEURI, fileUpload.getUri());
			newRecord.put(COLUMN_UPLOADED_TO_SERVER, fileUpload.getUploadToServer());
			newRecord.put(COLUMN_DATECREATED, fileUpload.getDateCreated());
			newRecord.put(COLUMN_FILETYPE, fileUpload.getFileType());
			database.insert(TABLE_FILEUPLOAD, null, newRecord);
		}catch (Exception e)
		{
			
		}
		finally{
			if(database!= null)
			{
	               try
	                {
	                    database.close();
	                }
	                catch (Exception e) {}
			}
			endWriteLock();
		}
		
	}
	
	public void markAsUploadedToServer(HttpPostDb httpPost)
	{		
		SQLiteDatabase database = null;

		try
		{
			beginWriteLock();
			database = this.getWritableDatabase();
			database.delete(TABLE_HTTPPOSTQUEUE, COLUMN_POSTID + " = " + httpPost.getID(), null);
		}catch (Exception e)
		{
			
		}
		finally{
			if(database!= null)
			{
	               try
	                {
	                    database.close();
	                }
	                catch (Exception e) {}
			}
			endWriteLock();
		}
	}
	
	public void markAsUploadedToServer(FileUploadDb fileUpload)
	{		
		SQLiteDatabase database = null;

		try
		{
			beginWriteLock();
			database = this.getWritableDatabase();
			database.delete(TABLE_FILEUPLOAD, COLUMN_FILEID + " = " + fileUpload.getID(), null);
		}catch (Exception e)
		{
			
		}
		finally{
			if(database!= null)
			{
	               try
	                {
	                    database.close();
	                }
	                catch (Exception e) {}
			}
			endWriteLock();
		}
	}
	
    private static void beginReadLock()
    {
        rwLock.readLock().lock();
    }

    private static void endReadLock()
    {
        rwLock.readLock().unlock();
    }

    private static void beginWriteLock()
    {
        rwLock.writeLock().lock();
    }

    private static void endWriteLock()
    {
        rwLock.writeLock().unlock();
    }
    
    public static synchronized DatabaseConfig getHelper(Context context)
    {
        if (db == null)
            db = new DatabaseConfig(context);

        return db;
    }
    
	//For testing only
	public void launchMissles()
	{
		//dangerous use only in extreme circumstances
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_HTTPPOSTQUEUE, null, null);
		db.delete(TABLE_FILEUPLOAD, null, null);

		db.close();
	}
	
	//For testing only
		public void launchFileMissles()
		{
			//dangerous use only in extreme circumstances
			SQLiteDatabase db = this.getWritableDatabase();
			db.delete(TABLE_FILEUPLOAD, null, null);

			db.close();
		}
	
}
