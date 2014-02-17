package nl.xservices.plugins;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.CalendarContract.Events;
import android.util.Log;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.Date;


public class Calendar extends CordovaPlugin {
	public static final String ACTION_CREATE_EVENT = "createEvent";
	public static final String ACTION_DELETE_EVENT = "deleteEvent";
	public static final String ACTION_FIND_EVENT   = "findEvent";
	public static final String ACTION_MODIFY_EVENT = "modifyEvent";
	public static final String ACTION_LIST_EVENTS_IN_RANGE = "listEventsInRange";


	public static final Integer RESULT_CODE_CREATE = 0;
	private CallbackContext callback;
	
	@Override
	public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
		try {
			if (ACTION_CREATE_EVENT.equals(action)) {
        callback = callbackContext;

				final Intent calIntent = new Intent(Intent.ACTION_EDIT)
            .setType("vnd.android.cursor.item/event")
            .putExtra("title", args.getString(0))
            .putExtra("eventLocation", args.getString(1))
            .putExtra("description", args.getString(2))
            .putExtra("beginTime", args.getLong(3))
            .putExtra("endTime", args.getLong(4))
            .putExtra("allDay", isAllDayEvent(new Date(args.getLong(3)), new Date(args.getLong(4))));

				this.cordova.startActivityForResult(this, calIntent, RESULT_CODE_CREATE);
				return true;
			} else if(ACTION_LIST_EVENTS_IN_RANGE.equals(action)){
				Uri l_eventUri;
			    if (Build.VERSION.SDK_INT >= 8) {
			        l_eventUri = Uri.parse("content://com.android.calendar/events");
			    } else {
			        l_eventUri = Uri.parse("content://calendar/events");
			    }
			    ContentResolver contentResolver = this.cordova.getActivity().getContentResolver();

			    long input_start_date = args.getLong(0);
			    long input_end_date = args.getLong(1);
			    
			    Log.d("calendar", "raw start "+input_start_date);
			    Log.d("calendar", "raw end "+input_end_date);
			    //prepare start date
			    java.util.Calendar calendar_start = java.util.Calendar.getInstance();			    
			    Date date_start = new Date(input_start_date);
			    calendar_start.setTime(date_start);			    

			    //prepare end date
			    java.util.Calendar calendar_end = java.util.Calendar.getInstance();
			    Date date_end = new Date(input_end_date);
			    calendar_end.setTime(date_end);
			    
			    //projection of DB columns
			    String[] l_projection = new String[] { "title", "dtstart", "dtend", "eventLocation" };	    
			    
			    //actual query
			    Cursor cursor= contentResolver.query(l_eventUri, l_projection, "( dtstart >" + calendar_start.getTimeInMillis() + " AND dtend <" + calendar_end.getTimeInMillis() + ")", null,
			            "dtstart ASC");
			    
			    Log.d("calendar", "cursor count: "+cursor.getCount());
			    Log.d("calendar", "Input start: "+date_start.getTime());
			    Log.d("calendar", "Input end: "+date_end.getTime());
			    while(cursor.moveToNext()){
			    	Date start = new Date(cursor.getLong(1));
			    	Date end = new Date(cursor.getLong(2));
			    	Log.d("calendar", "title: "+cursor.getString(0));
			    	Log.d("calendar", "start time: "+start.getTime());
			    	Log.d("calendar", "end time: "+end.getTime());
			    	Log.d("calendar", "location: "+cursor.getString(3));
			    	Log.d("calendar","------------------------------");
			    }
				return true;
			} else  {
        callbackContext.error("calendar." + action + " is not (yet) supported on Android.");
        return false;
      }
		} catch(Exception e) {
			System.err.println("Exception: " + e.getMessage());
			return false;
		}     
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == RESULT_CODE_CREATE) {
      // Hmm, resultCode and requestCode are always 0.. so this doesn't help
			if (resultCode == Activity.RESULT_OK) {
				callback.success();
			} else if (resultCode == Activity.RESULT_CANCELED) {
        callback.error("User cancelled");
			} else {
				callback.error("Unable to add event (" + resultCode + ").");
			}
    }
	}

  private boolean isAllDayEvent(final Date startDate, final Date endDate) {
    return startDate.getHours() == 0 &&
        startDate.getMinutes() == 0 &&
        startDate.getSeconds() == 0 &&
        endDate.getHours() == 0 &&
        endDate.getMinutes() == 0 &&
        endDate.getSeconds() == 0;
  }
}
