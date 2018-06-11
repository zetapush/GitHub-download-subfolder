package com.zetapush.tools;

import org.springframework.cloud.openfeign.FeignClient;

import feign.Headers;
import feign.Param;
import feign.RequestLine;

@FeignClient("github-folder")
@Headers("Authorization: token {token}")
public interface GitHubHttpClientFolder {
	
	@RequestLine("GET /{path}")
	String getSubfolder(@Param("token") String token, @Param("path") String path);
}