package com.resiligence.callnow;

import android.Manifest.permission;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Айрат on 21.11.2017.
 */

public class ZoomUaUtility {
  public static class ZoomCallMetaInfo {
    public final String zoomMetingId;
    public final String calendarCallTitle;
    public final String calendarCallDescription;

    ZoomCallMetaInfo(String zoomMetingId, String calendarCallTitle, String calendarCallDescription) {
      this.zoomMetingId = zoomMetingId;
      this.calendarCallTitle = calendarCallTitle;
      this.calendarCallDescription = calendarCallDescription;
    }
  }

  public static class ZoomCallInfo extends ZoomCallMetaInfo {
    public boolean called = false;
    public ZoomCallInfo(ZoomCallMetaInfo parent){
      super(parent.zoomMetingId, parent.calendarCallTitle, parent.calendarCallDescription);
    }
  }

  public static ArrayList<ZoomCallMetaInfo> readCalendarEvent(Context context) {

    ArrayList<ZoomCallMetaInfo> callsMetas = new ArrayList<ZoomCallMetaInfo>();

    try {
      if (ActivityCompat.checkSelfPermission(context, permission.READ_CALENDAR)
          != PackageManager.PERMISSION_GRANTED) {
        return null;
      }
      Log.e("TAGTAG", "1");
      Calendar calendar = Calendar.getInstance();

      //current day it must be it
      calendar.setTimeInMillis(System.currentTimeMillis());
      //calendar.set(2017, Calendar.NOVEMBER, 21, 0, 0, 0);

      calendar.set(Calendar.HOUR_OF_DAY, 0);
      calendar.set(Calendar.MINUTE, 0);
      calendar.set(Calendar.SECOND, 0);
      long startDay = calendar.getTimeInMillis();

      //calendar.set(2017, Calendar.NOVEMBER, 21, 23, 59, 59);

      calendar.set(Calendar.HOUR_OF_DAY, 23);
      calendar.set(Calendar.MINUTE, 59);
      calendar.set(Calendar.SECOND, 59);
      long endDay = calendar.getTimeInMillis();

      String[] projection = new String[] { BaseColumns._ID, CalendarContract.Events.TITLE, CalendarContract.Events.DTSTART, Events.DESCRIPTION,  Events.EVENT_LOCATION };
      String selection = CalendarContract.Events.DTSTART + " >= ? AND " + CalendarContract.Events.DTSTART + "<= ?";
      String[] selectionArgs = new String[] { Long.toString(startDay), Long.toString(endDay) };

      Cursor cursor = context.getContentResolver().query(CalendarContract.Events.CONTENT_URI, projection, selection, selectionArgs, null);
      cursor.moveToFirst();
      // fetching calendars id
      for (int i = 0; i < cursor.getCount(); i++, cursor.moveToNext()) {
        String zoomMetingID = parseZoomMetingIDFromCalendarLocation(cursor.getString(4));

        if (zoomMetingID == null) {
          continue;
        }

        ZoomCallMetaInfo newZoomCallMetaInfo = new ZoomCallMetaInfo(zoomMetingID, cursor.getString(1), cursor.getString(2));
        Log.e("TAGTAG", cursor.getString(4) + "  " +  cursor.getString(1));

        callsMetas.add(newZoomCallMetaInfo);
      }
    } catch (Throwable e) {
      Log.e("TAGTAG", e.toString());
    }
    return callsMetas;
  }

  private static String parseZoomMetingIDFromCalendarLocation(String calendarLocation) {
    //parse string like https://zoom.us/j/xxxxxxx
    if (calendarLocation == null)
      return null;

    String zoomPattern = "zoom.us/j/";
    int zoomPatternIndex = calendarLocation.indexOf(zoomPattern);

    if (zoomPatternIndex == -1)
      return null;

    String zoomMitingID = calendarLocation.substring(zoomPatternIndex+zoomPattern.length(), calendarLocation.length());

    return zoomMitingID;
  }

  public static String getDate(long milliSeconds) {
    SimpleDateFormat formatter = new SimpleDateFormat(
        "dd/MM/yyyy hh:mm:ss a");
    Calendar calendar = Calendar.getInstance();
    calendar.setTimeInMillis(milliSeconds);
    return formatter.format(calendar.getTime());
  }

  static ArrayList<ZoomCallInfo> fromMetta(ArrayList<ZoomCallMetaInfo> source){
    ArrayList<ZoomCallInfo> result = new ArrayList<ZoomCallInfo>(0);
    for (ZoomCallMetaInfo zoomCallMetaInfo : source) {
      result.add(new ZoomCallInfo(zoomCallMetaInfo));
    }
    return result;
  }
}
