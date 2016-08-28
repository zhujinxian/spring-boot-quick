/*
 * *****************************************************
 * Copyright (C) 2016 iQIYI.COM - All Rights Reserved
 * This file is part of iQiyi Pay project.
 * Unauthorized copy of this file, via any medium is strictly prohibited.
 * Proprietary and Confidential.
 * ****************************************************
 */
package com.zhucode.web.quick.aspect;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.hibernate.validator.internal.engine.ValidatorImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import com.zhucode.web.quick.annotation.Para;
import com.zhucode.web.quick.exception.ParamErrorexception;

/**
 * @author zhu jinxian <zhujinxian@qiyi.com>
 *
 * @date 2016年8月12日
 */
@Aspect
@Component
public class ParameterValidAspect {

	@Autowired
	private Validator validator;

	@Autowired
	HttpServletResponse response;

	@Around("@annotation(org.springframework.web.bind.annotation.RequestMapping)")
	public Object valid(ProceedingJoinPoint point) throws Throwable {
		MethodSignature ms = (MethodSignature) point.getSignature();
		Method m = ms.getMethod();
		Parameter[] parameters = m.getParameters();
		Object[] args = point.getArgs();
		
		LocalValidatorFactoryBean validatorFactoryBean = (LocalValidatorFactoryBean) validator;
		ValidatorImpl validatorImpl = (ValidatorImpl) validatorFactoryBean
				.getValidator();
		Class<?>[] groups = new Class<?>[0];

		Set<ConstraintViolation<Object>> violations = validatorImpl
				.validateParameters(point.getTarget(), m, args, groups);
		if (violations.isEmpty()) {
			return point.proceed();
		}

		ConstraintViolation<Object> violation = violations.iterator().next();
		String pathString = violation.getPropertyPath().toString();
		String subPath = pathString.split("\\.")[1];
		Para para = null;
		for (Parameter p : parameters) {
			if (subPath.equals(p.getName())) {
				para = p.getAnnotation(Para.class);
				break;
			}
		}
		
		if (para != null) {
			para = AnnotationUtils.getAnnotation(para, Para.class);
			throw new ParamErrorexception(para.name(), violation.getMessage());
		} else {
			return point.proceed();
		}
	}

}
