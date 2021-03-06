package edu.cmu.cs.lti.discoursedb.io.wikipedia.talk.model;

import java.sql.Timestamp;

/**
 * Represents paragraph in Wikipedia Discussion page
 * 
 * @author chebotar
 * @author ferschke
 *
 */
public class TalkPageParagraph {
	
	private String text;				// Text of the paragraph
	private int begin;					// Begin index of the paragraph
	private int end;					// End index of the paragraph
	private int indentAmount;			// Indentation amount of this paragraph
	private String contributor;			// Contributor of this paragraph
	private Timestamp timestamp;		// Timestamp when this paragraph was created
	private long revisionId;    		// id of the revision of origin
	private boolean contributorIsBot;   // id of the revision of origin

	public static final String UNKNOWN_AUTHOR = "UNKNOWN_CONTRIBUTOR";
	
	public long getRevisionId() {
		return revisionId;
	}
	
	public void setRevisionId(long revisionId) {
		this.revisionId = revisionId;
	}
	
	/**
	 * @return the indentAmount
	 */
	public int getIndentAmount() {
		return indentAmount;
	}
	/**
	 * @return the contributor
	 */
	public String getContributor() {
		if(contributor==null||contributor.isEmpty()){
			return UNKNOWN_AUTHOR;
		}else{
			return contributor;			
		}
	}
	/**
	 * @param contributor the contributor to set
	 */
	public void setContributor(String contributor) {
		this.contributor = contributor;
	}
	/**
	 * @return the timestamp
	 */
	public Timestamp getTimestamp() {
		return timestamp;
	}
	/**
	 * @param timestamp the timestamp to set
	 */
	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}
	/**
	 * @param indentAmount the indentAmount to set
	 */
	public void setIndentAmount(int indentAmount) {
		this.indentAmount = indentAmount;
	}
	/**
	 * @return the text
	 */
	public String getText() {
		return text;
	}
	/**
	 * @param text the text to set
	 */
	public void setText(String text) {
		this.text = text;
	}

	/**
	 * @return the start index of that paragraph in the original page
	 */
	public int getBegin() {
		return begin;
	}
	/**
	 * @param begin the begin to set
	 */
	public void setBegin(int begin) {
		this.begin = begin;
	}
	/**
	 * @return the end index of the paragraph in the original page
	 */
	public int getEnd() {
		return end;
	}
	/**
	 * @param end the end to set
	 */
	public void setEnd(int end) {
		this.end = end;
	}

	public boolean contributorIsBot() {
		return contributorIsBot;
	}

	public void setContributorIsBot(boolean contributorIsBot) {
		this.contributorIsBot = contributorIsBot;
	}
	
	public boolean isValid(){
		return  getContributor() != null && 
				getTimestamp() != null && 
				!getContributor().isEmpty();
	}

}
