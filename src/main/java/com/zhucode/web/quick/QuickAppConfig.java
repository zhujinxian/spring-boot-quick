/**
The MIT License (MIT)
Copyright (c) <2015> <author or authors>
Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/
package com.zhucode.web.quick;

import java.util.List;

import javax.validation.Validator;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.zhucode.web.quick.argresolver.ParaHandlerMethodArgumentResolver;
import com.zhucode.web.quick.interceptor.ValidatorHandlerInterceptor;

/**
 * @author zhu jinxian
 *
 * @date 2016年7月18日
 */
@Configuration
public class QuickAppConfig extends WebMvcConfigurerAdapter {
	@Bean 
	Validator validator() {
		return new LocalValidatorFactoryBean();
	}
	
	@Bean
	public ValidatorHandlerInterceptor validatorHandlerInterceptor() {
	    return new ValidatorHandlerInterceptor();
	}

	@Bean
	ParaHandlerMethodArgumentResolver paraHandlerMethodArgumentResolver() {
		return new ParaHandlerMethodArgumentResolver();
	}
	
	@Override
	public void addArgumentResolvers(
			List<HandlerMethodArgumentResolver> argumentResolvers) {
		argumentResolvers.add(new ParaHandlerMethodArgumentResolver());
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(validatorHandlerInterceptor());
	}
}
