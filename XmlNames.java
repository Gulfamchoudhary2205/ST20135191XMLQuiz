package server.data;

/**
 * Constants for the xml tag and attribute names
 */
public enum XmlNames {
    ID("id"),
    PLAYERS("players"),
    QUESTION("Question"),
    TEXT("text"),
    ANSWERS("answers"),
    ANSWER_ID("answerid");

    private final String name;

    XmlNames(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
