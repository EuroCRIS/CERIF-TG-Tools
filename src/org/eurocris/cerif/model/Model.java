package org.eurocris.cerif.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class Model {

	private final Map<UUID, Entity> entitiesByUuid = new LinkedHashMap<>();
	private final Map<String, Entity> entitiesByPhysicalName = new HashMap<>();
	private final Map<UUID, Attribute> attributesByUuid = new LinkedHashMap<>();
	private final Map<String, Attribute> attributesByPhysicalName = new HashMap<>();
	private final Map<UUID, Relationship> relationshipsByUuid = new LinkedHashMap<>();
	private final Map<UUID, String> categoryLabelsByUuid = new LinkedHashMap<>();
	private final String modifiedDate;
	private final String title;
	
	public Model( final String modifiedDate, final String title ) {
		super();
		this.modifiedDate = modifiedDate;
		this.title = title;
	}

	public String getModifiedDate() {
		return modifiedDate;
	}

	public String getTitle() {
		return title;
	}

	public Iterable<Entity> iterableEntities() {
		return Collections.unmodifiableCollection( entitiesByUuid.values() );
	}
	
	public Iterable<Attribute> iterableAttributes() {
		return Collections.unmodifiableCollection( attributesByUuid.values() );
	}
	
	public Iterable<Relationship> iterableRelationships() {
		return Collections.unmodifiableCollection( relationshipsByUuid.values() );
	}
	
	public Iterable<Map.Entry<UUID, String>> iterableCategories() {
		return Collections.unmodifiableMap( categoryLabelsByUuid ).entrySet();
	}
	
	public Entity getEntityBy( final UUID uuid ) {
		return entitiesByUuid.get( uuid );
	}
	
	public Entity getEntityBy( final String physicalName ) {
		return entitiesByPhysicalName.get( physicalName );
	}
	
	public Attribute getAttributeBy( final UUID uuid ) {
		return attributesByUuid.get( uuid );
	}
	
	public Attribute getAttributeBy( final String physicalName ) {
		return attributesByPhysicalName.get( physicalName );
	}
	
	public Relationship getRelationshipBy( final UUID uuid ) {
		return relationshipsByUuid.get( uuid );
	}
	
	public void add( final Entity entity ) {
		entitiesByUuid.put( entity.getUuid(), entity );
		entitiesByPhysicalName.put( entity.getPhysicalName(), entity );
	}
	
	public void add( final Attribute attribute ) {
		attributesByUuid.put( attribute.getUuid(), attribute );
		attributesByPhysicalName.put( attribute.getPhysicalName(), attribute );
	}
	
	public void add( final Relationship relationship ) {
		relationshipsByUuid.put( relationship.getUuid(), relationship );
	}
	
	public void addCategory( final UUID uuid, final String label ) {
		categoryLabelsByUuid.put( uuid, label );
	}
	
}
