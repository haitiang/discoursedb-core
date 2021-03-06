package edu.cmu.cs.lti.discoursedb.core.service.macro;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import edu.cmu.cs.lti.discoursedb.core.model.annotation.AnnotationAggregate;
import edu.cmu.cs.lti.discoursedb.core.model.annotation.AnnotationInstance;
import edu.cmu.cs.lti.discoursedb.core.model.macro.Contribution;
import edu.cmu.cs.lti.discoursedb.core.model.macro.Discourse;
import edu.cmu.cs.lti.discoursedb.core.model.macro.DiscoursePart;
import edu.cmu.cs.lti.discoursedb.core.model.macro.DiscoursePartContribution;
import edu.cmu.cs.lti.discoursedb.core.model.macro.DiscoursePartRelation;
import edu.cmu.cs.lti.discoursedb.core.model.macro.DiscourseToDiscoursePart;
import edu.cmu.cs.lti.discoursedb.core.model.system.DataSourceInstance;
import edu.cmu.cs.lti.discoursedb.core.repository.macro.DiscoursePartContributionRepository;
import edu.cmu.cs.lti.discoursedb.core.repository.macro.DiscoursePartRelationRepository;
import edu.cmu.cs.lti.discoursedb.core.repository.macro.DiscoursePartRepository;
import edu.cmu.cs.lti.discoursedb.core.repository.macro.DiscourseToDiscoursePartRepository;
import edu.cmu.cs.lti.discoursedb.core.service.system.DataSourceService;
import edu.cmu.cs.lti.discoursedb.core.type.DiscoursePartRelationTypes;
import edu.cmu.cs.lti.discoursedb.core.type.DiscoursePartTypes;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Service
@Transactional(propagation= Propagation.REQUIRED, readOnly=false)
@RequiredArgsConstructor(onConstructor = @__(@Autowired) )
public class DiscoursePartService {

	private final @NonNull DiscoursePartRepository discoursePartRepo;
	private final @NonNull DataSourceService dataSourceService;
	private final @NonNull DiscoursePartRelationRepository discoursePartRelationRepo;
	private final @NonNull DiscoursePartContributionRepository discoursePartContributionRepo;
	private final @NonNull DiscourseToDiscoursePartRepository discourseToDiscoursePartRepo;

