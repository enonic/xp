package com.enonic.xp.lib.auditlog;

import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableSet;

import com.enonic.xp.auditlog.AuditLog;
import com.enonic.xp.auditlog.AuditLogParams;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.lib.auditlog.mapper.AuditLogMapper;
import com.enonic.xp.lib.common.FormJsonToPropertyTreeTranslator;
import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.security.PrincipalKey;

public class CreateAuditLogHandler
    extends BaseAuditLogHandler
{
    private String type;

    private Instant time;

    private String source;

    private PrincipalKey user;

    private String message;

    private ImmutableSet<URI> objectUris;

    private PropertyTree data;

    @Override
    protected Object doExecute()
    {
        AuditLog log = this.auditLogService.log( AuditLogParams.create().
            type( this.type ).
            time( this.time ).
            source( this.source ).
            user( this.user ).
            message( this.message ).
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

    public void setMessage( final String message )
    {
        this.message = message;
    }

    public void setObjectUris( final ScriptValue objectUris )
    {
        if ( objectUris == null || objectUris.getList() == null )
        {
            return;
        }

        List<URI> uris = objectUris.getList().stream().map( o -> URI.create( (String) o ) ).collect( Collectors.toList() );
        this.objectUris = ImmutableSet.copyOf( uris );
    }

    public void setData( final ScriptValue data )
    {
        if ( data == null || data.getMap() == null )
        {
            return;
        }

        this.data = new FormJsonToPropertyTreeTranslator( null, false ).translate( createJson( data.getMap() ) );
    }

    private JsonNode createJson( final Map<?, ?> value )
    {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.valueToTree( value );
    }
}
