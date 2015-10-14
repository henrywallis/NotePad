/*
 * Copyright (c) 2015 Jonas Kalderstam.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.nononsenseapps.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;

import com.nononsenseapps.notepad.R;
import com.nononsenseapps.notepad.database.TaskList;

/**
 * Simple utility class to hold some general functions.
 */
public class ListHelper {
    /**
     * If temp list is > 0, returns it. Else, checks if a default list is set
     * then returns that. If none set, then returns first (alphabetical) list
     * Returns -1 if no lists in database.
     * <p/>
     * Guarantees default list is valid if you are unsure. (e.g. if you input garbage,
     * non-garbage will hopefully flow out)
     */
    public static long getARealList(final Context context, final long tempList) {
        long returnList = tempList;

        if (returnList == -1) {
            // Then check if a default list is specified
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            returnList = Long.parseLong(prefs.getString(context.getString(R.string
                    .pref_defaultlist), "-1"));
        }

        if (returnList > 0) {
            // See if it exists
            final Cursor c = context.getContentResolver().query(TaskList.URI, TaskList.Columns
                    .FIELDS, TaskList.Columns._ID + " IS ?", new String[]{Long.toString
                    (returnList)}, null);
            if (c != null) {
                if (c.moveToFirst()) {
                    returnList = c.getLong(0);
                } else {
                    returnList = -1;
                }
                c.close();
            }
        }

        if (returnList == -1) {
            // Fetch a valid list from database if previous attempts are invalid
            final Cursor c = context.getContentResolver().query(TaskList.URI, TaskList.Columns
                    .FIELDS, null, null, context.getResources().getString(R.string
                    .const_as_alphabetic, TaskList.Columns.TITLE));
            if (c != null) {
                if (c.moveToFirst()) {
                    returnList = c.getLong(0);
                }
                c.close();
            }
        }

        return returnList;
    }
}