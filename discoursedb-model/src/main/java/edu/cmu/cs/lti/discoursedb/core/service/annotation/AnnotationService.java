package edu.cmu.cs.lti.discoursedb.core.service.annotation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import edu.cmu.cs.lti.discoursedb.core.model.TimedAnnotatableBE;
import edu.cmu.cs.lti.discoursedb.core.model.TypedTimedAnnotatableBE;
import edu.cmu.cs.lti.discoursedb.core.model.annotation.AnnotationAggregate;
import edu.cmu.cs.lti.discoursedb.core.model.annotation.AnnotationInstance;
import edu.cmu.cs.lti.discoursedb.core.model.annotation.Feature;
import edu.cmu.cs.lti.discoursedb.core.repository.annotation.AnnotationInstanceRepository;
import edu.cmu.cs.lti.discoursedb.core.repository.annotation.AnnotationAggregateRepository;
import edu.cmu.cs.lti.discoursedb.core.repository.annotation.FeatureRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Service
@Transactional(propagation= Propagation.REQUIRED, readOnly=false)
@RequiredArgsConstructor(onConstructor = @__(@Autowired) )
public class AnnotationService {

	private final @NonNull AnnotationInstanceRepository annoInstanceRepo;
	private final @NonNull AnnotationAggregateRepository annoRepo;
	private final @NonNull FeatureRepository featureRepo;
	
	/**
	 * Retrieves all annotations for the given entity.
	 * 
	 * This is a convenience method. 
	 * It actually just retrieves annotations from the entity object, but it performs additional null checks on the annotation aggregate.
	 * 
	 * @param entity the entity to retrieve the annotations for
	 * @return a set of AnnotationInstances for the given entity
	 */
	@Transactional(propagation= Propagation.REQUIRED, readOnly=true)
	public <T extends TimedAnnotatableBE> Set<AnnotationInstance> findAnnotations(T entity) {
		Assert.notNull(entity,"Entity cannot be null. Provide an annotated entity.");		
		AnnotationAggregate annos = entity.getAnnotations();
		return annos==null?new HashSet<AnnotationInstance>():annos.getAnnotations();
	}
	
	/**
	 * Retrieves all annotations for the given entity.
	 * 
	 * This is a convenience method. 
	 * It actually just retrieves annotations from the entity object, but it performs additional null checks on the annotation aggregate.
	 * 
	 * @param entity the entity to retrieve the annotations for
	 * @return a set of AnnotationInstances for the given entity
	 */
	@Transactional(propagation= Propagation.REQUIRED, readOnly=true)
	public <T extends TypedTimedAnnotatableBE> Set<AnnotationInstance> findAnnotations(T entity) {
		Assert.notNull(entity,"Entity cannot be null. Provide an annotated entity.");		
		AnnotationAggregate annos = entity.getAnnotations();
		return annos==null?new HashSet<AnnotationInstance>():annos.getAnnotations();
	}
	
	/**
	 * Creates a new AnnotationInstance and associates it with an AnnotationType that matches the provided String.
	 * If an annotationtype with the provided String already exists, it will be reused.
	 * 
	 * @param type
	 *            the value for the AnnotationType
	 * @return a new empty AnnotationInstance that is already saved to the db and
	 *         connected with its requested type
	 */
	public AnnotationInstance createTypedAnnotation(String type){
		Assert.hasText(type,"Type cannot be empty. Provide an annotation type or create untyped AnnotationInstance.");
		
		AnnotationInstance annotation = new AnnotationInstance();
		annotation.setType(type);
		return annoInstanceRepo.save(annotation);
	}	
	
	/**
	 * Creates a new Feature with the provided value.
	 * No type is assigned.
	 * 
	 * @param value
	 *            the feature value
	 * @return a new empty Feature that is already saved to the db and
	 *         connected with its requested type
	 */
	public Feature createFeature(String value){
		Assert.hasText(value,"Feature value cannot be empty.");
		
		Feature feature = new Feature();		
		feature.setValue(value);
		return featureRepo.save(feature);
	}	
	
	/**
	 * Creates a new Feature and associates it with a FeatureType that matches the provided String.
	 * If an FeatureType with the provided String already exists, it will be reused.
	 * 
	 * @param value
	 *            the feature value
	 * @param type
	 *            the value for the FeatureType
	 * @return a new empty Feature that is already saved to the db and
	 *         connected with its requested type
	 */
	public Feature createTypedFeature(String value, String type){
		Assert.hasText(value,"Feature value cannot be empty.");
		Assert.hasText(type,"Type cannot be empty. Provide a feature type or create untype feature.");
		
		Feature feature = createTypedFeature(type);		
		feature.setValue(value);		
		return featureRepo.save(feature);
	}	

	/**
	 * Creates a new Feature and associates it with a FeatureType that matches the provided String.
	 * If an FeatureType with the provided String already exists, it will be reused.
	 * 
	 * @param type
	 *            the value for the FeatureType
	 * @return a new empty Feature that is already saved to the db and
	 *         connected with its requested type
	 */
	public Feature createTypedFeature(String type){
		Assert.hasText(type,"Type cannot be empty. Provide a feature type or create untyped feature.");
		
		Feature feature = new Feature();
		feature.setType(type);
		return featureRepo.save(feature);
	}	
	
