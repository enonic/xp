package com.enonic.wem.thymeleaf;

import java.util.Map;

import com.enonic.wem.api.resource.ResourceKey;

public interface ThymeleafProcessor
{
    public ThymeleafProcessor view( ResourceKey view );

    public ThymeleafProcessor parameters( Map<String, Object> parameters );

    public String process();
}
