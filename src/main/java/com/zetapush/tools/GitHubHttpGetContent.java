package com.zetapush.tools;

import org.springframework.cloud.openfeign.FeignClient;

import feign.Headers;
import feign.Param;
import feign.RequestLine;

@FeignClient("github-content")
@Headers("Authorization: token c08eb69724dc94c48edcbc0460e6edca6a5ce820")
public interface GitHubHttpGetContent {

	@RequestLine("GET {path}")
	String getFileContent(@Param("path") String path);
}