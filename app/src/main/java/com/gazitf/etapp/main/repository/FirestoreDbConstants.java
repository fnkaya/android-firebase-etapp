package com.gazitf.etapp.main.repository;

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
        public static final String START_DATE = "startDate";
    }
}
