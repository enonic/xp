package com.enonic.xp.core.impl.project;

import java.util.Objects;

import org.osgi.service.component.annotations.Component;

import com.enonic.xp.content.CreateContentParams;
import com.enonic.xp.content.processor.ContentProcessor;
import com.enonic.xp.content.processor.ProcessCreateParams;
import com.enonic.xp.content.processor.ProcessCreateResult;
import com.enonic.xp.content.processor.ProcessUpdateParams;
import com.enonic.xp.content.processor.ProcessUpdateResult;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.project.ProjectRole;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.site.Site;

@Component
public final class ProjectAccessSiteProcessor
    implements ContentProcessor
{
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
        if ( !Objects.equals( ( (Site) params.getOriginalContent() ).getSiteConfigs(),
                              ( (Site) params.getEditedContent() ).getSiteConfigs() ) )
        {
            final Context context = ContextAccessor.current();
            final AuthenticationInfo authenticationInfo = context.getAuthInfo();
            final ProjectName projectName = ProjectName.from( context.getRepositoryId() );

            if ( !ProjectAccessHelper.hasAccess( authenticationInfo, projectName, ProjectRole.OWNER ) )
            {
                throw new ProjectAccessRequiredException( authenticationInfo.getUser().getKey(), ProjectRole.OWNER );
            }
        }

        return null;
    }
}
