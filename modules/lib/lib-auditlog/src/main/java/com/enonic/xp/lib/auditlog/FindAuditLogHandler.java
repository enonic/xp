package com.enonic.xp.lib.auditlog;

import java.time.Instant;
import java.util.stream.Collectors;

import com.enonic.xp.audit.AuditLogIds;
import com.enonic.xp.audit.FindAuditLogParams;
import com.enonic.xp.lib.auditlog.mapper.FindAuditLogResultMapper;
import com.enonic.xp.script.ScriptValue;

public class FindAuditLogHandler
    extends BaseAuditLogHandler
{

    private Integer start;

    private Integer count;

    private AuditLogIds ids;

    private Instant from;

    private Instant to;

    private String type;

    private String source;

    @Override
    protected Object doExecute()
    {
        return new FindAuditLogResultMapper( this.auditLogService.find( FindAuditLogParams.
            create().
            ids( ids ).
            from( from ).
            to( to ).
            type( type ).
            source( source ).
            count( count ).
            start( start ).
            build() ) );
    }

    public void setStart( final Integer start )
    {
        this.start = start;
    }

    public void setCount( final Integer count )
    {
        this.count = count;
    }

    public void setIds( final ScriptValue ids )
    {
        if ( ids == null || ids.getList() == null )
        {
            return;
        }
        this.ids = AuditLogIds.from( ids.getList().stream().map( o -> o.toString() ).collect( Collectors.toList() ) );
    }

    public void setFrom( final String from )
    {
        this.from = from != null ? Instant.parse( from ) : null;
    }

    public void setTo( final String to )
    {
        this.to = to != null ? Instant.parse( to ) : null;
    }

    public void setType( final String type )
    {
        this.type = type;
    }

    public void setSource( final String source )
    {
        this.source = source;
    }
}
