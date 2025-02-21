package sn.zahra.service.exception;

public class BadRequestException extends RuntimeException {
    private String entityName;
    private String errorKey;

    // Constructeur
    public BadRequestException(String message, String entityName, String errorKey) {
        super(message);
        this.entityName = entityName;
        this.errorKey = errorKey;
    }

    // Getter pour l'entité
    public String getEntityName() {
        return entityName;
    }

    // Getter pour la clé d'erreur
    public String getErrorKey() {
        return errorKey;
    }
}
