package net.trenkmann.dev.config;

import javax.sql.DataSource;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FlywayConfiguration implements FlywayMigrationStrategy {

  @Autowired
  @Qualifier("sourceDatasource")
  private DataSource sourceDatasource;

  @Autowired
  @Qualifier("target1Datasource")
  private DataSource target1Datasource;

  @Autowired
  @Qualifier("target2Datasource")
  private DataSource target2Datasource;

  @Override
  public void migrate(Flyway flyway1) {
    // target1
    Flyway flyway =
        Flyway.configure()
            .baselineOnMigrate(true)
            .dataSource(target1Datasource)
            .locations("db/batch/migration/target1")
            .load();
    flyway.migrate();
    // target2
    flyway =
        Flyway.configure()
            .baselineOnMigrate(true)
            .dataSource(target2Datasource)
            .locations("db/batch/migration/target2")
            .load();
    flyway.migrate();

    // source
    flyway =
        Flyway.configure()
            .baselineOnMigrate(true)
            .dataSource(sourceDatasource)
            .locations("db/batch/migration/source")
            .load();
    flyway.migrate();
  }
}
