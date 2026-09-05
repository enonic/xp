package com.enonic.xp.web.dispatch;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ResourceMapping<T>
{
    String getName();

    List<String> getConnectors();

    int getOrder();

    /**
     * @deprecated Init parameters are not passed to the filter or servlet. Configure the resource in the
     * {@code @Activate} method of its component instead. Scheduled for removal in XP 9.0.
     */
    @Deprecated(since = "8.2", forRemoval = true)
    Map<String, String> getInitParams();

    Set<String> getUrlPatterns();

    T getResource();
}
