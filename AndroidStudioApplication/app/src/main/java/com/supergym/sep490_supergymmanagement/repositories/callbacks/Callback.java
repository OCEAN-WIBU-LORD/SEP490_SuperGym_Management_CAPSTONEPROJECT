package com.supergym.sep490_supergymmanagement.repositories.callbacks;

import java.util.List;

public interface Callback<T> {
    void onCallback(List<T> objects);
}