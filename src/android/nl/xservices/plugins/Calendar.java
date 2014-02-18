package nl.xservices.plugins;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
<<<<<<< HEAD
import android.os.Build;
import android.util.Log;
import nl.xservices.plugins.accessor.AbstractCalendarAccessor;
import nl.xservices.plugins.accessor.CalendarProviderAccessor;
import nl.xservices.plugins.accessor.LegacyCalendarAccessor;
=======
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.CalendarContract.Events;
import android.util.Log;

>>>>>>> features
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;


public class Calendar extends CordovaPlugin {
<<<<<<< HEAD
  public static final String ACTION_CREATE_EVENT = "createEvent";
  public static final String ACTION_CREATE_EVENT_INTERACTIVELY = "createEventInteractively";
  public static final String ACTION_DELETE_EVENT = "deleteEvent";
  public static final String ACTION_FIND_EVENT = "findEvent";

  public static final Integer RESULT_CODE_CREATE = 0;

  private CallbackContext callback;

  private static final String LOG_TAG = AbstractCalendarAccessor.LOG_TAG;

  @Override
  public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
    callback = callbackContext;
    // TODO this plugin may work fine on 3.0 devices, but have not tested it yet, so to be sure:
    final boolean hasLimitedSupport = Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH;
    if (ACTION_CREATE_EVENT.equals(action)) {
      if (hasLimitedSupport) {
        // TODO investigate this option some day: http://stackoverflow.com/questions/3721963/how-to-add-calendar-events-in-android
        return createEventInteractively(args);
      } else {
        return createEvent(args);
      }
    } else if (ACTION_CREATE_EVENT_INTERACTIVELY.equals(action)) {
      return createEventInteractively(args);
    } else if (!hasLimitedSupport && ACTION_FIND_EVENT.equals(action)) {
      return findEvents(args);
    } else if (!hasLimitedSupport && ACTION_DELETE_EVENT.equals(action)) {
      return deleteEvent(args);
    }
    return false;
  }

  private boolean createEventInteractively(JSONArray args) throws JSONException {
    final JSONObject jsonFilter = args.getJSONObject(0);

    final Intent calIntent = new Intent(Intent.ACTION_EDIT)
        .setType("vnd.android.cursor.item/event")
        .putExtra("title", jsonFilter.optString("title"))
        .putExtra("eventLocation", jsonFilter.optString("location"))
        .putExtra("description", jsonFilter.optString("notes"))
        .putExtra("beginTime", jsonFilter.optLong("startTime"))
        .putExtra("endTime", jsonFilter.optLong("endTime"))
        .putExtra("allDay", AbstractCalendarAccessor.isAllDayEvent(new Date(jsonFilter.optLong("startTime")), new Date(jsonFilter.optLong("endTime"))));

    this.cordova.startActivityForResult(this, calIntent, RESULT_CODE_CREATE);
    return true;
  }

  private AbstractCalendarAccessor calendarAccessor;

  private AbstractCalendarAccessor getCalendarAccessor() {
    if (this.calendarAccessor == null) {
      // Note: currently LegacyCalendarAccessor is never used, see the TODO at the top of this class
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
        Log.d(LOG_TAG, "Initializing calendar plugin");
        this.calendarAccessor = new CalendarProviderAccessor(this.cordova);
      } else {
        Log.d(LOG_TAG, "Initializing legacy calendar plugin");
        this.calendarAccessor = new LegacyCalendarAccessor(this.cordova);
      }
    }
    return this.calendarAccessor;
  }

  private boolean deleteEvent(JSONArray args) {
    if (args.length() == 0) {
      System.err.println("Exception: No Arguments passed");
    } else {
      try {
        JSONObject jsonFilter = args.getJSONObject(0);
        boolean deleteResult = getCalendarAccessor().deleteEvent(
            null,
            jsonFilter.optLong("startTime"),
            jsonFilter.optLong("endTime"),
            jsonFilter.optString("title"),
            jsonFilter.optString("location"));
        PluginResult res = new PluginResult(PluginResult.Status.OK, deleteResult);
        res.setKeepCallback(true);
        callback.sendPluginResult(res);
        return true;
      } catch (JSONException e) {
        System.err.println("Exception: " + e.getMessage());
      }
    }
    return false;
  }

  private boolean findEvents(JSONArray args) {
    if (args.length() == 0) {
      System.err.println("Exception: No Arguments passed");
    }
    try {
      JSONObject jsonFilter = args.getJSONObject(0);
      JSONArray jsonEvents = getCalendarAccessor().findEvents(
          jsonFilter.optString("title"),
          jsonFilter.optString("location"),
          jsonFilter.optLong("startTime"),
          jsonFilter.optLong("endTime"));

      PluginResult res = new PluginResult(PluginResult.Status.OK, jsonEvents);
      res.setKeepCallback(true);
      callback.sendPluginResult(res);
      return true;

    } catch (JSONException e) {
      System.err.println("Exception: " + e.getMessage());
    }
    return false;
  }

  private boolean createEvent(JSONArray args) {
    try {
      JSONObject arg_object = args.getJSONObject(0);
      boolean status = getCalendarAccessor().createEvent(null, arg_object.getString("title"),
          arg_object.getLong("startTime"), arg_object.getLong("endTime"),
          arg_object.getString("notes"), arg_object.getString("location"));

      callback.success("" + status);
      return true;
    } catch (Exception e) {
      System.err.println("Exception: " + e.getMessage());
    }
    return false;
  }

  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == RESULT_CODE_CREATE) {
      if (resultCode == Activity.RESULT_OK || resultCode == Activity.RESULT_CANCELED) {
        callback.success();
=======
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
>>>>>>> features
      }
    } else {
      callback.error("Unable to add event (" + resultCode + ").");
    }
  }
}
