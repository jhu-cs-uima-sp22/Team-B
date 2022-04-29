# Team-B
Team B's music recommendation project 


## SPRINT 1 DEBUGS
With Sprint 1 we were able to implement a lot of the basic set up that made up the framework of everything that we needed for sprint 2. But we still had some bug fixes that we needed to implement that carried through from Sprint 1 which can be seen in the backlog of code with the fix that properly orders the songs and information displayed. With the first sprint we had a problem where the API calls to fetch images, artists and titles did not line up with the actual call to spotify that starts and stops that actual music player that controls play/pause, volume etc. The reason was that these API calls were happening out of order sometimes depending on the time it took to call the different API’s as to get all the information and play the song took three separate API’s that called for different information. Now what we had originally was a call that basically talked to the player and then once that happened the other API’s were called and there was a mismatch of when information was returned to the app. However, in order to fix this we realized that all API calls must be used as feedback in order to perform them like async/wait functions so that we can be confident in the order everytime. This fixed the problem after refactoring the code so that no API call was outside of the one that initially sets everything up. However even with this fix the API is still sometimes buggy and there are doubles of the images which seems to just happen every 10 times you run the app and might just be a symptom of something on their end because our code was properly formatted.

## UI CHANGES
For the first sprint, there were many elements of the front end that were placeholders for the rest of the project. Examples include color choices, using words for buttons, not caring about the size of the buttons, and allowing long titles to take up screen space. Each of these elements had to be faced separately. The color choice was easy, given a great idea to move the accent color from purple (hard to see) to hot pink (easy to see, and makes a statement). The words for buttons meant having to move the Button to an ImageButton, which was easy enough, but added the problem of button sizing and backgrounds. We created a background of a circle so that the foreground could be the icon that demonstrates the use of the button. Then, ordering the buttons on the frontend to fit the sizing requirements meant adding some padding and inner-layering for layouts. Moving the title to a marquee was simple once we realized the word for “text moving left to right but staying where it is”. Altogether, the frontend design was simple in hindsight but difficult to figure out how to define the problems.

## SHARE 
We wanted to add some potential for social interaction between people, so we decided to implement a share button. This functions pretty much like any share button on any other app. For example, if you’re on TikTok and find a video you want to share to someone via text message: you would hit the share button, be prompted to select an app to share to, and then share it. Ours works the same way – you’re listening to a song, you hit the share button, you select an app to share to, and it will share the Spotify URL of the current song that is playing.

## LIKED SONGS
One of our goals for this sprint was to implement a feature where the user can “like” songs and view them later, since the purpose of our app is to discover new music. To do this we implemented an SQLite database that contains the song’s title, artist, album, the Spotify URI for the song, and the Spotify URI for the song’s album image. The primary key for the database is the Spotify URI, since by definition it is unique for every song. Whenever the user clicks the “like” button from the song view, we look up that song’s URI in the database. If it is not found, it gets added, and if it is found it gets removed, since a user clicking “like” on a previously liked song signals that they are trying to “unlike” it. The backend of the liked songs is fully implemented, and the “like” button works as intended, however we did not have time to finish the frontend of the list of liked songs view.

## GENRE
One of the most important features of our app is the ability for users to choose the genre of music to listen to. To implement this functionality, we had to first fetch all the personalized and recommended genres from the Spotify API. After receiving the data, we will use it to populate the dropDown menu for genres. When a user selects a genre to listen to, we will fetch all songs from the spotify API, update the UI and start playing the playlist. By default, the “recently played songs” is selected when the app is launched.

## BUGS AND FUTURE IMPLEMENTATIONS 
Some bugs we now have in our code are very similar to what we had previously. While we made the bug fixes from the last sprint that allowed us to move forward to this one with the implementation of the Genre this means more API calls to get the both Genres as well as repopulating. Now it becomes impossible to call everything asynchronous or at least we attempted to put everything into one long chained feedback statement but we were not able to because many of the songs are still out of order. However, that is not the only issue that we see. When switching genres because of the issue that the API calls take time to complete it repopulates the data in very strange ways because the calls are no longer waiting for them to complete the stack is being popped in a random order after all local code is finished running. So we get strange orderings. Also, because we are now switching between genres the app gets confused when you switch because the API calls that should happen in the middle of the switch happen at the end and don’t repopulate everything full so you get lists that are shorter. This all sounds like a lot of bugs but honestly the code itself is solid. The ordering of everything is entirely correct if we just wrap every api in a Java version of async/wait function then we will have everything working harmoniously. But for right now the songs still are slightly out of order and the app runs strange because of this. If as a grader you boot up the app and there is some strange activity it is caused by the fact that the API isn’t great combined with the fact that the API calls are not asynchronous and waiting for completion. Simply run the app again and it should take more than once or twice for the app to run properly and it typically does have everything fully functioning on the first go.

