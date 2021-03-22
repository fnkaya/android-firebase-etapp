package com.gazitf.etapp.main.repository;

import com.google.firebase.firestore.FirebaseFirestore;

/*
 * @created 22/03/2021 - 6:18 PM
 * @project EtApp
 * @author fnkaya
 */
public class FirestoreRepository {

    private FirebaseFirestore db;

    public FirestoreRepository() {
        db = FirebaseFirestore.getInstance();
    }
}
