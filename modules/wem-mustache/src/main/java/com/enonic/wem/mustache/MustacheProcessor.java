package com.enonic.wem.mustache;

import java.util.Map;

import com.enonic.wem.api.resource.ResourceKey;

public interface MustacheProcessor
{
    public MustacheProcessor view( ResourceKey view );

    public MustacheProcessor parameters( Map<String, Object> parameters );

    public String process();
}
