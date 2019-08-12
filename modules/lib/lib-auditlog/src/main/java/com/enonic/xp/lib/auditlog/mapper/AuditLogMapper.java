package com.enonic.xp.lib.auditlog.mapper;

import java.net.URI;

import com.google.common.collect.ImmutableSet;

import com.enonic.xp.auditlog.AuditLog;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.lib.common.PropertyTreeMapper;
import com.enonic.xp.script.serializer.MapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;

public class AuditLogMapper
    implements MapSerializable
{

    private final AuditLog auditLog;

    public AuditLogMapper( final AuditLog auditLog )
    {
        this.auditLog = auditLog;
    }

    @Override
    public void serialize( final MapGenerator gen )
    {
        gen.value( "_id", auditLog.getId() ); // TODO: Should this be _id like content or just id
        gen.value( "type", auditLog.getType() );
        gen.value( "time", auditLog.getTime() );
        gen.value( "source", auditLog.getSource() );
        gen.value( "user", auditLog.getUser() );
        gen.value( "message", auditLog.getMessage() );
        serializeObjectUris( gen, auditLog.getObjectUris() );
        serializeData( gen, auditLog.getData() );
    }

    private static void serializeObjectUris( final MapGenerator gen, final ImmutableSet<URI> list )
    {
        gen.array( "objectUris" );
        for ( final URI value : list )
        {
            gen.value( value );
        }
        gen.end();
    }

    private static void serializeData( final MapGenerator gen, final PropertyTree value )
    {
        gen.map( "data" );
        new PropertyTreeMapper( value ).serialize( gen );
        gen.end();
    }
}
