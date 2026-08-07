package com.board;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * JSP 를 쓰기 위해 war 로 패키징하지만, 실행은 내장 톰캣이 그대로 맡는다.
 * ./gradlew bootRun 또는 java -jar build/libs/*.war 로 띄운다.
 */
@SpringBootApplication
public class BoardApplication {

	public static void main(String[] args) {
		SpringApplication.run(BoardApplication.class, args);
	}

}
