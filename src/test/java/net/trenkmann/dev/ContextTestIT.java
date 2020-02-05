package net.trenkmann.dev;


import net.trenkmann.dev.config.SourceDataConfiguration;
import net.trenkmann.dev.config.Target1DataConfiguration;
import net.trenkmann.dev.config.Target2DataConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes= {Target1DataConfiguration.class, SourceDataConfiguration.class, Target2DataConfiguration.class})
@TestPropertySource(locations="classpath:environment.properties")
public class ContextTestIT {


  @Test
  public void contextLoads() {
    /* Test for correct loaded context */
  }
}
