/**
The MIT License (MIT)
Copyright (c) <2015> <author or authors>
Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/
package com.zhucode.web.quick.interceptor;

import java.io.IOException;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.hibernate.validator.internal.engine.ValidatorImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.alibaba.fastjson.JSONObject;
import com.zhucode.web.quick.annotation.Para;
import com.zhucode.web.quick.argresolver.ParaHandlerMethodArgumentResolver;
/**
 * 参数校验拦截器
 * 
 * @author zhu jinxian
 *
 * 2016年6月2日
 */
public class ValidatorHandlerInterceptor extends HandlerInterceptorAdapter {
	
	Logger logger = LoggerFactory.getLogger(ValidatorHandlerInterceptor.class);
	
	
	@Autowired
	Validator validator;
	
	@Autowired
	ParaHandlerMethodArgumentResolver resolver;

	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		LocalValidatorFactoryBean validatorFactoryBean = (LocalValidatorFactoryBean)validator;
		HandlerMethod method = (HandlerMethod)handler;
		MethodParameter[] parameters = method.getMethodParameters();
		Object[] parameterValues = new Object[parameters.length];
		ServletWebRequest webRequest = new ServletWebRequest(request, response);
		for (int i = 0; i < parameters.length; i++) {
			try {
				MethodParameter parameter = parameters[i];
				Object value = resolver.resolveArgument(parameter, null, webRequest, null);
				parameterValues[i] = value;
			} catch (TypeMismatchException e) {
				Para para = parameters[i].getParameterAnnotation(Para.class);
				writeError(response,  para.name(), e.getMessage());
				return false;
			}
		}
		ValidatorImpl validatorImpl = (ValidatorImpl) validatorFactoryBean.getValidator();
		Class<?>[] groups = new Class<?>[0];
		Set<ConstraintViolation<Object>> violations = validatorImpl.validateParameters(method.getBean(), method.getMethod(), parameterValues, groups);
		if (!violations.isEmpty()) {
			ConstraintViolation<Object> violation = violations.iterator().next();
			String pathString = violation.getPropertyPath().toString();
			int paraIdx = Integer.parseInt(pathString.split("\\.")[1].substring(3));
			Para para = parameters[paraIdx].getParameterAnnotation(Para.class);
			writeError(response, para.name(),  violation.getMessage());
			return false;
		}
		return true;
	}
	
	
	private void writeError(HttpServletResponse response, String paraname, String message) throws IOException {
		JSONObject json = new JSONObject();
		json.put("code", "ERR_PARAMETER");
		json.put("msg", "参数 `" + paraname + "` " + message);
		response.setContentType("application/json; charset=utf-8");
		logger.info(json.toJSONString());
		response.getWriter().println(json.toJSONString());
	}

}
