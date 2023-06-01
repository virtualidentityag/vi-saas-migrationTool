package com.vi.migrationtool.tenantservice;

import com.vi.migrationtool.common.MigrationTasks;
import com.vi.migrationtool.config.BeanAwareSpringLiquibase;
import liquibase.database.Database;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.jdbc.core.JdbcTemplate;

@Slf4j
public class ImportDiocesesFromAgencyToTenantServiceTask extends MigrationTasks {

    private static final String CREATE_DIACOSE_TABLE = "CREATE TABLE IF NOT EXISTS diocese (id BIGINT NOT NULL AUTO_INCREMENT, name VARCHAR(255) NOT NULL, PRIMARY KEY (id) UNIQUE (name));";

    @Data
    static class Diacose {
        private String name;
    }

    @Override
    public void execute(Database database) {
        log.info("Starting import of dioceses from agency to tenant");
        val agencyJdbcTemplate = BeanAwareSpringLiquibase.getNamedBean("agencyServiceJdbcTemplate", JdbcTemplate.class);

        val dioceseTableExistsQuery = "select count(*) from information_schema.tables where table_name = ? and table_schema = 'agencyservice';";
        val result = agencyJdbcTemplate.queryForObject(dioceseTableExistsQuery, Integer.class, "diocese");

        boolean dioceseTableDoesNotExist = result == null || result == 0;
        if (dioceseTableDoesNotExist) {
            log.info("Diocese table does not exist in agency service, skipping import");
            return;
        }

        val diacosen = agencyJdbcTemplate.query("select name from diocese", (rs, rowNum) -> {
            val diacose = new Diacose();
            diacose.setName(rs.getString("name"));
            return diacose;
        });

        log.info("The following diacosens will be migrated: {}", diacosen);

        var tenantServiceJdbcTemplate = BeanAwareSpringLiquibase.getNamedBean("tenantServiceJdbcTemplate", JdbcTemplate.class);

        assertDiacosTableCreatedInTenantService(tenantServiceJdbcTemplate);

        val batchUpdateResult = tenantServiceJdbcTemplate.batchUpdate("insert ignore into diocese (name) values (?)", diacosen, 1000, (ps, diacose) ->
                ps.setString(1, diacose.getName()));

        log.info("Inserted {} rows into diacosen table", batchUpdateResult.length);
        log.info("Finished import of dioceses from agency to tenant");
    }

    private static void assertDiacosTableCreatedInTenantService(JdbcTemplate tenantServiceJdbcTemplate) {
        val numAffected = tenantServiceJdbcTemplate.update(CREATE_DIACOSE_TABLE);
        log.info("Created diacosen table with {} rows affected", numAffected);
    }
}