	/**
	 * Retrieves existing or creates a new DiscoursePartType entity with the
	 * provided type. It then creates a new empty DiscoursePart entity,
	 * connects it with the type and the provided discourse.<br/>
	 * 
	 * All changed/created entities are committed to the db and the DiscoursePart is returned.<br/>
	 * 
	 * The discoursePartName is constructed like this: <code>discourseName_DiscoursePartType</code>.<br/>
	 * Use {@link #createOrGetTypedDiscoursePart(Discourse discourse, String discoursePartName, DiscoursePartTypes type)} to explicitly set the discoursePartName. 
	 * 
	 * @param discourse
	 *            the discourse of which the new DiscoursePart is a part of
	 * @param type
	 *            the value for the DiscoursePartType
	 * @return a new empty DiscoursePart that is already saved to the db and
	 *         connected with its requested type
	 */
	public DiscoursePart createOrGetTypedDiscoursePart(Discourse discourse, DiscoursePartTypes type){
		Assert.notNull(discourse, "Discourse cannot be null.");
		Assert.notNull(type, "Type cannot be null.");		

		return createOrGetTypedDiscoursePart(discourse,discourse.getName()+"_"+type.name(),type);
	}
	/**
	 * Retrieves existing or creates a new DiscoursePartType entity with the
	 * provided type. It then creates a new empty DiscoursePart entity,
	 * connects it with the type and the provided discourse.
	 * 
	 * All changed/created entities are committed to the db and the DiscoursePart is returned.
	 * 
	 * @param discourse
	 *            the discourse of which the new DiscoursePart is a part of
	 * @param discoursePartName
	 *            the name of the discoursePart that should be retrieved or created
	 * @param type
	 *            the value for the DiscoursePartType
	 * @return a new empty DiscoursePart that is already saved to the db and
	 *         connected with its requested type
	 */
	public DiscoursePart createOrGetTypedDiscoursePart(Discourse discourse, String discoursePartName, DiscoursePartTypes type){		
		Assert.notNull(discourse, "Discourse cannot be null.");
		Assert.hasText(discoursePartName, "DiscoursePart name cannot be empty");
		Assert.notNull(type, "Type cannot be null.");		

		//check if this exact discoursePart already exists, reuse it if it does and create it if it doesn't
		Optional<DiscoursePart> existingDiscoursePart = Optional.ofNullable(discoursePartRepo.findOne(
						DiscoursePartPredicates.discoursePartHasName(discoursePartName).and(
						DiscoursePartPredicates.discoursePartHasType(type).and(
						DiscoursePartPredicates.discoursePartHasDiscourse(discourse)))));

		DiscoursePart dPart=existingDiscoursePart.orElseGet(()->{
			DiscoursePart newDP=new DiscoursePart();
			newDP.setType(type.name());
			newDP.setName(discoursePartName);
			return discoursePartRepo.save(newDP);
			}
		);			
		
		Optional<DiscourseToDiscoursePart> existingDiscourseToDiscoursePart = discourseToDiscoursePartRepo.findOneByDiscourseAndDiscoursePart(discourse, dPart);	
		if(!existingDiscourseToDiscoursePart.isPresent()){
			DiscourseToDiscoursePart discourseToDiscoursePart = new DiscourseToDiscoursePart();			
			discourseToDiscoursePart.setDiscourse(discourse);
			discourseToDiscoursePart.setDiscoursePart(dPart);
			discourseToDiscoursePartRepo.save(discourseToDiscoursePart);			
		}
		
		return dPart;
	}		

	
	
	
	/**
	 * Creates a new DiscoursePart with the provided parameters even if a DiscoursePart with the same name already exists.
 	 * The discoursePartName is constructed like this: <code>discourseName_DiscoursePartType</code>.<br/>
	 * Use {@link #createTypedDiscoursePart(Discourse discourse, String discoursePartName, DiscoursePartTypes type)} to explicitly set the discoursePartName. 
	 * 
	 * If duplicate discoursePartNames should be avoided, use {@link #createOrGetTypedDiscoursePart(Discourse discourse, DiscoursePartTypes type)}. 
	 * This only creates a DiscoursePart if none with the same name exists and retrieves the existing one otherwise.. 
	 * Duplicate names make sense if the DiscoursePart is further disambiguated with DataSources or through relations with other DiscourseParts.
	 * 
	 * @param discourse
	 *            the discourse of which the new DiscoursePart is a part of
	 * @param type
	 *            the value for the DiscoursePartType
	 * @return a new empty DiscoursePart that is already saved to the db and
	 *         connected with its requested type
	 */
	public DiscoursePart createTypedDiscoursePart(Discourse discourse, DiscoursePartTypes type){
		Assert.notNull(discourse, "Discourse cannot be null.");
		Assert.notNull(type, "Type cannot be null.");		

		return createTypedDiscoursePart(discourse,discourse.getName()+"_"+type.name(),type);
	}
	/**
	 * Creates a new DiscoursePart with the provided parameters even if a DiscoursePart with the same name already exists.
	 * If duplicate discoursePartNames should be avoided, use {@link #createOrGetTypedDiscoursePart(Discourse discourse, String discoursePartName, DiscoursePartTypes type)}. This only creates a DiscoursePart if none with the same name exists and retrieves the existing one otherwise.. 
	 * Duplicate names make sense if the DiscoursePart is further disambiguated with DataSources or through relations with other DiscourseParts.
	 * 
	 * @param discourse
	 *            the discourse of which the new DiscoursePart is a part of
	 * @param discoursePartName
	 *            the name of the discoursePart that should be retrieved or created
	 * @param type
	 *            the value for the DiscoursePartType
	 * @return a new empty DiscoursePart that is already saved to the db and
	 *         connected with its requested type
	 */
	public DiscoursePart createTypedDiscoursePart(Discourse discourse, String discoursePartName, DiscoursePartTypes type){
		Assert.notNull(discourse, "Discourse cannot be null.");
		Assert.hasText(discoursePartName);
		Assert.notNull(type, "Type cannot be null.");		
				
		DiscoursePart dPart=new DiscoursePart();
		dPart.setType(type.name());
		dPart.setName(discoursePartName);
		dPart = discoursePartRepo.save(dPart);
		
		DiscourseToDiscoursePart discourseToDiscoursePart = new DiscourseToDiscoursePart();			
		discourseToDiscoursePart.setDiscourse(discourse);
		discourseToDiscoursePart.setDiscoursePart(dPart);
		discourseToDiscoursePartRepo.save(discourseToDiscoursePart);			
		
		return dPart;
	}	
	
		
	/**
	 * Adds the given contribution to the provided DiscoursePart.
	 * The start date of the relation between the two is initialized with the creation date of the contribution.
	 * 
	 * In case this is not true, the DiscoursePartContribution relation has to be created manually or updated accordingly. 
	 * 
	 * @param contrib the contribution that is part of the given DiscoursePart.
	 * @param dPArt the DiscoursePart that contains the given contribution.
	 */
	public DiscoursePartContribution addContributionToDiscoursePart(Contribution contrib, DiscoursePart dPArt){	
		Assert.notNull(contrib);
		Assert.notNull(dPArt);
		
		return discoursePartContributionRepo.findOneByContributionAndDiscoursePart(contrib, dPArt).orElseGet(()->{
			DiscoursePartContribution newDPContrib = new DiscoursePartContribution();
			newDPContrib.setContribution(contrib);
			newDPContrib.setDiscoursePart(dPArt);
			newDPContrib.setStartTime(contrib.getStartTime());	
			discoursePartContributionRepo.save(newDPContrib);
			return newDPContrib;
		});		
	}
	
	
	/**
	 * Creates a new DiscoursePartRelation of the given type between the two provided DiscourseParts.
	 * Depending on the type, the relation might be directed or not. This information should be given in the type definition.
	 * 
	 * If a DiscoursePartRelation of the given type already exists between the two DiscourseParts (taking into account the direction of the relation),
	 * then the existing relation is returned. 
	 * DiscourseDB does not enforce the uniqueness of these relations by default, but enforcing it in this service method will cater to most of the use cases we will see.
	 * 
	 * @param sourceDiscoursePart the source or parent DiscoursePart of the relation
	 * @param targetDiscoursePart the target or child DiscoursePart of the relation
	 * @param type the DiscoursePartRelationTypes
	 * @return a DiscoursePartRelation between the two provided DiscourseParts with the given type that has already been saved to the database 
	 */
	public DiscoursePartRelation createDiscoursePartRelation(DiscoursePart sourceDiscoursePart, DiscoursePart targetDiscoursePart, DiscoursePartRelationTypes type) {
		Assert.notNull(sourceDiscoursePart);
		Assert.notNull(targetDiscoursePart);
		Assert.notNull(type, "Type cannot be null.");		
				
		//check if a relation of the given type already exists between the two DiscourseParts
		return discoursePartRelationRepo.findOneBySourceAndTargetAndType(sourceDiscoursePart, targetDiscoursePart, type.name()).orElseGet(()->{
			DiscoursePartRelation newRelation = new DiscoursePartRelation();
			newRelation.setSource(sourceDiscoursePart);
			newRelation.setTarget(targetDiscoursePart);
			newRelation.setType(type.name());
			return discoursePartRelationRepo.save(newRelation);						
		});
		
	}
	
	
	/**
	 * Retrieves DiscourseParts that are related to the given DiscoursePart with a DiscoursePartRelation of the given type.
	 * The provided DiscoursePart is the parent or source in this relation.
	 * 
	 * @param sourceDiscoursePart the source or parent DiscoursePart of the relation
	 * @param type the DiscoursePartRelationTypes 
	 * @return a DiscoursePartRelation between the two provided DiscourseParts with the given type that has already been saved to the database 
	 */
	@Transactional(propagation= Propagation.REQUIRED, readOnly=true)
	public List<DiscoursePart> findChildDiscourseParts(DiscoursePart sourceDiscoursePart, DiscoursePartRelationTypes type) {
		Assert.notNull(sourceDiscoursePart);
		Assert.notNull(type, "Type cannot be null.");		
		
		List<DiscoursePart> returnList = new ArrayList<>();
		
		//check if a relation of the given type already exists between the two DiscourseParts
		List<DiscoursePartRelation> existingRelations = discoursePartRelationRepo.findAllBySourceAndType(sourceDiscoursePart, type.name());
		for(DiscoursePartRelation relation:existingRelations){
			returnList.add(relation.getTarget());
		}
		
		return returnList;
	}
	
	
	/**
	 * Saves the provided entity to the db using the save method of the corresponding repository
	 * 
	 * @param part the entity to save
	 * @return the possibly altered entity after the save process 
	 */
	public DiscoursePart save(DiscoursePart part){
		Assert.notNull(part, "DiscoursePart cannot be null.");

		return discoursePartRepo.save(part);
	}
	
