package com.gazitf.etapp.repository;

/*
 * @created 23/03/2021 - 6:37 PM
 * @project EtApp
 * @author fnkaya
 */
public abstract class DbConstants {

    public static abstract class Categories {
        public static final String COLLECTION = "Categories";
        public static final String NAME = "name";
        public static final String DESCRIPTION = "description";
        public static final String IMAGE_URL = "imageUrl";
    }

    public static abstract class Activities {
        public static final String COLLECTION = "Activities";
        public static final String DOCUMENT_ID = "documentId";
        public static final String OWNER_ID = "ownerId";
        public static final String START_DATE = "startDate";
        public static final String ACTIVITY_NAME = "name";
    }

    public static abstract class Favorites {
        public static final String COLLECTION = "Favorites";
        public static final String FAVORITE_LIST = "favoriteList";
    }

    public static abstract class Users {
        public static final String COLLECTION = "Users";
        public static final String DISPLAY_NAME = "displayName";
        public static final String PHOTO_URL = "photoUrl";
    }

    public static abstract class Attendees {
        public static final String COLLECTION = "Attendees";
        public static final String ATTENDEE_LIST = "attendeeList";

    }

    public static abstract class Requests {
        public static final String COLLECTION = "Requests";
        public static final String ACTIVITY_ID = "activityId";
        public static final String ACTIVITY_OWNER_ID = "activityOwnerId";
        public static final String OWNER_ID = "requestOwnerId";
        public static final String OWNER_NAME = "requestOwnerName";
        public static final String REQUEST_DATE = "requestDate";
        public static final String REQUEST_MESSAGE = "requestMessage";
        public static final String STATUS = "status";
        public static final String PENDING = "pending";
        public static final String ACCEPTED = "accepted";
        public static final String REJECTED = "rejected";
    }
}