For future implementation we want to implement infinite scrolling. Currently it's limited to 10-13 songs that you can scroll through but one possible solution is storing in the SQLite database where we are in a specific playlist and then updating that as we go so that when we scroll back we know how to repopulate these same local 10 songs that we had seen previously. However there are other solutions that we need to look into for infinite scrolling. We also need to implement the liked songs view which should be a simple population of information from the song information that is stored in the SQLite database in some sort of Recycler view and is a relatively quick fix, the harder part was the liking of the songs and setting up the database which as discussed above has been completed. This was supposed to be a sprint 2 implementation but because of all the bug fixes from sprint 1 and then the issues working with the API for genre stuff this sprint we felt like we needed to prioritize other features over the list and just have the liked songs functionality and the mostly working genre implementation. Those are the major two fixes that we want for the immediate future. For the long term future we want to implement a progress bar for each song so that you can jump around as well as the major implementation of a backend algorithm to select songs for the user based on what they like. This is the biggest implementation that we would get done for Sprints far in the future. That would really bump this project from a cool implementation for a new spotify feature to something that is legitimately helpful and marketable.

# HOW TO TEST INSTRUCTIONS:
There are a few things that must be completed in order for the emulator to properly run the code. Firstly, the branch that is most up to date is ‘shuffle’, so we recommend running that. Although both shuttle and master have a slight bug as discussed above that moves around the song order with respect to the actual song information. It is the same bug but is much more prominent in shuffle. The first step is to download and open the zipped project.
The set up can be split into two separate parts, the part that can be done locally on your browser of choice, and the steps that must be completed in the emulator itself.
Desktop (aka Authentication):

The first step to gaining authentication is to log into the spotify developer dashboard on your desktop (https://developer.spotify.com/dashboard/login),
Log in to the developer dashboard with the following information:

**Username:** sirsammage@gmail.com

**Password:** eeshleeberdeesh86

There will be a single project already created title “Disco”, open that and go into the project settings by clicking ‘edit settings’
Scroll down to the section labeled Android packages, you will notice that there are already three packages set up for the app which correspond to our personal computers, please do not remove these. Instead you will be adding your computer.
For the top input please just copy and paste from the other computers by inputting ‘com.example.disco’
Now to get the fingerprint (which is the bottom string), open up whatever command line you happen to have on your computer and input the following command.

**For bash like shells:**
keytool -alias androiddebugkey -keystore ~/.android/debug.keystore -list -v

**For windows like shells:**
keytool -alias androiddebugkey -keystore %HOMEPATH%\.android\debug.keystore -list -v

You will be prompted for a password, input the password ‘android’ and you should receive your fingerprints. Choose the fingerprint encrypted by SHA1 which should be next to a string ‘SHA1:’
Copy and past that fingerprint into the developer, then click ‘add’ and don’t forget to scroll to the bottom and click save.
That concludes the desktop portion of the set up.

### Emulator (aka Spotify Access):
The api information is already in the code but it needs the Spotify app to be on whatever device is running the Disco app
Check if your emulator has the ‘Google Play Store’, if it does not create one that does, the recommendation from us is to create a Pixel 4 emulator.
The way to tell if the emulator you are about to create does have the store is to go to ‘tools > device manager’, select ‘create device’ and make sure there is a small symbol in the column labeled ‘Play Store’.

Now that you have an emulator that also has the Google Play Store, open the store and search for Spotify.
Download spotify and log in with the same information that you used to log in to the developer desktop

**Username:** sirsammage@gmail.com

**Password:** eeshleeberdeesh86

This should be the end for the emulator portion of the set up and Disco should run smoothly when you run it in this emulator.