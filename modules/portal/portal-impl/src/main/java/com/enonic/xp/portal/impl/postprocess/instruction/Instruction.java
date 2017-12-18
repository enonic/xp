package com.enonic.xp.portal.impl.postprocess.instruction;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ListMultimap;

final class Instruction
{
    private final String id;

    private final ImmutableListMultimap<String, String> attributes;

    Instruction( final String id, ListMultimap<String, String> attributes )
    {
        this.id = id;
        this.attributes = ImmutableListMultimap.copyOf( attributes );
    }

    String getId()
    {
        return id;
    }

    ImmutableList<String> attributes( final String name )
    {
        return attributes.get( name );
    }

    String attribute( final String name )
    {
        final ImmutableList<String> attrs = attributes.get( name );
        return attrs.isEmpty() ? null : attrs.get( 0 );
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
