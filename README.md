# spring-boot-quick

HelloController.java

```java

@RestController
@RequestMapping("/hello")
public class HelloController {
	
	@RequestMapping("hello")
	public String hello(@NotBlank @Para("x")String x, @Valid User user) {
		System.out.println(user.getName());
		return "{}";
	}
	
}


```

ErrorHandler.java

```java

@ResponseBody
@ControllerAdvice
public class ErrorHandler {
	
	@ExceptionHandler({ParamErrorexception.class})
	public String handlerError(ParamErrorexception ex) {
		return ex.getPara() + ex.getMessage();
	}
	
	@ExceptionHandler({BindException.class})
	public String handleError(BindException ex) {
		FieldError oe = (FieldError)ex.getAllErrors().get(0);
		return  oe.getObjectName() + oe.getField() + oe.getDefaultMessage();
	}
}

```

Appconfig.java

```java

@Configuration
@ComponentScan("com.zhucode.web.quick")
public class Appconfig {
	
}


```