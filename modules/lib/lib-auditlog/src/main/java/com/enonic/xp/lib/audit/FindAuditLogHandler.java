package com.enonic.xp.lib.audit;

import java.time.Instant;

import com.enonic.xp.audit.AuditLogId;
import com.enonic.xp.audit.AuditLogIds;
import com.enonic.xp.audit.AuditLogUri;
import com.enonic.xp.audit.AuditLogUris;
import com.enonic.xp.audit.FindAuditLogParams;
import com.enonic.xp.audit.FindAuditLogResult;
import com.enonic.xp.lib.audit.mapper.FindAuditLogResultMapper;
import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;

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

    private PrincipalKeys users;

    private AuditLogUris objectUris;

    @Override
    protected Object doExecute()
    {
        final FindAuditLogParams params = FindAuditLogParams.
            create().
            ids( ids ).
            from( from ).
            to( to ).
            type( type ).
            source( source ).
            count( count ).
            start( start ).
            users( users ).
            objectUris( objectUris ).
            build();
        final FindAuditLogResult result = auditLogService.find( params );
        return new FindAuditLogResultMapper( result );
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
        this.ids = ids.getList().stream().map( Object::toString ).map( AuditLogId::from ).collect( AuditLogIds.collector() );
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

    public void setUsers( final ScriptValue users )
    {
        if ( users == null || users.getList() == null )
        {
            return;
        }
        this.users = users.getList().
            stream().map( o -> PrincipalKey.from( o.toString() ) ).
            collect( PrincipalKeys.collector() );
    }

    public void setObjectUris( final ScriptValue objectUris )
    {
        if ( objectUris == null || objectUris.getList() == null )
        {
            return;
        }
        this.objectUris = objectUris.getList().
            stream().map( o -> AuditLogUri.from( o.toString() ) ).
            collect( AuditLogUris.collector() );
    }
}
