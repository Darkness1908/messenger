package com.relex.messenger;

import org.junit.jupiter.api.Test;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;
import org.springframework.boot.test.context.SpringBootTest;

@SelectPackages("com.relex.messenger")
@Suite
@SpringBootTest
class MessengerApplicationTests {

	@Test
	void contextLoads() {
	}

}
