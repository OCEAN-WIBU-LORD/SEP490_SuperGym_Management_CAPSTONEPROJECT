package com.example.sep490_supergymmanagement.models.sep490_supergymmanagement.repositories.callbacks;

import java.util.List;

public interface Callback<T> {
    void onCallback(List<T> objects);
}