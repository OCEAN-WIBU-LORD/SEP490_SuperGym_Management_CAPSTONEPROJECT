package com.supergym.sep490_supergymmanagement;

import com.supergym.sep490_supergymmanagement.models.Set;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SessionDataHolder {
    // Static Map to hold sets for each exercise by exerciseId
    public static Map<String, List<Set>> exerciseSetsMap = new HashMap<>();
}
