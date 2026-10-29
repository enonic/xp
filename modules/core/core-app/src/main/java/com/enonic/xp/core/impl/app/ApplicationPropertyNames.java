package com.enonic.xp.core.impl.app;

class ApplicationPropertyNames
{
    static final String MAX_SYSTEM_VERSION = "maxSystemVersion";

    static final String MIN_SYSTEM_VERSION = "minSystemVersion";

    static final String VENDOR_NAME = "vendorName";

    static final String MODIFIED_TIME = "modifiedTime";

    static final String VERSION = "version";

    static final String STARTED = "started";

    /**
     * How many times the persisted schema of the application was changed through the schema API since it was installed or
     * created: {@code 0} for a schema as the bundle shipped it, or as the application was created without one. Every write of it
     * gives the application node a new version, which is what every reader checks its cached schema against.
     */
    static final String SCHEMA_REVISION = "schemaRevision";
}
