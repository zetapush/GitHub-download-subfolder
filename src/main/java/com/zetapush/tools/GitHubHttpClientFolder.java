package com.zetapush.tools;

import org.springframework.cloud.openfeign.FeignClient;

import feign.Headers;
import feign.Param;
import feign.RequestLine;

@FeignClient("github-folder")
@Headers("Authorization: token 5605ddf85fcac4db2b8aa31478a8f59d9b36bb9a")
public interface GitHubHttpClientFolder {
	
	@RequestLine("GET /{path}")
	String getSubfolder(@Param("path") String path);
}