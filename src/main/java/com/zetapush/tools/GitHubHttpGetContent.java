package com.zetapush.tools;

import org.springframework.cloud.openfeign.FeignClient;

import feign.Headers;
import feign.Param;
import feign.RequestLine;

@FeignClient("github-content")
@Headers("Authorization: token {token}")
public interface GitHubHttpGetContent {

	@RequestLine("GET {path}")
	String getFileContent(@Param("token") String token, @Param("path") String path);
}