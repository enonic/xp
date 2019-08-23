package com.enonic.xp.lib.audit.mapper;

import com.enonic.xp.audit.AuditLog;
import com.enonic.xp.audit.AuditLogUri;
import com.enonic.xp.audit.AuditLogUris;
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
        serializeAuditLog( gen, auditLog );
    }

    static void serializeAuditLog( MapGenerator gen, AuditLog auditLog )
    {
        gen.value( "_id", auditLog.getId() );
        gen.value( "type", auditLog.getType() );
        gen.value( "time", auditLog.getTime() );
        gen.value( "source", auditLog.getSource() );
        gen.value( "user", auditLog.getUser() );
        gen.value( "message", auditLog.getMessage() );
        serializeObjectUris( gen, auditLog.getObjectUris() );
        serializeData( gen, auditLog.getData() );
    }

    private static void serializeObjectUris( final MapGenerator gen, final AuditLogUris uris )
    {
        gen.array( "objects" );
        for ( final AuditLogUri value : uris )
        {
            gen.value( value.toString() );
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
