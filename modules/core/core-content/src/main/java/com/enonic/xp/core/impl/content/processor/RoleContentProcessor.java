package com.enonic.xp.core.impl.content.processor;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.CreateContentParams;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.security.Principals;
import com.enonic.xp.security.Role;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.SecurityService;

@Component
public final class RoleContentProcessor
    implements ContentProcessor
{

    private ContentService contentService;

    private SecurityService securityService;

    private final String SITE_CONFIG = "siteConfig";

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
        final Content editedContent = params.getEditedContent();
        final PropertySet editedSiteConfig = editedContent.getData().getPropertySet( SITE_CONFIG );
        final Content originalContent = contentService.getById( editedContent.getId() );
        final PropertySet originalSiteConfig = originalContent.getData().getPropertySet( SITE_CONFIG );
        final PrincipalKey principalKey = params.getModifier().getKey();

        if ( editedSiteConfig == null && originalSiteConfig != null ||
            editedSiteConfig != null && !editedSiteConfig.equals( originalSiteConfig ) )
        {
            if ( !this.hasAdminRole( principalKey ) )
            {
                throw new RoleRequiredException( principalKey, originalContent.getPath(), RoleKeys.ADMIN, RoleKeys.CONTENT_MANAGER_ADMIN );
            }
        }

        return null;
    }

    private boolean hasAdminRole( PrincipalKey key )
    {
        PrincipalKeys principalKeys = securityService.getMemberships( key );
        final Principals principals = securityService.getPrincipals( principalKeys );

        for ( Role role : principals.getRoles() )
        {
            if ( role.getKey().equals( RoleKeys.ADMIN ) || role.getKey().equals( RoleKeys.CONTENT_MANAGER_ADMIN ) )
            {
                return true;
            }
        }

        return false;
    }

    @Reference
    public void setContentService( final ContentService contentService )
    {
        this.contentService = contentService;
    }

    @Reference
    public void setSecurityService( final SecurityService securityService )
    {
        this.securityService = securityService;
    }
}
