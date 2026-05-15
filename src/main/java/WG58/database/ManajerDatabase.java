package WG58.database;

public interface ManajerDatabase {
    boolean hubungkan();

    void putuskan();

    boolean simpanKeDB(Object data);
}
