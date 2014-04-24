moves-android-api-library
=========================

Here I'm just trying to make a library to deal with Moves API calls from android. Developers can use this library project to fetch data from Moves. 

Completed API Methods:
  + Authentication
  + Profile
  + Summary
  + Activities
  + Storyline
  + ... more will update soon

Remaining Pipelined Tasks:
  + Places
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

Disclaimer
----------
This API library project is using data from <a href="https://dev.moves-app.com/docs/api">Moves API</a> but this project is not endorsed by Moves. <a href="http://www.moves-app.com/">Moves</a> is a trademark of ProtoGeo Oy.

Copyright and License
---------------------
This library project is licenced under The MIT License (MIT)
<br>
<pre>

Copyright (c) 2014 Midhu

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
</pre>

Contact
-------
<i>Please feel free to use this library, and give your feedbacks to improve it. You can reach me at </i> midhunarmid@gmail.com
<br><br>--- Midhu

<br><br>
Read more about <a href="http://midhunarmid.github.io/moves-android-api-library/">Moves Android API Library Project</a>
<br><br>