	/**
	 * Determines whether a DiscoursePart with the provided parameters exists
	 *  
	 * @param discourse the associated discourse
	 * @param discoursePartName the name of the discourse part
	 * @param type the DiscoursePartType
	 * @return true, if the DiscoursePart exists. False, otherwise
	 */
	@Transactional(propagation= Propagation.REQUIRED, readOnly=true)
	public boolean exists(Discourse discourse, String discoursePartName, DiscoursePartTypes type){
		Assert.notNull(discourse, "Discourse cannot be null.");
		Assert.hasText(discoursePartName, "DiscoursePart name cannot be empty.");
		Assert.notNull(type, "Type cannot be null.");		
		
		return discoursePartRepo.count(
				DiscoursePartPredicates.discoursePartHasName(discoursePartName).and(
				DiscoursePartPredicates.discoursePartHasType(type).and(
				DiscoursePartPredicates.discoursePartHasDiscourse(discourse))))>0;
	}
	
	/**
	 * Returns all DiscourseParts of the given Type independet from a Discourse
	 * 
	 * @param type the type of the discoursepart
	 * @return a list of discoursepart of the given type that might be empty
	 */
	@Transactional(propagation= Propagation.REQUIRED, readOnly=true)
	public List<DiscoursePart> findAllByType(DiscoursePartTypes type){
		Assert.notNull(type, "Type cannot be null.");		
		return discoursePartRepo.findAllByType(type.name());					
	}
	
