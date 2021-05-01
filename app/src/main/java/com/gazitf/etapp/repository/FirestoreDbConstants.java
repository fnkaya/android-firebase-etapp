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
        public static final String PHOTO_URL = "photoUrl";
    }

    public static abstract class ActivitiesConstans {
        public static final String COLLECTION = "Activities";
        public static final String DOCUMENT_ID = "documentId";
        public static final String OWNER_ID = "ownerId";
        public static final String START_DATE = "startDate";
    }

    public static abstract class FavoritesConstans {
        public static final String COLLECTION = "Favorites";
        public static final String FAVORITE_LIST = "favoriteList";
    }

    public static abstract class UsersConstants {
        public static final String COLLECTION = "Users";
    }
}
