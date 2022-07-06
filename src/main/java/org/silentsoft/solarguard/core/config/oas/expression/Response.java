package org.silentsoft.solarguard.core.config.oas.expression;

public class Response {

    public class Code {
        public static final String OK = "200";
        public static final String CREATED = "201";
        public static final String NO_CONTENT = "204";
        public static final String BAD_REQUEST = "400";
        public static final String FORBIDDEN = "403";
        public static final String NOT_FOUND = "404";
        public static final String UNPROCESSABLE_ENTITY = "422";
        public static final String PRECONDITION_REQUIRED = "428";
    }

    public class Description {
        private static final String RESPONSE_IF = "Response if ";

        public static final String USER_IS_NOT_EXISTS = RESPONSE_IF + "user is not exists";
        public static final String USER_IS_NOT_AN_ADMIN = RESPONSE_IF + "user is not an admin";
        public static final String USER_IS_NOT_A_MEMBER_OF_ORGANIZATION = RESPONSE_IF + "user is not a member of organization";
        public static final String USER_HAS_NO_PERMISSION = RESPONSE_IF + "user has no permission";
        public static final String USER_HAS_NOT_STAFF_ROLE_IN_ORGANIZATION = RESPONSE_IF + "user has not staff role in organization";
        public static final String ORGANIZATION_IS_NOT_EXISTS = RESPONSE_IF + "organization is not exists";
        public static final String PRODUCT_API_IS_NOT_ALLOWED = RESPONSE_IF + "you don't have the proper authority, or it's called through the product API";
        public static final String PRODUCT_IS_NOT_EXISTS = RESPONSE_IF + "product is not exists";
        public static final String PRODUCT_TOKEN_IS_NOT_EXISTS = RESPONSE_IF + "product token is not exists";
        public static final String PERSONAL_TOKEN_IS_NOT_EXISTS = RESPONSE_IF + "personal token is not exists";
        public static final String PACKAGE_IS_NOT_EXISTS = RESPONSE_IF + "package is not exists";
        public static final String FAILED_TO_CREATE_USER = RESPONSE_IF + "failed to create user";
        public static final String FAILED_TO_EDIT_USER = RESPONSE_IF + "failed to edit user";
        public static final String FAILED_TO_DELETE_USER = RESPONSE_IF + "failed to delete user";
        public static final String FAILED_TO_CREATE_ORGANIZATION = RESPONSE_IF + "failed to create organization";
        public static final String FAILED_TO_UPDATE_ORGANIZATION = RESPONSE_IF + "failed to update organization";
        public static final String FAILED_TO_ADD_MEMBER_TO_ORGANIZATION = RESPONSE_IF + "failed to add member to organization";
        public static final String FAILED_TO_REMOVE_MEMBER_FROM_ORGANIZATION = RESPONSE_IF + "failed to remove member from organization";
        public static final String FAILED_TO_ADD_PRODUCT_TO_ORGANIZATION = RESPONSE_IF + "failed to add product to organization";
        public static final String FAILED_TO_UPDATE_PRODUCT = RESPONSE_IF + "failed to update product";
        public static final String FAILED_TO_CREATE_PRODUCT_TOKEN = RESPONSE_IF + "failed to create product token";
        public static final String FAILED_TO_UPDATE_PRODUCT_TOKEN = RESPONSE_IF + "failed to update product token";
        public static final String FAILED_TO_ADD_PACKAGE_TO_ORGANIZATION = RESPONSE_IF + "failed to add package to organization";
        public static final String FAILED_TO_CHECK_LICENSE_KEY = RESPONSE_IF + "license key does not exist or is invalid or does not have access authority";
    }

}
