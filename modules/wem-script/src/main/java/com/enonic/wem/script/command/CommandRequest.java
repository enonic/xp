package com.enonic.wem.script.command;

import java.util.Map;

import com.enonic.wem.api.resource.ResourceKey;

public interface CommandRequest
{
    public String getName();

    public ResourceKey getScript();

    public CommandParam param( String name );

    public Map<String, Object> paramsMap();
}
