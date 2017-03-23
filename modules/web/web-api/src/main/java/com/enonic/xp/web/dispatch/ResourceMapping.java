package com.enonic.xp.web.dispatch;

import java.util.Map;
import java.util.Set;

public interface ResourceMapping<T>
{
    String getName();

    int getOrder();

    Map<String, String> getInitParams();

    Set<String> getUrlPatterns();

    T getResource();
}
