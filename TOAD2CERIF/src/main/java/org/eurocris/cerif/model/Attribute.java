package org.eurocris.cerif.model;

import java.util.UUID;

import org.eurocris.cerif.CERIFClassScheme;

@CERIFClassScheme( id="318118e8-d323-4e3b-9882-a17c635e9c58", name="CERIF Attributes" )
public class Attribute {

	private final Entity entity;
	private final UUID uuid;
	private final String physicalName;
	private final String logicalName;
	private final String notes;
	private final String comments;
	
	public Attribute( final Entity entity, final UUID uuid, final String physicalName, final String logicalName, final String notes, final String comments ) {
		( this.entity = entity ).getAttributes().add( this );
		this.uuid = uuid;
		this.physicalName = physicalName;
		this.logicalName = logicalName;
		this.notes = notes;
		this.comments = comments;
	}

	public Entity getEntity() {
		return entity;
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

	public String getTerm() {
		return getPhysicalName();
	}

	public String getNotes() {
		return notes;
	}
	
	public String getComments() {
		return comments;
	}

}
