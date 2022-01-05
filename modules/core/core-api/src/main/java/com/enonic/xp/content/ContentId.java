package com.enonic.xp.content;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.node.UUID;
import com.enonic.xp.util.Reference;

@PublicApi
public final class ContentId extends UUID
{
    private ContentId( final String id )
    {
        super(id);
    }

    private ContentId( final UUID id )
    {
        super(id);
    }

    @Override
    public boolean equals( final Object o )
    {
        return super.equals( o );
    }

    @Override
    public int hashCode()
    {
        return super.hashCode();
    }

    @Override
    public String toString()
    {
        return super.toString();
    }

    public static ContentId from( final String id )
    {
        return new ContentId( id );
    }

    public static ContentId from( final Reference reference )
    {
        return new ContentId( reference.getNodeId() );
    }
}
