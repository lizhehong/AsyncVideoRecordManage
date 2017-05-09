package cn.hy;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

//@RunWith(SpringRunner.class)
//@SpringBootTest
public class AsyncVideoRecorderManageApplicationTests {

	private String regexDuration = "Duration: (.*?), start: (.*?), bitrate: (\\d*) kb\\/s"; 
	
	private String regexVideo = "Video: (.*?), (.*?), (.*?), (.*?) fps, (.*?) tbr, (.*?)k tbn, (.*?) tbc";
	private String regexAudio = "Audio: (\\w*), (\\d*) Hz";
	
	@Test
	public void contextLoads() throws IOException {
		String path = "E:/download_vr/c0a80013-5bd6-123f-815b-d6a4c61c0007/video-0.mp4";
		Process process = Runtime.getRuntime().exec("ffmpeg -i "+path);
		InputStream in = process.getErrorStream();
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String line;    
		Pattern pDuration = Pattern.compile(regexDuration);
		Pattern pVideo = Pattern.compile(regexVideo);
		Pattern pAudio = Pattern.compile(regexAudio);
        while((line=br.readLine()) != null){    
        	Matcher matcher = pDuration.matcher(line);
        	if(matcher.find())
        		System.out.println(matcher.group(1) + " " + matcher.group(2)+ " " +matcher.group(3));
        	matcher = pVideo.matcher(line);
        	if(matcher.find())
        		System.out.println(
        				matcher.group(1) + 
        				" " + matcher.group(2)+ 
        				" " +matcher.group(3)+
        				" " +matcher.group(4)+
        				" " +matcher.group(5)+
        				" " +matcher.group(6)+
        				" " +matcher.group(7)
        				);
        	matcher = pAudio.matcher(line);
        	if(matcher.find())
        		System.out.println(matcher.group(1) + " " + matcher.group(2)+ " " );
        } 	
	}

}
