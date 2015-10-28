package edu.cmu.cs.lti.discoursedb.core.model.macro;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import edu.cmu.cs.lti.discoursedb.core.model.BaseTypeEntity;

@Entity
@Table(name="discourse_relation_type")
public class DiscourseRelationType extends BaseTypeEntity implements Serializable {

	private static final long serialVersionUID = -6905270877949246079L;

	private long id;
	
	private Set<DiscourseRelation> discourseRelations = new HashSet<DiscourseRelation>();

	public DiscourseRelationType(){}
	
	@Id
	@Column(name="id_discourse_relation_type", nullable=false)
    @GeneratedValue(strategy = GenerationType.AUTO)
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL, mappedBy="type")
	public Set<DiscourseRelation> getDiscourseRelations() {
		return discourseRelations;
	}

	public void setDiscourseRelations(Set<DiscourseRelation> discourseRelations) {
		this.discourseRelations = discourseRelations;
	}

	public void addDiscourseRelation(DiscourseRelation discourseRelation) {
		this.discourseRelations.add(discourseRelation);
	}

}
