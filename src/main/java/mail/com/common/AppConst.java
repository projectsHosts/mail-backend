package mail.com.common;

public class AppConst {
    // Authentication / Login
    public static final String INVALID_CREDENTIALS = "Invalid credentials";
    public static final String USER_NOT_FOUND = "User not found";
    public static final String EMAIL_NOT_FOUND = "Email not found";
    public static final String PASSWORD_INCORRECT = "Incorrect password";
    public static final String ACCOUNT_DISABLED = "Account is disabled";
    public static final String ACCOUNT_LOCKED = "Account is locked";
    public static final String TOKEN_EXPIRED = "Session expired. Please login again";
    public static final String TOKEN_INVALID = "Invalid authentication token";
    public static final String UNAUTHORIZED = "You are not authorized to access this resource";

    // Signup
    public static final String ALREADY_REGISTERED = "Email already registered";
    public static final String USERNAME_TAKEN = "Username already taken";
    public static final String WEAK_PASSWORD = "Password is too weak";
    public static final String INVALID_EMAIL = "Invalid email format";
    public static final String MISSING_REQUIRED_FIELDS = "Required fields are missing";

    // Validation
    public static final String INVALID_INPUT = "Invalid input data";
    public static final String INVALID_ID = "Invalid ID provided";
    public static final String INVALID_FORMAT = "Invalid data format";
    public static final String FIELD_TOO_SHORT = "Field value is too short";
    public static final String FIELD_TOO_LONG = "Field value is too long";
    public static final String FIELD_CANNOT_BE_EMPTY = "Field cannot be empty";

    // Database
    public static final String DATABASE_ERROR = "Database error occurred";
    public static final String DUPLICATE_ENTRY = "Duplicate entry found";
    public static final String RECORD_NOT_FOUND = "Record not found";
    public static final String FAILED_TO_SAVE = "Failed to save data";
    public static final String FAILED_TO_UPDATE = "Failed to update record";
    public static final String FAILED_TO_DELETE = "Failed to delete record";

    // Token / JWT
    public static final String JWT_INVALID = "Invalid JWT token";
    public static final String JWT_EXPIRED = "JWT token expired";
    public static final String JWT_MISSING = "JWT token is missing";
    public static final String AUTHENTICATION_FAILED = "Authentication failed";

    // Server
    public static final String INTERNAL_SERVER_ERROR = "Internal server error";
    public static final String SERVICE_UNAVAILABLE = "Service unavailable";
    public static final String REQUEST_FAILED = "Request failed";
    public static final String UNKNOWN_ERROR = "Something went wrong";
}
