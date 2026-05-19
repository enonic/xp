package com.enonic.xp.lib.context;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.LocalScope;
import com.enonic.xp.lib.common.PrincipalMapper;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.script.serializer.MapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.security.User;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.session.Session;

public final class ContextMapper
    implements MapSerializable
{
    private final Context context;

    public ContextMapper( final Context context )
    {
        this.context = context;
    }

    @Override
    public void serialize( final MapGenerator gen )
    {
        final Branch branch = this.context.getBranch();
        if ( branch != null )
        {
            gen.value( "branch", branch.toString() );
        }
        final RepositoryId repositoryId = this.context.getRepositoryId();
        if ( repositoryId != null )
        {
            gen.value( "repository", repositoryId.toString() );
        }
        serializeAuthInfo( gen, this.context.getAuthInfo() );
        serializeAttributes( gen );
    }

    private void serializeAuthInfo( final MapGenerator gen, final AuthenticationInfo info )
    {
        if ( info == null )
        {
            return;
        }

        gen.map( "authInfo" );
        serializeUser( gen, info.getUser() );
        serializePrincipals( gen, info.getPrincipals() );
        gen.end();
    }

    private void serializeUser( final MapGenerator gen, final User user )
    {
        if ( user == null )
        {
            return;
        }

        gen.map( "user" );
        new PrincipalMapper( user ).serialize( gen );
        gen.end();
    }

    private void serializePrincipals( final MapGenerator gen, final PrincipalKeys keys )
    {
        if ( keys == null )
        {
            return;
        }

        gen.array( "principals" );
        for ( final PrincipalKey key : keys )
        {
            gen.value( key.toString() );
        }
        gen.end();
    }

    private void serializeAttributes( final MapGenerator gen )
    {
        gen.map( "attributes" );
        getAttributes().forEach( ( k, v ) -> {
            if ( canBeSerialized( v ) )
            {
                gen.value( k, v );
            }
        } );
        gen.end();
    }

    private Map<String, Object> getAttributes()
    {
        LocalScope localScope = this.context.getLocalScope();
        Map<String, Object> attributes = this.context.getAttributes();
        Map<String, Object> localAttributes = localScope.getAttributes();
        Session session = localScope.getSession();
        Map<String, Object> sessionAttributes = session == null ? Map.of() : session.getAttributes();

        return Stream.of( attributes, localAttributes, sessionAttributes ).
            flatMap( map -> map.entrySet().stream() ).
            collect( Collectors.toMap( Map.Entry::getKey, Map.Entry::getValue, ( v1, v2 ) -> v1, HashMap::new ) );
    }

    private boolean canBeSerialized( final Object value )
    {
        return value instanceof Number || value instanceof String || value instanceof Boolean || value instanceof MapSerializable;
    }
}
