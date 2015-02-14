package com.enonic.xp.portal.script.command;

import java.util.Map;

import com.enonic.wem.api.resource.ResourceKey;

// TODO: Rename to CommandContext and include setResult(..) and setResultAsJson(..)
public interface CommandRequest
{
    public String getName();

    public ResourceKey getScript();

    public CommandParam param( String name );

    public Map<String, Object> getParams();
}
