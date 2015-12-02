package com.enonic.xp.web.impl.dos;

public @interface DosFilterConfig
{
    boolean enabled() default false;

    int maxRequestsPerSec() default 25;

    long delayMs() default 100;

    long maxWaitMs() default 50;

    int throttledRequests() default 5;

    long throttleMs() default 30000;

    long maxRequestMs() default 30000;

    long maxIdleTrackerMs() default 30000;

    boolean insertHeaders() default true;

    boolean trackSessions() default true;

    boolean remotePort() default false;

    String ipWhitelist() default "";
}
