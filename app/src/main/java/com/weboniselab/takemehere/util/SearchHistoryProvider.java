package com.weboniselab.takemehere.util;

import android.content.SearchRecentSuggestionsProvider;

/**
 * Created by Jarvis on 04/09/16.
 */

public class SearchHistoryProvider extends SearchRecentSuggestionsProvider {

    public final static String AUTHORITY = "com.weboniselab.takemehere.util.SearchHistoryProvider";
    public final static int MODE = DATABASE_MODE_QUERIES;

    public SearchHistoryProvider() {
        setupSuggestions(AUTHORITY, MODE);
    }
}
