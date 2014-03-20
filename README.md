moves-android-api-library
=========================

Here I'm just trying to make a library to deal with Moves API calls from android. Developers can use this library project to fetch data from Moves. 

Completed API Methods:
  + Authentication
  + Profile
  + Summary
  + ... more will update soon

Remaining Pipelined Tasks:
  + Activities
  + Places
  + Storyline
  + Activity list
  + Notifications

Usage:
------
You can just use the static methods from the main API class 'MovesAPI' after calling MovesAPI.init(...) method.
All completed methods are well documented, so hope you will not face any difficulties while developing.
The source is open to all (will be always) and you are free to modify it in anyway, but please update it here too. ;)

Some sample usages of MovesAPI static methods are as follows :
  * <code>MovesAPI.authenticate(...);</code>
  * <code>MovesAPI.getAuthData();</code>
  * <code>MovesAPI.getProfile(...);</code>
  * <code>MovesAPI.getSummary_SingleDay(...);</code>
  * <code>MovesAPI.getSummary_SpecificWeek(...);</code>
  * <code>MovesAPI.getSummary_SpecificMonth(...);</code>
  * <code>MovesAPI.getSummary_WithinRange(...);</code>
  * <code>MovesAPI.getSummary_PastDays(...);</code>

You will get notified with success and failure responses at :
<code>

	private MovesHandler<<MovesData>> authDialogHandler = new MovesHandler<<MovesData>>() {
		@Override
		public void onSuccess(<MovesData> result) {
		
		}
		
		@Override
		public void onFailure(MovesStatus status, String message) {
		
		}
	};
	
</code>



<i>Please feel free to use this library, and give your feedbacks to improve it.</i>
