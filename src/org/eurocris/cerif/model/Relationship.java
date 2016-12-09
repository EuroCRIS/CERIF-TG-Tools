package org.eurocris.cerif.model;

import java.util.UUID;

import org.eurocris.cerif.CERIFClassScheme;

@CERIFClassScheme( id="fd0791d1-570b-4b7a-825f-99679a3a29cf", name="" )
public class Relationship {

	private final UUID uuid;
	private final String name;
	private final PrimaryKey pk;
	private final ForeignKey fk;
	private final String notes;
	private final String comments;

	public Relationship( final UUID uuid, final String name, final PrimaryKey pk, final ForeignKey fk, final String notes, final String comments ) {
		this.uuid = uuid;
		this.name = name;
		this.pk = pk;
		this.fk = fk;
		this.notes = notes;
		this.comments = comments;
	}

	public UUID getUuid() {
		return uuid;
	}
	
	public String getName() {
		return name;
	}

	public PrimaryKey getPk() {
		return pk;
	}

	public ForeignKey getFk() {
		return fk;
	}

	public Entity getParentEntity() {
		return pk.getEntity();
	}
	
	public Entity getChildEntity() {
		return fk.getEntity();
	}
	
	public String getNotes() {
		return notes;
	}
	
	public String getComments() {
		return comments;
	}
	
}
