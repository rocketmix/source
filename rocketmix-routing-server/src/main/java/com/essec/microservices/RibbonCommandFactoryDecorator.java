package com.essec.microservices;

import java.lang.reflect.Field;

import org.springframework.cloud.netflix.ribbon.support.RibbonCommandContext;
import org.springframework.cloud.netflix.zuul.filters.route.RibbonCommand;
import org.springframework.cloud.netflix.zuul.filters.route.RibbonCommandFactory;

import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;

public class RibbonCommandFactoryDecorator implements RibbonCommandFactory {

	private RibbonCommandFactory factory;
	
	public RibbonCommandFactoryDecorator(RibbonCommandFactory factory) {
		this.factory = factory;
	}
	
	
	@Override
	public RibbonCommand create(RibbonCommandContext context) {
		RibbonCommand ribbonCommand = this.factory.create(context);
		fix(ribbonCommand, "commandKey", HystrixCommandKey.Factory.asKey(context.getUri()));
		fix(ribbonCommand, "commandGroup", HystrixCommandGroupKey.Factory.asKey(context.getServiceId()));
		return ribbonCommand;
	}
	
	
	private boolean fix(Object object, String fieldName, Object fieldValue) {
	    Class<?> clazz = object.getClass();
	    while (clazz != null) {
	        try {
	            Field field = clazz.getDeclaredField(fieldName);
	            field.setAccessible(true);
	            field.set(object, fieldValue);
	            return true;
	        } catch (NoSuchFieldException e) {
	            clazz = clazz.getSuperclass();
	        } catch (Exception e) {
	            throw new IllegalStateException(e);
	        }
	    }
	    return false;
	}

}
