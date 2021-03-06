package edu.cmu.cs.lti.discoursedb.io.prosolo.socialactivity.model;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Wraps entities form the node table in prosolo
 * 
 * @author Oliver Ferschke
 *
 */
@Data
@AllArgsConstructor
public class ProsoloNode {
	private String dtype;	
	private Long id;
	private Date created;
	private Boolean deleted;
	private String dc_description;
	private String title;
	private String visibility;
	private Date deadline;
	private Boolean free_to_join;
	private Boolean archived;
	private Date completed_date;
	private Integer progress;
	private Boolean progress_activity_dependent;
	private Integer duration;
	private String type;
	private Integer validity_period;
	private Boolean compl;
	private Date completed_day;
	private Date date_finished;
	private Date date_started;
	private String assignment_link;
	private String assignment_title;
	private Boolean completed;
	private Date date_completed;
	private Long ta_position;
	private Boolean mandatory;
	private Integer max_files_number;
	private Boolean visible_to_everyone;
	private Long maker;
	private Long course_enrollment;
	private Long learning_goal;
	private Long competence;
	private Long parent_goal;
	private Long activity;
	private Long parent_competence;
	private Long rich_content;
}