	/**
	 * Retrieves DiscourseParts by name
	 * 
	 * @param discoursePartName the name of the discourse part to search for
	 * @return an list of DiscoursePart entities that might be empty 
	 */
	@Transactional(propagation= Propagation.REQUIRED, readOnly=true)
	public List<DiscoursePart> findAllByName(String discoursePartName) {
		Assert.hasText(discoursePartName, "DiscoursePart name cannot be empty.");		
		return discoursePartRepo.findAllByName(discoursePartName);
	}
	
    /**
	 * Retrieves a discourse part that has a source which exactly matches the given DataSource parameters.
	 * 
	 * @param entitySourceId the source id of the contribution  
	 * @param entitySourceDescriptor the entitySourceDescriptor
	 * @param dataSetName the dataset the source id was derived from
	 * @return an optional DiscoursePart that meets the requested parameters
	 */
	@Transactional(propagation= Propagation.REQUIRED, readOnly=true)
	public Optional<DiscoursePart> findOneByDataSource(String entitySourceId, String entitySourceDescriptor, String dataSetName) {
		Assert.hasText(entitySourceId, "Entity source id cannot be empty.");
		Assert.hasText(entitySourceDescriptor, "Entity source descriptor cannot be empty.");
		Assert.hasText(dataSetName, "Dataset name cannot be empty.");

		Optional<DataSourceInstance> dataSource = dataSourceService.findDataSource(entitySourceId, entitySourceDescriptor, dataSetName);
		if(dataSource.isPresent()){
			return Optional.ofNullable(discoursePartRepo.findOne(
					DiscoursePartPredicates.discoursePartHasDataSource(dataSource.get())));			
		}else{
			return Optional.empty();
		}
	}
	
	/**
	 * Get the set of all discourse parts that do NOT contain a particular annotation type
	 * 
	 * @param badAnnotation
	 * @return a set of discourse part names
	 */
    public Set<DiscoursePart> findDiscoursePartsWithoutAnnotation(String badAnnotation) {
        Set<DiscoursePart> unannotated = new HashSet<DiscoursePart>();
        for(DiscoursePart dp : discoursePartRepo.findAll()) {
                boolean addme = true;
                AnnotationAggregate ag = dp.getAnnotations();
                if (ag != null) {
                        Set<AnnotationInstance> sai = ag.getAnnotations();
                        if (sai != null) {
                                for (AnnotationInstance ai : sai) {
                                        if (ai.getType() == badAnnotation) { addme = false; break; }
                                }
                        }
                }
                if (addme) { unannotated.add(dp); }
        }
        return unannotated;
    }

}
