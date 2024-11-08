package sopro.service;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import sopro.TrintelApplication;

@Service
public class ExportImportService implements ExportImportInterface {

    private final JdbcTemplate jdbc;

    @Autowired
    public ExportImportService(JdbcTemplate jdbcTemplate) {
        this.jdbc = jdbcTemplate;
    }

    /**
     * Dumps database to sql file.
     *
     * @return dumpfile path
     */
    public String export() {
        File dump = new File(TrintelApplication.EXPORT_PATH + "/trintelExport.sql");
        if (dump.exists()) {
            dump.delete();
        }

        this.jdbc.execute("script to '" + dump.getPath() + "'");
        // this.jdbc.execute("mysqldump > '" + dump.getPath() + "'");
        return dump.getPath();
    }

    /**
     * Imports sql file into database.
     * Note: DB should idealy be empty before running this.
     *
     * @param sql file
     * @return isSuccess
     */
    public boolean importSQL(String pathToScript) {
        this.jdbc.execute("DROP ALL OBJECTS");
        this.jdbc.execute("runscript from '" + pathToScript + "'");
        return true;
    }
}
