/**
The MIT License (MIT)
Copyright (c) <2015> <author or authors>
Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/
package com.zhucode.web.quick.argresolver;

import org.springframework.beans.SimpleTypeConverter;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.annotation.ValueConstants;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.zhucode.web.quick.annotation.Para;



/**
 * 识别@Para注明的参数
 * 
 * @author zhu jinxian
 *
 * 2016年6月3日
 */
public class ParaHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {

	SimpleTypeConverter converter = new SimpleTypeConverter();
	
	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return parameter.getParameterAnnotation(Para.class) != null;
	}

	@Override
	public Object resolveArgument(MethodParameter parameter,
			ModelAndViewContainer mavContainer, NativeWebRequest webRequest,
			WebDataBinderFactory binderFactory) throws Exception {
		Para p = parameter.getParameterAnnotation(Para.class);
		if (p == null) {
			return null;
		}
		Object val = webRequest.getParameter(p.name());
		if (!p.required()) {
			if (val == null) {
				val = p.defaultValue();
			}
		}
		if (ValueConstants.DEFAULT_NONE.equals(val)) {
			return null;
		}
		return converter.convertIfNecessary(val, parameter.getParameterType());
	}
}
