package com.enonic.xp.lib.repo;

import java.util.Collection;
import java.util.Map;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.util.BinaryAttachment;

class RepositoryBinaryAttachmentsParser
{
    private final ImmutableList.Builder<BinaryAttachment> binaryAttachmentsBuilder = ImmutableList.builder();

    public ImmutableList<BinaryAttachment> parse( final ScriptValue value )
    {
        final Map<String, Object> map = value.getMap();

        handleMap( map );

        return this.binaryAttachmentsBuilder.build();
    }

    private void handleElement( final Object value )
    {
        if ( value instanceof Map )
        {
            handleMap( (Map) value );
        }
        else if ( value instanceof Collection )
        {
            handleArray( (Collection) value );
        }
        else
        {
            handleValue( value );
        }
    }

    private void handleMap( final Map map )
    {
        for ( final Object key : map.keySet() )
        {
            handleElement( map.get( key ) );
        }
    }

    private void handleArray( final Collection values )
    {
        for ( final Object value : values )
        {
            handleElement( value );
        }
    }

    private void handleValue( final Object value )
    {
        if ( value instanceof BinaryAttachment )
        {
            final BinaryAttachment binaryAttachment = (BinaryAttachment) value;
            // for security reasons don't let subclasses to sneak into list. Obsolete when BinaryAttachment class declared final
            final BinaryAttachment copy = new BinaryAttachment( binaryAttachment.getReference(), binaryAttachment.getByteSource() );
            this.binaryAttachmentsBuilder.add( copy );
        }
    }
}
