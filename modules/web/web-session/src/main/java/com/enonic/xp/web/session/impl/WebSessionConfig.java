package com.enonic.xp.web.session.impl;

public @interface WebSessionConfig
{
    int retries() default 3;

    int eviction_max_size() default 1000;

    String write_sync_mode() default "full";

    String cache_mode() default "replicated";

    int cache_replicas() default 1;

    boolean cache_stats_enabled() default true;

    int session_save_period() default 10;
    
    int write_timeout() default 10_000;
}
