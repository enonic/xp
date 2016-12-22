package com.enonic.xp.trace;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;

public interface Trace
    extends Map<String, Object>
{
    String getId();

    String getParentId();

    String getType();

    TraceLocation getLocation();

    Instant getStartTime();

    Instant getEndTime();

    boolean inProgress();

    Duration getDuration();

    void start();

    void end();
}
