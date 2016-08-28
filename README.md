# spring-boot-quick
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

```java

@Configuration
@ComponentScan("com.zhucode.web.quick")
public class Appconfig {
	
}


```