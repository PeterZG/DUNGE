package dungeonmania.response.models;

/**
 * DO NOT CHANGE THIS FILE
 */
public final class ItemResponse {
    private final String id;
    private final String type;

    public ItemResponse(String id, String type) {
        this.id = id;
        this.type = type;
    }

    public final String getType() {
        return type;
    }

    public final String getId() {
        return id;
    }
}
