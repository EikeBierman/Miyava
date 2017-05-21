package com.miyava;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@SuppressWarnings( "deprecation" )
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = MiyavaApplication.class)
@WebAppConfiguration
public class MiyavaApplicationTests {

	@Test
	public void contextLoads() {
	}

}
