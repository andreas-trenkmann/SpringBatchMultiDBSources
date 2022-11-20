package net.trenkmann.dev;


import net.trenkmann.dev.config.SourceDataConfiguration;
import net.trenkmann.dev.config.Target1DataConfiguration;
import net.trenkmann.dev.config.Target2DataConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes= {Target1DataConfiguration.class, SourceDataConfiguration.class, Target2DataConfiguration.class})
@TestPropertySource(locations="classpath:environment.properties")
public class ContextTestIT {


  @Test
  public void contextLoads() {
    /* Test for correct loaded context */
  }
}
