package org.eurocris.cerif.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.eurocris.cerif.CERIFClassScheme;

@CERIFClassScheme( id="6e0d9af0-1cd6-11e1-8bc2-0800200c9a66", name="CERIF Entities" )
public class Entity {

	private final UUID uuid;
	private final String physicalName;
	private final String logicalName;
	private final String term;
	private final String notes;
	private final String comments;
	private final CERIFEntityType entityType;
	private final List<Attribute> attributesList = new ArrayList<>();
	private final PrimaryKey primaryKey;
	private final List<ForeignKey> foreignKeys = new ArrayList<>();
	
	public Entity( final UUID uuid, final String physicalName, final String logicalName, final CERIFEntityType entityType, final String term, final String notes, final String comments, final String pkName ) {
		this.uuid = uuid;
		this.physicalName = physicalName;
		this.logicalName = logicalName;
		this.entityType = entityType;
		this.term = term;
		this.notes = notes;
		this.comments = comments;
		this.primaryKey = new PrimaryKey( this, pkName );
	}

	public UUID getUuid() {
		return uuid;
	}

	public String getPhysicalName() {
		return physicalName;
	}

	public String getLogicalName() {
		return logicalName;
	}

	public CERIFEntityType getEntityType() {
		return entityType;
	}
	
	public String getTerm() {
		return term;
	}

	public String getNotes() {
		return notes;
	}

	public String getComments() {
		return comments;
	}

	public List<Attribute> getAttributes() {
		return attributesList ;
	}
	
	public PrimaryKey getPrimaryKey() {
		return primaryKey;
	}

	public List<ForeignKey> getForeignKeys() {
		return foreignKeys;
	}
	
}
