package de.osmembrane.model.settings;

/**
 * Exception is thrown if a object in the Settings-model was not parsable.
 * 
 * @author jakob_jarosch
 */
public class UnparsableFormatException extends Exception {
	
	private static final long serialVersionUID = 2011020220530001L;
	
	private SettingType type;

	/**
	 * Creates a new {@link UnparsableFormatException}.
	 * 
	 * @param type type of the unparsable object
	 */
	public UnparsableFormatException(SettingType type) {
		this.type = type;
	}
	
	/**
	 * Returns the type of the unparsable object.
	 * 
	 * @return type of the unparsable object
	 */
	public SettingType getType() {
		return type;
	}
}