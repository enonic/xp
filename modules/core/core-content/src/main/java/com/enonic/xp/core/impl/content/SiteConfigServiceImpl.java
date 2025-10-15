package com.enonic.xp.core.impl.content;

import java.util.Optional;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.event.EventPublisher;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.project.Project;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.project.ProjectService;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.schema.content.ContentTypeService;
import com.enonic.xp.site.Site;
import com.enonic.xp.site.SiteConfigService;
import com.enonic.xp.site.SiteConfigs;
import com.enonic.xp.site.SiteConfigsDataSerializer;

@Component
public final class SiteConfigServiceImpl
    implements SiteConfigService
{
    private final NodeService nodeService;

    private final ProjectService projectService;

    private final ContentTypeService contentTypeService;

    private final EventPublisher eventPublisher;

    private final ContentNodeTranslator translator;

    @Activate
    public SiteConfigServiceImpl( @Reference NodeService nodeService, @Reference ProjectService projectService,
                                  @Reference ContentTypeService contentTypeService, @Reference EventPublisher eventPublisher )
    {
        this.nodeService = nodeService;
        this.projectService = projectService;

        this.contentTypeService = contentTypeService;
        this.eventPublisher = eventPublisher;
        this.translator = new ContentNodeTranslator();
    }

    @Override
    public SiteConfigs getSiteConfigs( final ContentPath path )
    {
        final Site nearestSite = (Site) FindNearestContentByPathCommand.create()
            .contentPath( path )
            .predicate( Content::isSite )
            .nodeService( this.nodeService )
            .contentTypeService( this.contentTypeService )
            .translator( this.translator )
            .eventPublisher( this.eventPublisher )
            .build()
            .execute();

        return nearestSite == null ? getProjectSiteConfigs() : SiteConfigsDataSerializer.fromData( nearestSite.getData().getRoot() );
    }

    private SiteConfigs getProjectSiteConfigs()
    {
        final RepositoryId repositoryId = ContextAccessor.current().getRepositoryId();

        return Optional.ofNullable( repositoryId != null ? ProjectName.from( repositoryId ) : null )
            .map( projectService::get )
            .map( Project::getSiteConfigs )
            .orElseGet( SiteConfigs::empty );
    }

}
