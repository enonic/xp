package com.enonic.xp.node;

@Deprecated( since = "8" )
public interface DuplicateNodeProcessor
{
    CreateNodeParams process( CreateNodeParams originalParams );
}
