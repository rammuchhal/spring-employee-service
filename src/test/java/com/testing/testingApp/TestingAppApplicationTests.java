package com.testing.testingApp;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;


@Slf4j
class TestingAppApplicationTests {

	@BeforeEach
    void setUp(){
		log.info("setting up");
	}

	@AfterEach
	void tearDown(){
		log.info("tearing it down");
	}

	@BeforeAll
    static void setUpOnce(){
		log.info("setup once");
	}

	@AfterAll
    static void tearDownOnce(){
		log.info("tearing it once");
	}

	@Test
	@Disabled
	void contextLoads() {
		log.info("this is test1");
	}

	@Test
	void testTwo(){
		log.info("this is test two");
		Assertions.assertThatThrownBy(()->divideByTwo(4,0))
				.isInstanceOf(ArithmeticException.class)
				.hasMessage("Tried to divide by 0");
	}

	double divideByTwo(int a,int b){
		try{
			return a/b;
		} catch(ArithmeticException e) {
			log.error("this is error of arithmetic exception");
			throw new ArithmeticException("Tried to divide by 0");
		}
	}

	@DisplayName("Final Test")
	@Test
	void testThree(){
		log.info("this is final test");
	}

}
