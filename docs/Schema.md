# Firebase Schema

The application follows the following schema format:

```
{
    Bookmarks: {
        "firebase uid": {
            "session_id": true (boolean),
            "session_id": true        
        }
    },
    "Event": {
        "event_id": {
            "startdate": "yyyy-mm-dd",
            "enddate": "yyyy-mm-dd",
            "name": "Event name"
        }
    },
    Speakers: {
        "speaker_id": {
            "avatar_url": "url of the avatar",
            "company": "Company name",
            "description": "Speaker Description",
            "email": "email id of the speaker",
            "name": "name of the speaker",
            "role": "role of speaker in that company (example CEO)"
        }
    },
    "Tracks": {
        "track_id": {
            "color": "hex color code (example #000000)",
            "name": "Track name"
        }
    },
    "Sessions": {
        "session_id": {
            "description": "Description of the session",
            "starttime": "Time at which the session is going to start (should be in Unix timestamp format)",
            "endtime": "Time at which the session is going to end (should be in Unix timestamp format)",
            "location": "Location of the session (example Hall A, Hall B)",
            "name": "Name of the session",
            "sessiondate": "yyyy-mm-dd",
            "sessiontype": "Type of the session",
            "speakers": [
                {
                    "id": "id of the speaker from the speaker node",
                    //rest of the details to be copied in the exact same format from the respective speaker node.
                },
                {
                    "id": "id of the second speaker",
                    //rest of the details to be copied in the exact same format from the respective speaker node.
                }
            ],
            "tracks": [
                {
                    "id": "id of the track",
                    //rest of the details to be copied in the exact same format from the respective track node.
                }
            ]
        }
    },
    "Users": {
        "firebase uid": {
            "attendeeType" : "Attendee/Volunteer/Student",
            "company": "Company name",
            "email": "email-id of the attendee",
            "fname": "First name of the attendee",
            "lname": "Last name of the attendee",
            "title": "Title of the attendee (Example CEO)"
        }
    }
}
```

Let's understand what each node stores the data about:

1. **Bookmarks:** This node stores the bookmarks stored by the user while using the application to enable sync across devices.
                  Bookmarks are only stored for registered users and are not stored for non-registered users.
                  
2. **Event**: This node stores the data of a single event at a given time. Currently, the application is limited to only single event at a time and hence you should store the data of only single event at a given time.

3. **Speakers**: This node stores the data related to the speakers of the event. An event can have multiple speakers.

4. **Tracks**: This node stores the data related to the tracks of the event. An event can have multiple tracks.

5. **Sessions**: This node stores the data related to the sessions of the event. An event can have multiple sessions.

6. **Users**: This node stores the data related to the users who have registered for the event. It's optional for the user to register for the event.