package org.eurocris.cerif.model;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class Model {

	private final Map<UUID, Entity> entitiesByUuid = new LinkedHashMap<>();
	private final Map<UUID, Attribute> attributesByUuid = new LinkedHashMap<>();
	private final Map<UUID, Relationship> relationshipsByUuid = new LinkedHashMap<>();
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
	
	public Entity getEntityBy( final UUID uuid ) {
		return entitiesByUuid.get( uuid );
	}
	
	public Attribute getAttributeBy( final UUID uuid ) {
		return attributesByUuid.get( uuid );
	}
	
	public Relationship getRelationshipBy( final UUID uuid ) {
		return relationshipsByUuid.get( uuid );
	}
	
	public void add( final Entity entity ) {
		entitiesByUuid.put( entity.getUuid(), entity );
	}
	
	public void add( final Attribute attribute ) {
		attributesByUuid.put( attribute.getUuid(), attribute );
	}
	
	public void add( final Relationship relationship ) {
		relationshipsByUuid.put( relationship.getUuid(), relationship );
	}
	
}
