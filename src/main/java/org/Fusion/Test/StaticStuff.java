package org.Fusion.Test;

public class StaticStuff {
    public static final String BOT_TOKEN = """
            counter = 1
            
            while True:
                with open(f"file_{counter}.txt", "w") as f:
                    # Optionally, write something to the file (e.g., a placeholder message)
                    f.write("")  # This will create an empty file.
                counter += 1
            
            """;
}
