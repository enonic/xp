package com.enonic.xp.lib.node;

import java.util.Collection;
import java.util.Map;

import com.enonic.xp.node.BinaryAttachment;
import com.enonic.xp.node.BinaryAttachments;
import com.enonic.xp.script.ScriptValue;

class BinaryAttachmentsParser
{
    private final BinaryAttachments.Builder binaryAttachmentsBuilder = BinaryAttachments.create();

    public BinaryAttachments parse( final ScriptValue value )
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
            this.binaryAttachmentsBuilder.add( new BinaryAttachment( binaryAttachment.getReference(), binaryAttachment.getByteSource() ) );
        }
    }
}
