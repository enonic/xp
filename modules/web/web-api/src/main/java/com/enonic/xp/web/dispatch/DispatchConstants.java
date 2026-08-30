package com.enonic.xp.web.dispatch;

import java.util.List;

public final class DispatchConstants
{
    public static final String VIRTUAL_HOST_PREFIX = "@";

    /**
     * The web endpoint connector. The value is the legacy connector name, kept until XP 9.0.
     */
    public static final String WEB_CONNECTOR = "xp";

    /**
     * The management endpoint connector. The value is the legacy connector name, kept until XP 9.0.
     */
    public static final String MANAGEMENT_CONNECTOR = "api";

    /**
     * The statistics endpoint connector. The value is the legacy connector name, kept until XP 9.0.
     */
    public static final String STATISTICS_CONNECTOR = "status";

    /**
     * @deprecated Use {@link #WEB_CONNECTOR}.
     */
    @Deprecated
    public static final String XP_CONNECTOR = WEB_CONNECTOR;

    /**
     * @deprecated Use {@link #MANAGEMENT_CONNECTOR}.
     */
    @Deprecated
    public static final String API_CONNECTOR = MANAGEMENT_CONNECTOR;

    /**
     * @deprecated Use {@link #STATISTICS_CONNECTOR}.
     */
    @Deprecated
    public static final String STATUS_CONNECTOR = STATISTICS_CONNECTOR;

    public static final List<String> CONNECTORS = List.of( WEB_CONNECTOR, MANAGEMENT_CONNECTOR, STATISTICS_CONNECTOR );

    public static final String CONNECTOR_PROPERTY = "connector";

    public static final String CONNECTOR_ATTRIBUTE = "com.enonic.xp.web.dispatch.connector";

    private DispatchConstants()
    {
    }
}
