package org.eurocris.cerif.model;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.eurocris.cerif.CERIFClassScheme;

/**
 * Enumeration: CERIF Entity Types.
 */
@CERIFClassScheme( id="348ce6ee-43ef-4b71-aa77-a11ff988cae4", name="CERIF Entity Types" )
public enum CERIFEntityType {
	
	BASE_ENTITIES {
		public UUID getUuid() {
			return BASE_ENTITIES_UUID;
		}
		public String getTerm() {
			return "Base Entities";
		}
	},
	
	CLASSIFICATION {
		public UUID getUuid() {
			return CLASSIFICATION_UUID;
		}
		public String getTerm() {
			return "Classification";
		}
	},
	
	LINKING_ENTITIES {
		public UUID getUuid() {
			return LINKING_ENTITIES_UUID;
		}
		public String getTerm() {
			return "Linking Entities";
		}
	},
	
	MULTILINGUAL {
		public UUID getUuid() {
			return MULTILINGUAL_UUID;
		}
		public String getTerm() {
			return "Multilingual";
		}
	},
	
	RESULT_ENTITIES {
		public UUID getUuid() {
			return RESULT_ENTITIES_UUID;
		}
		public String getTerm() {
			return "Result Entities";
		}
	},
	
	ADDITIONAL_ENTITIES {
		public UUID getUuid() {
			return ADDITIONAL_ENTITIES_UUID;
		}
		public String getTerm() {
			return "Additional Entities";
		}
	},
	
	INFRASTRUCTURE_ENTITIES {
		public UUID getUuid() {
			return INFRASTRUCTURE_ENTITIES_UUID;
		}
		public String getTerm() {
			return "Infrastructure Entities";
		}
	},
	
	SECOND_ORDER_ENTITIES {
		public UUID getUuid() {
			return SECOND_ORDER_ENTITIES_UUID;
		}
		public String getTerm() {
			return "2nd Order Entities";
		}
	},
	
	INDICATORS_AND_MEASUREMENTS {
		public UUID getUuid() {
			return INDICATORS_AND_MEASUREMENTS_UUID;
		}
		public String getTerm() {
			return "Indicators & Measurements";
		}
	},
	
	DUBLIN_CORE {
		public UUID getUuid() {
			return DUBLIN_CORE_UUID;
		}
		public String getTerm() {
			return "Dublin Core";
		}
	},
	
	;

	/**
	 * Get the UUID that identifies the cfClass.
	 */
	public abstract UUID getUuid();
	
	/**
	 * Get the English term for the cfClass.
	 */
	public abstract String getTerm();
	
	private final static Map<UUID, CERIFEntityType> VALUE_BY_UUID = new HashMap<>();
	static {
		for ( final CERIFEntityType x : values() ) {
			VALUE_BY_UUID.put( x.getUuid(), x );
		}
	}
	
	/**
	 * Get the right entry by its UUID.
	 * @param uuid
	 * @return
	 */
	public static CERIFEntityType getByUuid( final UUID uuid ) {
		return VALUE_BY_UUID.get( uuid );
	}
	
	private static final UUID BASE_ENTITIES_UUID = UUID.fromString( "59fa2e25-4c00-4131-92bd-ad1c87bb867c" );
	private static final UUID CLASSIFICATION_UUID = UUID.fromString( "b854c3ae-270e-4fdd-a110-6494ae64c67a" );
	private static final UUID LINKING_ENTITIES_UUID = UUID.fromString( "af5cac09-e1db-49e1-98b4-e5677b7324ef" );
	private static final UUID MULTILINGUAL_UUID = UUID.fromString( "34011f15-8d84-4858-989f-a71490a9aeef" );
	private static final UUID RESULT_ENTITIES_UUID = UUID.fromString( "2902e5cf-9ae3-41bf-a043-d7d7ca99510a" );
	private static final UUID ADDITIONAL_ENTITIES_UUID = UUID.fromString( "a05cab00-b5c7-46df-9dde-e8e82dde46c6" );
	private static final UUID INFRASTRUCTURE_ENTITIES_UUID = UUID.fromString( "4e67698c-3316-441a-8f81-b60767bf5578" );
	private static final UUID SECOND_ORDER_ENTITIES_UUID = UUID.fromString( "69f7eebc-c27b-4b02-b2de-8e2b645669b2" );
	private static final UUID INDICATORS_AND_MEASUREMENTS_UUID = UUID.fromString( "3494420d-7b32-4815-83de-0229602da0b3" );
	private static final UUID DUBLIN_CORE_UUID = UUID.fromString( "66c54cf1-ee03-4746-8129-74bac9f8c6b4" );

}