	/**
	 * Adds a new annotation instance to the provided entity.<br/>
	 * 
	 * @param entity
	 *            the entity to add a new source to
	 * @param annotation
	 *            the annotation instance to add to the entity
	 */
	public <T extends TypedTimedAnnotatableBE> void addAnnotation(T entity, AnnotationInstance annotation) {		
		Assert.notNull(entity,"Entity cannot be null. Provide an annotatable entity.");
		Assert.notNull(annotation, "Annotation cannot be null.");

		//the annotations aggregate is a proxy for the entity
		//all annotation instantimeAnnotatableBaseEntityRepo.ces are connected to the aggregate which is finally connected to the annotated entity
		AnnotationAggregate annoAggregate = entity.getAnnotations();
		if (annoAggregate == null) {
			annoAggregate=annoRepo.save(new AnnotationAggregate());
			entity.setAnnotations(annoAggregate);
		}
		annotation.setAnnotationAggregate(annoAggregate);
		annotation = annoInstanceRepo.save(annotation);
	}
	
	/**
	 * Adds a new annotation instance to the provided entity.<br/>
	 * 
	 * @param entity
	 *            the entity to add a new source to
	 * @param annotation
	 *            the annotation instance to add to the entity
	 */
	public <T extends TimedAnnotatableBE> void addAnnotation(T entity, AnnotationInstance annotation) {		
		Assert.notNull(entity,"Entity cannot be null. Provide an annotatable entity.");
		Assert.notNull(annotation, "Annotation cannot be null.");

		//the annotations aggregate is a proxy for the entity
		//all annotation instantimeAnnotatableBaseEntityRepo.ces are connected to the aggregate which is finally connected to the annotated entity
		AnnotationAggregate annoAggregate = entity.getAnnotations();
		if (annoAggregate == null) {
			annoAggregate=annoRepo.save(new AnnotationAggregate());
			entity.setAnnotations(annoAggregate);
		}
		annotation.setAnnotationAggregate(annoAggregate);
		annotation = annoInstanceRepo.save(annotation);
	}
	
	/**
	 * Deletes an annotation from DiscourseDB
	 * 
	 * @param annotation
	 *            the annotation instance to add to delete
	 */
	public <T extends TypedTimedAnnotatableBE> void deleteAnnotation(AnnotationInstance annotation) {		
		Assert.notNull(annotation,"Annotation to delete cannot be null.");
		Set<Feature> features = annotation.getFeatures();
		if(features!=null&&!features.isEmpty()){
			featureRepo.delete(annotation.getFeatures());			
		}
		annoInstanceRepo.delete(annotation);
	}

	/**
	 * Deletes an annotation from DiscourseDB
	 * 
	 * @param annotation
	 *            the annotation instance to add to delete
	 */
	public <T extends TypedTimedAnnotatableBE> void deleteAnnotations(Iterable<AnnotationInstance> annotations) {		
		Assert.notNull(annotations, "Annotation iterable cannot be null.");

		List<Feature> featuresToDelete = new ArrayList<>();
		for(AnnotationInstance anno:annotations){
			Set<Feature> features = anno.getFeatures();
			if(features!=null&&!features.isEmpty()){
				featuresToDelete.addAll(features);
			}
		}
		featureRepo.delete(featuresToDelete);
		annoInstanceRepo.delete(annotations);
	}
	
	/**
	 * Checks whether the given enity has an annotation of the given type.
	 * 
	 * @param entity
	 *            the entity to check for annotations
	 * @param type
	 * 			  the annotation type to check for
	 */
	public <T extends TypedTimedAnnotatableBE> boolean hasAnnotationType(T entity, String type) {		
		Assert.notNull(entity,"Entity cannot be null. Provide an annotated entity.");
		Assert.hasText(type,"Type cannot be empty. Provide an annotation type.");		
		return entity.getAnnotations().getAnnotations().stream().filter(e -> e.getType()!=null).anyMatch(e -> e.getType().equalsIgnoreCase(type));		
	}
	
	/**
	 * Checks whether the given enity has an annotation of the given type.
	 * 
	 * @param entity
	 *            the entity to check for annotations
	 * @param type
	 * 			  the annotation type to check for
	 */
	public <T extends TimedAnnotatableBE> boolean hasAnnotationType(T entity, String type) {		
		Assert.notNull(entity,"Entity cannot be null. Provide an annotated entity.");
		Assert.hasText(type,"Type cannot be empty. Provide an annotation type.");		
		return entity.getAnnotations().getAnnotations().stream().filter(e -> e.getType()!=null).anyMatch(e -> e.getType().equalsIgnoreCase(type));		
	}
	
	/**
	 * Adds a new annotation instance to the provided entity.
	 * 
	 * @param annotation
	 *            the annotation to which the feature should be added
	 * @param feature
	 *            the new feature to add
	 */
	public void addFeature(AnnotationInstance annotation, Feature feature) {		
		Assert.notNull(annotation, "Annotation cannot be null.");
		Assert.notNull(feature, "Feature cannot be null.");		
		feature.setAnnotation(annotation);
	}

	/**
	 * Finds all annotations that have a feature matching a type=value pair
	 * 
	 * @param type  The feature type to search for
	 * @param value The value of the feature
	 * @return a List of annotations
	 */
	public List<AnnotationInstance> findAnnotationsByFeatureTypeAndValue(String type, String value) {
	        Assert.hasText(type,"Type cannot be empty. Provide an annotation type or create untyped AnnotationInstance.");
	
	        List<Feature> features = featureRepo.findAllByTypeAndValue(type, value);
	        List<AnnotationInstance> annotations = new ArrayList<AnnotationInstance>();
	        for(Feature f : features) {
	                annotations.add(f.getAnnotation());
	        }
	        return annotations;
	}

}
