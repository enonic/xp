package com.enonic.xp.context;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableMap;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.session.Session;

@PublicApi
public final class ContextBuilder
{
    private final Map<String, Object> attributes;

    private final LocalScope localScope;

    private ContextBuilder( final Map<String, Object> attributes, final LocalScope localScope )
    {
        this.attributes = attributes;
        this.localScope = localScope;
    }

    public ContextBuilder repositoryId( final String value )
    {
        return repositoryId( RepositoryId.from( value ) );
    }

    public ContextBuilder repositoryId( final RepositoryId value )
    {
        return attribute( value );
    }

    public ContextBuilder branch( final String value )
    {
        return branch( Branch.from( value ) );
    }

    public ContextBuilder branch( final Branch value )
    {
        return attribute( value );
    }

    public ContextBuilder authInfo( final AuthenticationInfo value )
    {
        return attribute( value );
    }

    public ContextBuilder attribute( final String key, final Object value )
    {
        this.attributes.put( key, value );
        return this;
    }

    public <T> ContextBuilder attribute( final T value )
    {
        return attribute( value.getClass().getName(), value );
    }

    public Context build()
    {
        return new ContextImpl( ImmutableMap.copyOf( this.attributes ), this.localScope );
    }

    public static ContextBuilder create()
    {
        return new ContextBuilder( new HashMap<>(), new LocalScopeImpl() );
    }

    public static ContextBuilder from( final Context parent )
    {
        return new ContextBuilder( new HashMap<>( parent.getAttributes() ), parent.getLocalScope() );
    }

    public static ContextBuilder copyOf( final Context context )
    {
        return new ContextBuilder( new HashMap<>( context.getAttributes() ),
                                   new LocalScopeImpl( mergeLocalScopeAttributes( context.getLocalScope() ) ) );
    }

    private static HashMap<String, Object> mergeLocalScopeAttributes( final LocalScope localScope )
    {
        final Map<String, Object> localAttributes = localScope.getAttributes();
        final Session session = localScope.getSession();
        final Map<String, Object> sessionAttributes = session == null ? Map.of() : session.getAttributes();

        return Stream.of( localAttributes, sessionAttributes ).
            flatMap( map -> map.entrySet().stream() ).
            collect( Collectors.toMap( Map.Entry::getKey, Map.Entry::getValue, ( v1, v2 ) -> v1, HashMap::new ) );
    }
}
