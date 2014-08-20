package com.enonic.wem.thymeleaf;

import java.util.Map;

import com.enonic.wem.api.resource.ResourceKey;

public interface ThymeleafProcessor
{
    public String process( ResourceKey view, Map<String, Object> params );
}
