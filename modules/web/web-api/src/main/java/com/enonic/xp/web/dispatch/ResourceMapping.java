package com.enonic.xp.web.dispatch;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ResourceMapping<T>
{
    String getName();

    List<String> getConnectors();

    int getOrder();

    Map<String, String> getInitParams();

    Set<String> getUrlPatterns();

    T getResource();
}
