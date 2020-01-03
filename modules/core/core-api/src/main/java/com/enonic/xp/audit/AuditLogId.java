package com.enonic.xp.audit;


import java.util.Objects;

import com.google.common.base.Preconditions;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public class AuditLogId
    extends com.enonic.xp.node.UUID
{
    public AuditLogId()
    {
        super();
    }

    private AuditLogId( final String value )
    {
        super( value );
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }

        final AuditLogId other = (AuditLogId) o;
        return Objects.equals( value, other.value );
    }

    public static AuditLogId from( String string )
    {
        return new AuditLogId( string );
    }

    public static AuditLogId from( Object object )
    {
        Preconditions.checkNotNull( object, "object cannot be null" );
        return AuditLogId.from( object.toString() );
    }
}
