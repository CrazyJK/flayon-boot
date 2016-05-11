package jk.crazy.flay;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import jk.crazy.flay.FlayApplication;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = FlayApplication.class)
@WebAppConfiguration
public class FlayApplicationTests {

	@Test
	public void contextLoads() {
	}

}
