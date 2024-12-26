package org.Fusion.Main;

import java.util.ArrayList;
import java.util.List;

public class ErrorHandler {

    // List to store errors
    private final List<String> errorLog = new ArrayList<>();

    // Handle and store the error message
    public void handleError(String message) {
        System.err.println("Error: " + message);
        errorLog.add(message);  // Store error in the log
    }

    // Retrieve all stored errors
    public List<String> getAllErrors() {
        return new ArrayList<>(errorLog);  // Return a copy to avoid external modification
    }

    // Optionally, retrieve a specific error by index
    public String getError(int index) {
        if (index >= 0 && index < errorLog.size()) {
            return errorLog.get(index);
        } else {
            return "Error index out of bounds";
        }
    }
    public void clearErrors() {
        errorLog.clear();
    }

}
