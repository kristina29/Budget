package controller;

/**
 * Exception für ungültige Eingaben.
 */
class InputInvalidException extends Exception {

	private static final long serialVersionUID = 1L;
	
	/**
	 * Konstruktor.
	 */
	public InputInvalidException() {
		super();
	}
	
	/**
	 * Konstruktor mit Nachricht.
	 */
	public InputInvalidException(String message) {
		super(message);
	}

}
