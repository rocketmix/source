package com.essec.microservices;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.google.common.io.CharStreams;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;

@Component
@Profile("recording")
public class LogRequestFilter extends ZuulFilter {
	private static Logger log = LoggerFactory.getLogger(LogRequestFilter.class);
	@Value("${recording.file:c:/temp/record.txt}")
	private String recordFile;

	@Override
	public String filterType() {
		return "pre";
	}

	@Override
	public int filterOrder() {
		return 2;
	}

	@Override
	public boolean shouldFilter() {
		return true;
	}

	@Override
	public Object run() {
		RequestContext ctx = RequestContext.getCurrentContext();
		HttpServletRequest request = new HttpServletRequestWrapper(ctx.getRequest());
		String requestData = null;
		try {
			if (request.getContentLength() > 0) {
				requestData = CharStreams.toString(request.getReader());
			}
		} catch (Exception e) {
			log.error("Error parsing request", e);
		}
		try {
			// String line = String.format("Request, %s, %s,%s,%s \r\n",
			// getContext().getGlobalId(), request.getRequestURL(),
			// request.getMethod(), requestData);
			String line = "test";
			BufferedWriter bw = Files.newBufferedWriter(Paths.get(recordFile), Charset.forName("UTF-8"), StandardOpenOption.APPEND);
			bw.write(line);
			bw.close();
		} catch (IOException e) {
			log.error("Error writing request", e);
		}
		return null;
	}
}
