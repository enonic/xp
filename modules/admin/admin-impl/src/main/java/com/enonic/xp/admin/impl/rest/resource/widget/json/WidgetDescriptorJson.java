package com.enonic.xp.admin.impl.rest.resource.widget.json;

import java.util.Map;
import java.util.Set;

public final class WidgetDescriptorJson
{
    public String key;

    public String displayName;

    public String url;

    public Set<String> interfaces;

    public Map<String, String> config;
}
