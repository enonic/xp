package com.enonic.xp.core.impl.project;

import java.util.Objects;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.content.CreateContentParams;
import com.enonic.xp.content.processor.ContentProcessor;
import com.enonic.xp.content.processor.ProcessCreateParams;
import com.enonic.xp.content.processor.ProcessCreateResult;
import com.enonic.xp.content.processor.ProcessUpdateParams;
import com.enonic.xp.content.processor.ProcessUpdateResult;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.project.ProjectConstants;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.project.ProjectRole;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.site.Site;
import com.enonic.xp.site.SiteConfigs;

@Component
public final class ProjectAccessSiteProcessor
    implements ContentProcessor
{
    private ProjectPermissionsContextManager projectPermissionsContextManager;

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

        final Context context = ContextAccessor.current();
        final AuthenticationInfo authenticationInfo = context.getAuthInfo();
        final ProjectName projectName = ProjectName.from( context.getRepositoryId() );

        if ( !Objects.equals( originalSiteConfigs, editedSiteConfigs ) )
        {
            if ( !ProjectAccessHelper.hasAdminAccess( authenticationInfo ) &&
                !this.projectPermissionsContextManager.hasAnyProjectRole( authenticationInfo, projectName, Set.of( ProjectRole.OWNER ) ) )
            {
                throw new ProjectAccessRequiredException( authenticationInfo.getUser().getKey(), ProjectRole.OWNER );
            }
        }

        return null;
    }

    @Reference
    public void setProjectPermissionsContextManager( final ProjectPermissionsContextManager projectPermissionsContextManager )
    {
        this.projectPermissionsContextManager = projectPermissionsContextManager;
    }
}
