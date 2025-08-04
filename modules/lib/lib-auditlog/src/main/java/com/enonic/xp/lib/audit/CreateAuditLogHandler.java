package com.enonic.xp.lib.audit;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;

import com.enonic.xp.audit.AuditLog;
import com.enonic.xp.audit.AuditLogUri;
import com.enonic.xp.audit.AuditLogUris;
import com.enonic.xp.audit.LogAuditLogParams;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.lib.audit.mapper.AuditLogMapper;
import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.security.PrincipalKey;

public class CreateAuditLogHandler
    extends BaseAuditLogHandler
{
    private String type;

    private Instant time;

    private String source;

    private PrincipalKey user;

    private AuditLogUris objectUris;

    private PropertyTree data;

    @Override
    protected Object doExecute()
    {
        AuditLog log = this.auditLogService.log( LogAuditLogParams.create().
            type( this.type ).
            time( this.time ).
            source( this.source ).
            user( this.user ).
            objectUris( this.objectUris ).
            data( this.data ).
            build() );
        return new AuditLogMapper( log );
    }

    public void setType( final String type )
    {
        this.type = type;
    }

    public void setTime( final String time )
    {
        this.time = time != null ? Instant.parse( time ) : null;
    }

    public void setSource( final String source )
    {
        this.source = source;
    }

    public void setUser( final String user )
    {
        this.user = user != null ? PrincipalKey.from( user ) : null;
    }

    public void setObjectUris( final ScriptValue objectUris )
    {
        if ( objectUris == null || objectUris.getList() == null )
        {
            return;
        }
        this.objectUris = objectUris.getList().
            stream().map( Object::toString ).map( AuditLogUri::from ).
            collect( AuditLogUris.collector() );
    }

    public void setData( final ScriptValue data )
    {
        this.data = PropertyTree.fromMap( Optional.ofNullable( data ).map( ScriptValue::getMap ).orElse( Map.of() ) );
    }

    @Override
    public void initialize( final BeanContext context )
    {
        super.initialize( context );
    }
}
