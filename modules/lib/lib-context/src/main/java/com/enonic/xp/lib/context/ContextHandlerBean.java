package com.enonic.xp.lib.context;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.context.LocalScope;
import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;
import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.SecurityConstants;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.security.SystemConstants;
import com.enonic.xp.security.User;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.security.auth.VerifiedUsernameAuthToken;
import com.enonic.xp.util.GenericValue;

public final class ContextHandlerBean
    implements ScriptBean
{
    // Applied unconditionally, so script attributes cannot collide with platform keys in the local scope.
    private static final String CUSTOM_PREFIX = "custom.";

    private Supplier<Context> context;

    private Supplier<SecurityService> securityService;

    public Object run( final ContextRunParams params )
    {
        final ContextBuilder builder = ContextBuilder.from( this.context.get() );
        applyRepository( builder, params.repository );
        applyAuthInfo( builder, params.idProvider, params.username, params.principals );
        applyBranch( builder, params.branch );
        addAttributes( builder, params.attributes );

        return builder.build().callWith( params.callback );
    }

    public ContextMapper get()
    {
        return new ContextMapper( this.context.get() );
    }

    public ContextRunParams newRunParams()
    {
        return new ContextRunParams();
    }

    public void setCustomLocalAttribute( final String name, final ScriptValue value )
    {
        final LocalScope localScope = this.context.get().getLocalScope();
        final GenericValue converted = toGenericValue( value );
        if ( converted == null )
        {
            localScope.removeAttribute( CUSTOM_PREFIX + name );
        }
        else
        {
            localScope.setAttribute( CUSTOM_PREFIX + name, converted );
        }
    }

    private static GenericValue toGenericValue( final ScriptValue value )
    {
        if ( value == null )
        {
            return null;
        }
        if ( value.isValue() )
        {
            return GenericValue.fromRawJava( value.getValue() );
        }
        if ( value.isArray() )
        {
            return GenericValue.fromRawJava( value.getList() );
        }
        if ( value.isObject() )
        {
            return GenericValue.fromRawJava( value.getMap() );
        }
        throw new IllegalArgumentException( "Local attribute value must be a string, number, boolean, array or object" );
    }

    private void applyRepository( final ContextBuilder builder, final String repository )
    {
        if ( repository != null )
        {
            builder.repositoryId( repository );
        }
    }

    private void applyAuthInfo( final ContextBuilder builder, final String idProvider, final String username,
                                final PrincipalKey[] principals )
    {
        AuthenticationInfo authInfo = this.context.get().getAuthInfo();
        if ( username != null )
        {
            authInfo = runAsAuthenticated( () -> this.securityService.get()
                .authenticate(
                    new VerifiedUsernameAuthToken( idProvider == null ? IdProviderKey.system() : IdProviderKey.from( idProvider ),
                                                   username ) ) );
        }
        if ( principals != null )
        {
            authInfo = AuthenticationInfo.copyOf( authInfo ).principals( principals ).build();
        }

        builder.authInfo( authInfo );
    }

    private void applyBranch( final ContextBuilder builder, final String branch )
    {
        if ( branch != null )
        {
            builder.branch( branch );
        }
    }

    private void addAttributes( final ContextBuilder builder, final Map<String, Object> attributes )
    {
        if ( attributes != null )
        {
            for ( Map.Entry<String, Object> attribute : attributes.entrySet() )
            {
                builder.attribute( attribute.getKey(), attribute.getValue() );
            }
        }
    }

    private <T> T runAsAuthenticated( final Callable<T> runnable )
    {
        final AuthenticationInfo authInfo =
            AuthenticationInfo.create().principals( RoleKeys.AUTHENTICATED ).user( User.anonymous() ).build();
        return ContextBuilder.from( this.context.get() )
            .authInfo( authInfo )
            .repositoryId( SystemConstants.SYSTEM_REPO_ID )
            .branch( SecurityConstants.BRANCH_SECURITY )
            .build()
            .callWith( runnable );
    }

    @Override
    public void initialize( final BeanContext context )
    {
        this.context = context.getBinding( Context.class );
        this.securityService = context.getService( SecurityService.class );
    }
}
