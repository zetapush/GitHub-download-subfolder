package com.zetapush.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.zeroturnaround.zip.ZipUtil;

import feign.Feign;


@RestController
public class Controller {
	
	@RequestMapping(value="/", method=RequestMethod.GET, produces="application/zip")
	@ResponseBody
	@CrossOrigin(origins = "*")
	public ResponseEntity<InputStreamResource> getSubfolder(
			@RequestParam(required = true) String owner,
			@RequestParam(required = true) String repository,
			@RequestParam(required = false, defaultValue = "") String path
			) throws IOException {
		
		// Clean the path
		path = path.replaceAll("^\\.", "").replaceAll("/$", "").replaceAll("^/", "");
			
		// CONSTANTS
		Long currentTimestamp = System.currentTimeMillis();
		String[] arrayPath = path.split("/");
		String nameLastFolder = arrayPath[arrayPath.length - 1];
		String nameRootFolder = path.length() > 0 ? nameLastFolder : repository;
		String pathFolderOfInLocalStorage = "/tmp/" + currentTimestamp + "-" + nameRootFolder	 + "/";
		String pathOutputZipInLocalStorage = "/tmp/" + currentTimestamp + "-" + nameRootFolder + ".zip";
		String nameOfTheZipFile = nameRootFolder + ".zip";
		
		// API to interact with GitHub
		GitHubHttpClientFolder apiFolder = Feign.builder().target(GitHubHttpClientFolder.class, "https://api.github.com/repos");
		GitHubHttpGetContent apiContent = Feign.builder().target(GitHubHttpGetContent.class, "https://raw.githubusercontent.com/zetapush/zetapush-tutorials/master");
				
		// Root folder of the subfolder we want to get
		String firstFolderToAnalyze = owner + "/" + repository + "/contents/" + path;
		
		// Starting to get subfolder
		getContentFiles(apiFolder, apiContent, pathFolderOfInLocalStorage, firstFolderToAnalyze);

		// Zip the output subfolder
		File zipOutput = new File(pathOutputZipInLocalStorage);
		ZipUtil.pack(new File(pathFolderOfInLocalStorage), zipOutput);
		
		// Resource stream to return it
		InputStreamResource resource = new InputStreamResource(new FileInputStream(zipOutput));
		
		return ResponseEntity
				.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment;filename=" + nameOfTheZipFile)
				.contentType(MediaType.APPLICATION_PDF).contentLength(zipOutput.length())
				.body(resource);
	}
	
	/**
	 * Iterate on the target subfolder to get all files
	 * @param apiFolder HttpClient to iterate on each subfolder
	 * @param apiContent HttpClient to get content of each file 
	 * @param pathLocalFolder Path of the subfolder in the local storage (for saving content)
	 * @param pathRemoteFolder Path of the subfolder in the GitHub directory
	 */
	public void getContentFiles(GitHubHttpClientFolder apiFolder, GitHubHttpGetContent apiContent, String pathLocalFolder, String pathRemoteFolder) {
		// Get the subfolder as an array of JSON object for each entry (directory or file)
		JSONArray files = new JSONArray(apiFolder.getSubfolder(pathRemoteFolder));
		
		// Create a new subfolder in the local storage for each subfolder we analyse
		File localFolder = new File(pathLocalFolder);
		localFolder.mkdirs();
		
		// Iterate on each entry on the subfolder
		for (int i=0; i<files.length(); i++) {
			JSONObject entry = files.getJSONObject(i);
			String typeEntry = entry.getString("type");
			
			// Save the content of each file in the subfolder
			if (typeEntry.equals("file")) {
				// Get content and save it
				String content = apiContent.getFileContent(entry.getString("path"));
				
				File file = new File(pathLocalFolder + entry.getString("name"));
				FileWriter writer = null;
				try {
					if (file.createNewFile()) {
						 writer = new FileWriter(file);
						writer.write(content);
					}
				} catch (IOException e1) {
					e1.printStackTrace();
				} finally {
					if (writer != null)
						try {
							writer.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
				}
			} else if (typeEntry.equals("dir")) {
				// Iterate on the next subfolder to get content of each file inside it
				getContentFiles(apiFolder, apiContent, pathLocalFolder + entry.getString("name") + "/", pathRemoteFolder + "/" + entry.getString("name"));
			}
		}	
	}
}