package com.enonic.xp.audit;


import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class AuditLogId
    extends com.enonic.xp.node.UUID
{
    public AuditLogId()
    {
        super();
    }

    private AuditLogId( final Object object )
    {
        super( object );
    }

    @Override
    public boolean equals( final Object o )
    {
        return super.equals( o );
    }

    public static AuditLogId from( final String string )
    {
        return new AuditLogId( string );
    }

    public static AuditLogId from( final Object object )
    {
        return new AuditLogId( object );
    }
}
