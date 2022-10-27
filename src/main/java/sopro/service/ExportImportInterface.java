package sopro.service;

import java.io.File;

public interface ExportImportInterface {
    public String export();
    public boolean importSQL(File sql);
}
