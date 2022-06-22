package com.enonic.xp.lib.portal.current;

import java.util.function.Supplier;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.lib.common.PropertyTreeMapper;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.project.Project;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.project.ProjectService;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;
import com.enonic.xp.site.Site;
import com.enonic.xp.site.SiteConfig;

public final class GetCurrentSiteConfigHandler
    implements ScriptBean
{
    private PortalRequest request;

    private Supplier<ProjectService> projectService;

    public PropertyTreeMapper execute()
    {
        final ApplicationKey applicationKey = this.request.getApplicationKey();
        if ( applicationKey != null )
        {
            final Site site = this.request.getSite();

            PropertyTree appConfigPropertyTree = null;

            if ( site != null )
            {
                appConfigPropertyTree = site.getSiteConfig( applicationKey );
            }
            else
            {
                final Project project = this.projectService.get().get( ProjectName.from( ContextAccessor.current().getRepositoryId() ) );
                if ( project != null )
                {
                    final SiteConfig config = project.getSiteConfigs().get( applicationKey );
                    if ( config != null )
                    {
                        appConfigPropertyTree = config.getConfig();
                    }
                }
            }

            if ( appConfigPropertyTree != null )
            {
                return new PropertyTreeMapper( appConfigPropertyTree );
            }
        }
        return null;
    }

    @Override
    public void initialize( final BeanContext context )
    {
        this.request = context.getBinding( PortalRequest.class ).get();
        this.projectService = context.getService( ProjectService.class );
    }
}
