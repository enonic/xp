package com.enonic.xp.core.impl.content.processor;

import java.util.Objects;
import java.util.concurrent.Callable;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.content.CreateContentParams;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.security.Principals;
import com.enonic.xp.security.Role;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.security.User;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.site.Site;
import com.enonic.xp.site.SiteConfigs;

@Component
public final class RoleContentProcessor
    implements ContentProcessor
{
    private SecurityService securityService;

    @Override
    public boolean supports( final ContentType contentType )
    {
        return contentType.getName().isSite();
    }

    @Override
    public ProcessCreateResult processCreate( final ProcessCreateParams params )
    {
        final CreateContentParams createContentParams = params.getCreateContentParams();

        return new ProcessCreateResult( CreateContentParams.create( createContentParams ).build() );
    }

    @Override
    public ProcessUpdateResult processUpdate( final ProcessUpdateParams params )
    {
        final Site editedSite = (Site) params.getEditedContent();
        final SiteConfigs editedSiteConfigs = editedSite.getSiteConfigs();
        final Site originalSite = (Site) params.getOriginalContent();
        final SiteConfigs originalSiteConfigs = originalSite.getSiteConfigs();
        final User modifier = params.getModifier();

        if ( !Objects.equals( originalSiteConfigs, editedSiteConfigs ) && !this.hasContentAdminRole( modifier ) )
        {
            throw new RoleRequiredException( modifier.getKey(), RoleKeys.ADMIN, RoleKeys.CONTENT_MANAGER_ADMIN );
        }

        return null;
    }

    private boolean hasContentAdminRole( final User user )
    {
        return runAsAdmin( () -> {
            PrincipalKeys principalKeys = securityService.getMemberships( user.getKey() );
            final Principals principals = securityService.getPrincipals( principalKeys );

            for ( Role role : principals.getRoles() )
            {
                if ( role.getKey().equals( RoleKeys.ADMIN ) || role.getKey().equals( RoleKeys.CONTENT_MANAGER_ADMIN ) )
                {
                    return true;
                }
            }

            return false;
        } );

    }

    private <T> T runAsAdmin( final Callable<T> callable )
    {
        final Context context = ContextAccessor.current();
        return ContextBuilder.from( ContextAccessor.current() ).
            authInfo( AuthenticationInfo.copyOf( context.getAuthInfo() ).
                principals( RoleKeys.ADMIN ).build() ).
            build().
            callWith( callable );
    }

    @Reference
    public void setSecurityService( final SecurityService securityService )
    {
        this.securityService = securityService;
    }
}
