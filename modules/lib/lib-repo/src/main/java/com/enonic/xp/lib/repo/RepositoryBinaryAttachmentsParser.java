package com.enonic.xp.lib.repo;

import java.util.Collection;
import java.util.Map;

import com.enonic.xp.repository.RepositoryBinaryAttachment;
import com.enonic.xp.repository.RepositoryBinaryAttachments;
import com.enonic.xp.script.ScriptValue;

class RepositoryBinaryAttachmentsParser
{
    private final RepositoryBinaryAttachments.Builder binaryAttachmentsBuilder = RepositoryBinaryAttachments.create();

    public RepositoryBinaryAttachments parse( final ScriptValue value )
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
        if ( value instanceof RepositoryBinaryAttachment )
        {
            final RepositoryBinaryAttachment binaryAttachment = (RepositoryBinaryAttachment) value;
            final RepositoryBinaryAttachment attachedBinary =
                new RepositoryBinaryAttachment( binaryAttachment.getReference(), binaryAttachment.getByteSource() );
            this.binaryAttachmentsBuilder.add( attachedBinary );
        }
    }
}
