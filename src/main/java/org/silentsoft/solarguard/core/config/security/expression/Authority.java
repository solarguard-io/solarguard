package org.silentsoft.solarguard.core.config.security.expression;

public class Authority {

    public static final String BROWSER_API = "BROWSER_API";
    public static final String PERSONAL_API = "PERSONAL_API";
    public static final String PRODUCT_API = "PRODUCT_API";

    public class Has {

        public static final String Admin = "hasAuthority('ADMIN')";

    }

    public class Allow {

        public static final String BROWSER_API = "hasAuthority('BROWSER_API')";
        public static final String PRODUCT_API = "hasAuthority('PRODUCT_API')";

    }

    public class Deny {

        public static final String PRODUCT_API = "!hasAuthority('PRODUCT_API')";

    }

}
