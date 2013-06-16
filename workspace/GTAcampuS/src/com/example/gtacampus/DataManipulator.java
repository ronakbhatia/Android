package com.example.gtacampus;


import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DataManipulator {
	private static final  String DATABASE_NAME = "mydb.db";
	private static final int DATABASE_VERSION = 1;
	static final String TABLE_NAME1 = "campus";
	static final String TABLE_NAME2 = "notes";
	static final String TABLE_NAME3 = "alarms";
	static final String TABLE_NAME4 = "courses";
	private static Context context;
	OpenHelper openHelper;
	ContentValues alarm;
	static SQLiteDatabase db;

	public DataManipulator(Context context) {
		DataManipulator.context = context;
		openHelper = new OpenHelper(DataManipulator.context);
		DataManipulator.db = openHelper.getWritableDatabase();
	}
	
	
	public void insert(String course,String code,String teacher) {
		ContentValues cval = new ContentValues();
		cval.put("course", course);
		cval.put("code", code);
		cval.put("bunk", 0);
		cval.put("teacher", teacher);
		db.insert(TABLE_NAME1, null, cval);
	}
	
	
	public void close()
	{
		openHelper.close();
		db.close();
	}
	public void insertnote(String title, String note)
	{
		db.execSQL("insert into "+TABLE_NAME2+" (title,notes) values ('" + title + "','" + note + "')");
	}
	public void update(String course)
	{
		db.execSQL("UPDATE "+TABLE_NAME1 + " SET bunk=bunk+1 WHERE course = '" + course +"'");
	}
	
	public int getbunk(String course)
	{
		Cursor bunkval= db.query(TABLE_NAME1, new String[]{"bunk"}, "course = ?", new String[]{course}, null, null, null);
		bunkval.moveToFirst();
		return bunkval.getInt(0);
	}

	public void deleteAll() {
		db.delete(TABLE_NAME1, null, null);
	}

	public List<String[]> selectAll()
	{

		List<String[]> list = new ArrayList<String[]>();
		Cursor cursor = db.query(TABLE_NAME1, new String[] { "id", "course","code","bunk"},
				null, null, null, null, null); 

		int x=0;
		if (cursor.moveToFirst()) {
			do {
				String[] b1=new String[]{cursor.getString(0),cursor.getString(1),cursor.getString(2),cursor.getString(3)};

				list.add(b1);

				x=x+1;
			} while (cursor.moveToNext());
		}
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		} 
		cursor.close();

		return list;
	}

	
	public List<String[]> selectAllnotes()
	{
		List<String[]> list = new ArrayList<String[]>();
		Cursor cursor=db.query(TABLE_NAME2, new String[] {"id","title","notes"}, null, null, null, null, null);
		if(cursor.moveToFirst())
		{
		do
		{
			String[] b = new String[]{cursor.getString(0),cursor.getString(1),cursor.getString(2)};
			list.add(b);
		}while(cursor.moveToNext());
		
		}
		cursor.close(); 
		return list;
		
				}
	
	public void coursetableinit(int column)
	{
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME4);
		int i;
		String columnstring;
		columnstring="DAY_ID INTEGER PRIMARY KEY AUTOINCREMENT, ";
		for(i=1;i<column;i++)
		{
			columnstring=columnstring+ "HOUR" + i + " TEXT, ";
		}
		columnstring = columnstring + "HOUR" + i + " TEXT";
		db.execSQL("CREATE TABLE " + TABLE_NAME4 + " (" + columnstring + " )");
	}

	public void alarmsave(Intent intent)
	{
		alarm = new ContentValues();
		alarm = putvalues(intent,alarm);
		db.insert(TABLE_NAME3, null, alarm);
	}
	
	public void alarmupdate(Intent intent,int alarmid)
	{
		alarm = new ContentValues();
		alarm = putvalues(intent,alarm);
		db.update(TABLE_NAME3, alarm, String.format("id=%d", alarmid), null);
	}
	
	public void disablealarm(int alarmid){
		db.execSQL("UPDATE " + TABLE_NAME3 + " SET status=0 WHERE id=" + alarmid);
	}
	
	public ContentValues putvalues(Intent intent, ContentValues alarm){
		alarm.put("hour",intent.getIntExtra("hour", Calendar.HOUR_OF_DAY));
		alarm.put("minute",intent.getIntExtra("minute", Calendar.MINUTE));
		alarm.put("title", intent.getStringExtra("alarmtitle"));
		alarm.put("type", intent.getStringExtra("type"));
		alarm.put("status", intent.getBooleanExtra("alarmstatus",true)?1:0);
		alarm.put("snooze", intent.getIntExtra("snoozetime", 5));
		alarm.put("shakemode", intent.getBooleanExtra("shake_mode", true)?1:0);
		alarm.put("mathsolver", intent.getBooleanExtra("mathsolver", false)?1:0);
			alarm.put("sun", intent.getBooleanExtra("sun", false)?1:0);
			alarm.put("mon", intent.getBooleanExtra("mon", false)?1:0);
			alarm.put("tue", intent.getBooleanExtra("tue", false)?1:0);
			alarm.put("wed", intent.getBooleanExtra("wed", false)?1:0);
			alarm.put("thu", intent.getBooleanExtra("thu", false)?1:0);
			alarm.put("fri", intent.getBooleanExtra("fri", false)?1:0);
			alarm.put("sat", intent.getBooleanExtra("sat", false)?1:0);
			alarm.put("day", intent.getIntExtra("day", Calendar.DAY_OF_MONTH));
			alarm.put("month", intent.getIntExtra("month", Calendar.MONTH));
			alarm.put("year", intent.getIntExtra("year", Calendar.YEAR));
			return alarm;
	}
	
	
	public Cursor fetchalarms()
	{
		return db.query(TABLE_NAME3, null, null, null, null, null, null);
	}
	
	public void addcourse(int day_id,ContentValues val)
	{
		db.update(TABLE_NAME4, val, "DAY_ID="+day_id, null);
	}
	
	public Cursor fetchenabledalarms()
	{
		return db.query(TABLE_NAME3, null, "status=?", new String[]{"1"}, null, null, null);}
	
	public void coursetimings(String[] array,int size)
	{
		ContentValues values = new ContentValues();
		int i=0;
		for(i=1;i<=size;i++)
		{
			values.put("HOUR"+i, array[i]);
		}
		db.insert(TABLE_NAME4, null, values);
	}
	
	public void initdays(int size)
	{
		ContentValues courses = new ContentValues();
		for(int i=1;i<=size;i++)
		{
			courses.put("HOUR"+i, "-");
		}
		for(int i=0;i<5;i++)
		db.insert(TABLE_NAME4, null, courses);
	}
	
	public Cursor gethourtimings()
	{
		return db.query(TABLE_NAME4, null, "DAY_ID=1", null, null, null, null);
	}
	
	public ContentValues get_time(int hourno)
	{
		ContentValues time = new ContentValues();
		Cursor timings = gethourtimings();
		timings.moveToFirst();
		String t = timings.getString(hourno);
		String[] t_split = t.split(":");
		time.put("hour", Integer.parseInt(t_split[0]));
		String[] tmin_split = t_split[1].split(" ");
		time.put("minute", Integer.parseInt(tmin_split[0]));
		time.put("am_pm", (tmin_split[1].equals("AM"))?0:1);
		return time;
	}
	
	public Cursor slotstat()
	{
		return db.query(TABLE_NAME4, null, "DAY_ID > 2", null, null,null , null);
	}
	
	public String[] gettimings()
	{
		Cursor time = db.query(TABLE_NAME4, null, null, null, null, null, null, "2");
		int i = time.getColumnCount()-1;
		String[] timings = new String[i];
		int j;
		
		for(j=0;j<i;j++)
		{
			time.moveToFirst();
			timings[j]=time.getString(j+1);
			time.moveToNext();
			timings[j]=timings[j]+" - " + time.getString(j+1);
		}
		time.close();
		return timings;
	}
	
	public void deletenote(String note){
		db.delete(TABLE_NAME2, String.format("%s=?","notes"), new String[]{note});
	}

	public void deletealarm(int alarmid){
		db.delete(TABLE_NAME3, String.format("id=%d", alarmid),null);
	}

	private static class OpenHelper extends SQLiteOpenHelper {

		OpenHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE " + TABLE_NAME1 + " (id INTEGER PRIMARY KEY, course TEXT, code TEXT,bunk INTEGER,teacher TEXT)");
			db.execSQL("CREATE TABLE " + TABLE_NAME2 + " (id INTEGER PRIMARY KEY, title TEXT, notes TEXT)");
			db.execSQL("CREATE TABLE " + TABLE_NAME3 + " (id INTEGER PRIMARY KEY AUTOINCREMENT,year INTEGER, month INTEGER, day INTEGER, hour INTEGER, minute INTEGER, title TEXT, type TEXT, status INTEGER, snooze INTEGER, shakemode INTEGER, mathsolver INTEGER, sun INTEGER,mon INTEGER,tue INTEGER, wed INTEGER,thu INTEGER, fri INTEGER,sat INTEGER)");
		}												//0										1				2				3			4				5			6			7			8				9				10					11

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME1);
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME2);
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME3);
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME4);
			onCreate(db);
		}
	}



}
