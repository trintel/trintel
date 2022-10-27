package sopro.service;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import sopro.TrintelApplication;

@Service
public class ExportImportService implements ExportImportInterface {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ExportImportService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
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

        this.jdbcTemplate.execute("script to '" + dump.getPath() + "'");
        return dump.getPath();
    }

    public boolean importSQL(File sql) {
        return false;
    }

}
