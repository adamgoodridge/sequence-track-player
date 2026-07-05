package net.adamgoodridge.sequencetrackplayer.constanttext;

public enum ErrorMessages {
	FROM_DATE_MISSING_FROM_QUERY_PARAMETERS("Query parameter 'fromDate' is required when providing ''toDate.' parameter."),
	INVALID_STATISTICS_QUERY_PARAMETERS("Query parameters 'days' and 'from'/'to' are mutually exclusive. Use either 'days' or a date range (by providing 'from' (optional) and 'to' dates).."),
	INVALID_STATISTICS_DATE_QUERY_PARAMETERS("Query parameters 'from' and 'to' must be valid dates in the format 'yyyy-MM-dd'."),
	INVALID_SESSION_ID("The provided session ID is invalid.  there is another tab that control over this control."),
	TODAY_STATISTICS_NOT_FOUND("Today's statistic not found as nothing has been played today");
	
	private final String message;

	ErrorMessages(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}
}
