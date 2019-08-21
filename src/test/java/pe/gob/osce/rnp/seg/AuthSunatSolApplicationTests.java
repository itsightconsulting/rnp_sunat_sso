package pe.gob.osce.rnp.seg;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import pe.gob.osce.rnp.seg.cfg.WebMvcConfiguration;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {WebMvcConfiguration.class})
@SpringBootTest
public class AuthSunatSolApplicationTests {

    @Test
    public void contextLoads() {
    }

}

