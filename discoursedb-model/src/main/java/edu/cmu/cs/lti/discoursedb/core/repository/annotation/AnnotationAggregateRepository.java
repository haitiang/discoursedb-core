package edu.cmu.cs.lti.discoursedb.core.repository.annotation;

import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import edu.cmu.cs.lti.discoursedb.core.model.annotation.AnnotationAggregate;
import edu.cmu.cs.lti.discoursedb.core.repository.BaseRepository;

@RepositoryRestResource(collectionResourceRel = "annotations", path = "annotations")
public interface AnnotationAggregateRepository extends BaseRepository<AnnotationAggregate,Long>{
    
    
}
