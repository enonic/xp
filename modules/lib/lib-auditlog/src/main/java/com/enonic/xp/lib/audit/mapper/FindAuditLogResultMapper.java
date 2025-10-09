package com.enonic.xp.lib.audit.mapper;

import com.enonic.xp.audit.FindAuditLogResult;
import com.enonic.xp.script.serializer.MapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;

public class FindAuditLogResultMapper
    implements MapSerializable
{
    private final FindAuditLogResult result;

    public FindAuditLogResultMapper( final FindAuditLogResult result )
    {
        this.result = result;
    }

    @Override
    public void serialize( final MapGenerator gen )
    {
        gen.value( "total", result.getTotal() );
        gen.value( "count", result.getHits().getSize() );
        gen.array( "hits" );
        result.getHits().forEach( hit -> {
            gen.map();
            AuditLogMapper.serializeAuditLog( gen, hit );
            gen.end();
        } );
        gen.end();
    }
}
