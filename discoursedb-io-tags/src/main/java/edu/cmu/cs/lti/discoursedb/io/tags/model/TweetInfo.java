package edu.cmu.cs.lti.discoursedb.io.tags.model;

import lombok.Data;

/**
 * POJO to represents twitter data collected by TAG v6.0.
 * 
 * Boilerplate code will be generated by Lombok at compile time.
 * 
 * @author Haitian Gong
 * @author Oliver Ferschke
 *
 */
@Data
public class TweetInfo {
	
	private String id_str;
	private String from_user;
	private String text;
	private String created_at;
	private String time;
	private String geo_coordinates;
	private String user_lang;
	private String in_reply_to_user;
	private String in_reply_to_screen_name;
	private String from_user_id_str;
	private String in_reply_to_user_id_str;
	private String in_reply_to_status_id_str;
	private String source;
	private String profile_image_url;
	private String user_followers_count;
	private String user_friends_count;
	private String user_utc_offset;
	private String status_url;
	private String entities_str;	
}
