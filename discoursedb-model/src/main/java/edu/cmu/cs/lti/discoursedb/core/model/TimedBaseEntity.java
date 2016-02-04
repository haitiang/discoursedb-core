package edu.cmu.cs.lti.discoursedb.core.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

/**
 * Adds basic fields to entities that do not keep track of their lifetime (version, entity creation date)
 * 
 * @author Oliver Ferschke
 *
 */
@Data
@MappedSuperclass
public abstract class TimedBaseEntity{

	@JsonIgnore
	@Version
	@Setter(AccessLevel.PRIVATE) 
	private Long version;	
	
	@JsonIgnore
	@CreationTimestamp
	@Column(name = "created")
	@Setter(AccessLevel.PRIVATE) 
	private Date createDate;
	
	@Column(name = "start_time")
	@Temporal(TemporalType.TIMESTAMP)
    private Date startTime;   

	@Column(name = "end_time")
	@Temporal(TemporalType.TIMESTAMP)
    private Date endTime;

}
