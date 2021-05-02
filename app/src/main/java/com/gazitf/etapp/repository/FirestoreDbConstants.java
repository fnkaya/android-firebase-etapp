package com.gazitf.etapp.repository;

/*
 * @created 23/03/2021 - 6:37 PM
 * @project EtApp
 * @author fnkaya
 */
public abstract class FirestoreDbConstants {

    public static abstract class CategoryConstants {
        public static final String COLLECTION = "Categories";
        public static final String NAME = "name";
        public static final String DESCRIPTION = "description";
        public static final String IMAGE_URL = "imageUrl";
    }

    public static abstract class ActivitiesConstants {
        public static final String COLLECTION = "Activities";
        public static final String DOCUMENT_ID = "documentId";
        public static final String OWNER_ID = "ownerId";
        public static final String START_DATE = "startDate";
        public static final String ACTIVITY_NAME = "name";
    }

    public static abstract class FavoritesConstants {
        public static final String COLLECTION = "Favorites";
        public static final String FAVORITE_LIST = "favoriteList";
    }

    public static abstract class UsersConstants {
        public static final String COLLECTION = "Users";
        public static final String DISPLAY_NAME = "displayName";
        public static final String PHOTO_URL = "photoUrl";
    }

    public static abstract class AttendeeConstants {
        public static final String COLLECTION = "Attendees";

    }

    public static abstract class RequestConstants {
        public static final String COLLECTION = "Requests";
        public static final String ACTIVITY_ID = "activityId";
        public static final String ACTIVITY_OWNER_ID = "activityOwnerId";
        public static final String OWNER_ID = "requestOwnerId";
        public static final String OWNER_NAME = "requestOwnerName";
        public static final String REQUEST_DATE = "requestDate";
        public static final String REQUEST_MESSAGE = "requestMessage";
    }
}
