package org.shoppingcart.config;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class WebMvcConfig implements WebMvcConfigurer {

	private static final Logger LOGEVENT = Logger.getLogger(WebMvcConfig.class);

	private static final Charset UTF8 = Charset.forName("UTF-8");

	// Config UTF-8 Encoding
	public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
		StringHttpMessageConverter messageConverter = new StringHttpMessageConverter();
		messageConverter.setSupportedMediaTypes(Arrays.asList(new MediaType("text", "plain", UTF8)));
		converters.add(messageConverter);
		LOGEVENT.info("WebMvcConfig -> configureMessageConverters -> " + messageConverter);
	}

	// Static Resource Config equivalents for <mvc:resources/> tag
	public void addResourcesHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/css/**").addResourceLocations("/css/").setCachePeriod(31556926);
		registry.addResourceHandler("/img/**").addResourceLocations("/img/").setCachePeriod(31556926);
		registry.addResourceHandler("/js/**").addResourceLocations("/js/").setCachePeriod(31556926);
		LOGEVENT.info("WebMvcConfig -> addResourcesHandlers");
	}

	// Equivalent for <mvc:default-servlet-handler> tag
	public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
		configurer.enable();
		LOGEVENT.info("WebMvcConfig -> configureDefaultServletHandling");
	}
}