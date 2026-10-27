package com.enonic.xp.lib.auth;

import org.jspecify.annotations.NullMarked;

import com.enonic.xp.script.serializer.MapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.token.Scope;

@NullMarked
public final class ScopeMapper
    implements MapSerializable
{
    private final Scope scope;

    public ScopeMapper( final Scope scope )
    {
        this.scope = scope;
    }

    @Override
    public void serialize( final MapGenerator gen )
    {
        gen.value( "idProvider", scope.getIdProvider().toString() );
        gen.value( "id", scope.getId() );
        gen.value( "title", scope.getTitle() );
        gen.value( "description", scope.getDescription() );
        gen.array( "roles" );
        scope.getRoles().stream().map( PrincipalKey::toString ).sorted().forEach( gen::value );
        gen.end();
        gen.value( "required", scope.isRequired() );
    }
}
