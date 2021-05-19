package com.google.api.services.samples.youtube.cmdline.youtube_cmdline_search_sample;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Thumbnail;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import java.net.*;
import java.awt.*;

public class Search {

//	Global instance properties filename
	private static String PROPERTIES_FILENAME = "youtube.properties";

//	Global instance of the HTTP transport
	private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();

//	Global instance of the JSON factory
	private static final JsonFactory JSON_FACTORY = new JacksonFactory();

	
	private static final long NUMBER_OF_VIDEOS_RETURNED = 25;

//	Global instance of YouTube object to make all API requests
	private static YouTube youtube;

	static String vidID;

	static Socket s = null;
	
	
	public static void main(String[] args) throws IOException, InterruptedException {

		ServerSocket ss = new ServerSocket(4999);
		s = ss.accept();
		
		System.out.println("Client connected");
		
		InputStreamReader in = new InputStreamReader(s.getInputStream());
		BufferedReader bf = new BufferedReader(in);
		
		String str = bf.readLine();
		System.out.println("Song requested is: "+ str);
		
		Properties properties = new Properties();
		try {
			InputStream ins = Search.class.getResourceAsStream("/" + PROPERTIES_FILENAME);
			if (ins == null) {
				System.err.println("There was an error reading: " + PROPERTIES_FILENAME);
				System.exit(1);
			}
			properties.load(ins);

		} catch (IOException e) {
			System.err.println("There was an error reading " + PROPERTIES_FILENAME + ": " + e.getCause() + " : " + e.getMessage());
			System.exit(1);
		}

		try {
			
			youtube = new YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY, new HttpRequestInitializer() {
						public void initialize(HttpRequest request)
								throws IOException {
						}
					}).setApplicationName("youtube-search")
					.build();
			
			String queryTerm = str;

			YouTube.Search.List search = youtube.search().list("id, snippet");
			
			String apiKey = properties.getProperty("youtube.apikey");
			search.setKey(apiKey);
			search.setQ(queryTerm);
			
			search.setType("video");
			
			search.setFields("items(id/kind,id/videoId,snippet/title,snippet/thumbnails/default/url)");
			search.setMaxResults(NUMBER_OF_VIDEOS_RETURNED);
			SearchListResponse searchResponse = search.execute();

			List<SearchResult> searchResultList = searchResponse.getItems();

			if (searchResultList != null) {
				prettyPrint(searchResultList.iterator(), queryTerm);
			}
		} catch (GoogleJsonResponseException e) {
			System.err.println("There was a service error: "
					+ e.getDetails().getCode() + " : "
					+ e.getDetails().getMessage());
		} catch (IOException e) {
			System.err.println("There was an IO error: " + e.getCause() + " : "
					+ e.getMessage());
		} catch (Throwable t) {
			t.printStackTrace();
		}

		ss.close();
	}

	
	private static void prettyPrint(Iterator<SearchResult> iteratorSearchResults, String query) throws IOException {
		int k=0;
		System.out.println("\n=============================================================");
		System.out.println("   First " + NUMBER_OF_VIDEOS_RETURNED + " videos for search on \"" + query + "\".");
		System.out.println("=============================================================\n");

		if (!iteratorSearchResults.hasNext()) {
			System.out.println(" There aren't any results for your query.");
		}

		while (iteratorSearchResults.hasNext()) {
			SearchResult singleVideo = iteratorSearchResults.next();
			ResourceId rId = singleVideo.getId();

//			Double checks the kind is video
			if (rId.getKind().equals("youtube#video")) {
				Thumbnail thumbnail = (Thumbnail) singleVideo.getSnippet().getThumbnails().get("default");
				
				System.out.println(" Video ID: " + rId.getVideoId());
				if(k==0)
					vidID="https://www.youtube.com/v/"+rId.getVideoId();
				System.out.println(" Title: " + singleVideo.getSnippet().getTitle());
				System.out.println(" Thumbnail: " + thumbnail.getUrl());
				System.out.println("\n-------------------------------------------------------------\n");
			}
			k++;
		}
		try {
		    Desktop.getDesktop().browse(new URL(vidID).toURI());
		} catch (Exception e) {}
	}
}