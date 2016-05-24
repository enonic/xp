package com.enonic.xp.portal.impl.postprocess.instruction;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

final class Instruction
{
    private final String id;

    private final ImmutableMap<String, String> attributes;

    Instruction( final String id, Map<String, String> attributes )
    {
        this.id = id;
        this.attributes = ImmutableMap.copyOf( attributes );
    }

    String getId()
    {
        return id;
    }

    String attribute( final String name, final String defaultValue )
    {
        return attributes.getOrDefault( name, defaultValue );
    }

    String attribute( final String name )
    {
        return attributes.get( name );
    }

    Iterable<String> attributeNames()
    {
        return attributes.keySet();
    }

    static boolean isInstruction( final String instruction, final String id )
    {
        return instruction.startsWith( id + " " );
    }
}
