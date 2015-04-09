package com.enonic.xp.portal.script.command;

import java.util.Map;

import com.google.common.annotations.Beta;

import com.enonic.xp.resource.ResourceKey;

@Beta
public interface CommandRequest
{
    String getName();

    ResourceKey getScript();

    CommandParam param( String name );

    Map<String, Object> getParams();
}
