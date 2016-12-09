package org.eurocris.cerif.model;

import java.util.ArrayList;
import java.util.List;

public abstract class Key {

	private final Entity entity;
	private final String name;
	private final List<Attribute> attributes = new ArrayList<>();
	
	protected Key( final Entity entity, final String name ) {
		this.entity = entity;
		this.name = name;
	}
	
	public Entity getEntity() {
		return entity;
	}
	
	public String getName() {
		return name;
	}
	
	public List<Attribute> getAttributes() {
		return attributes;
	}
	
}
