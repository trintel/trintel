package sopro.service;

public interface ExportImportInterface {
    public String export();
    public boolean importSQL(String path);
}
