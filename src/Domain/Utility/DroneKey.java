package Domain.Utility;

public class DroneKey {
    private final int id;
    private final int groupId;

    public DroneKey(int id, int groupId) {
        this.id = id;
        this.groupId = groupId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DroneKey)) return false;
        DroneKey other = (DroneKey) o;
        return id == other.id && groupId == other.groupId;
    }

    @Override
    public int hashCode() {
        return 31 * id + groupId;
    }
}

