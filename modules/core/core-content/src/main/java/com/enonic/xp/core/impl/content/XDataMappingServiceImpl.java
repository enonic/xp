package com.enonic.xp.core.impl.content;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationWildcardMatcher;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.event.EventPublisher;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.project.Project;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.project.ProjectService;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypeService;
import com.enonic.xp.schema.xdata.XData;
import com.enonic.xp.schema.xdata.XDataName;
import com.enonic.xp.schema.xdata.XDataService;
import com.enonic.xp.site.Site;
import com.enonic.xp.site.SiteConfig;
import com.enonic.xp.site.SiteConfigs;
import com.enonic.xp.site.SiteConfigsDataSerializer;
import com.enonic.xp.site.SiteService;
import com.enonic.xp.site.XDataMappingService;
import com.enonic.xp.site.XDataMappings;
import com.enonic.xp.site.XDataOption;

import static com.google.common.base.Strings.nullToEmpty;
import static java.util.stream.Collectors.toList;

@Component
public final class XDataMappingServiceImpl
    implements XDataMappingService
{
    private final NodeService nodeService;

    private final ProjectService projectService;

    private final SiteService siteService;

    private final XDataService xDataService;

    private final ContentTypeService contentTypeService;

    private final EventPublisher eventPublisher;

    private final ContentNodeTranslator translator;


    @Activate
    public XDataMappingServiceImpl( @Reference NodeService nodeService, @Reference ProjectService projectService,
                                    @Reference SiteService siteService, @Reference XDataService xDataService,
                                    @Reference ContentTypeService contentTypeService, @Reference EventPublisher eventPublisher )
    {
        this.nodeService = nodeService;
        this.projectService = projectService;
        this.siteService = siteService;
        this.xDataService = xDataService;

        this.contentTypeService = contentTypeService;
        this.eventPublisher = eventPublisher;
        this.translator = new ContentNodeTranslator();
    }

    @Override
    public List<XDataOption> fetch( final ContentPath path, final ContentTypeName type )
    {
        return path != null ? getSiteXData( path, type ) : List.of();
    }

    public List<XDataOption> fetch( final SiteConfigs siteConfigs, final ContentTypeName type )
    {
        final List<ApplicationKey> applicationKeys =
            Stream.concat( siteConfigs.stream().map( SiteConfig::getApplicationKey ), Stream.of( ApplicationKey.PORTAL ) )
                .distinct()
                .collect( toList() );

        return getXDataByApps( applicationKeys, type );
    }

    private List<XDataOption> getSiteXData( final ContentPath path, final ContentTypeName type )
    {
        final List<ApplicationKey> applicationKeys =
            Stream.concat( getSiteOrProjectConfigs( path ).stream().map( SiteConfig::getApplicationKey ),
                           Stream.of( ApplicationKey.PORTAL ) ).distinct().collect( toList() );

        return getXDataByApps( applicationKeys, type );
    }

    private SiteConfigs getSiteOrProjectConfigs( final ContentPath path )
    {
        final Site nearestSite = (Site) FindNearestContentByPathCommand.create().contentPath( path ).predicate( Content::isSite )
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

    private List<XDataOption> getXDataByApps( final Collection<ApplicationKey> applicationKeys, final ContentTypeName contentType )
    {
        final XDataMappings.Builder builder = XDataMappings.create();

        applicationKeys.stream()
            .map( siteService::getDescriptor )
            .filter( Objects::nonNull )
            .forEach( siteDescriptor -> builder.addAll( siteDescriptor.getXDataMappings() ) );

        return getXDatasByContentType( builder.build(), contentType );
    }

    private List<XDataOption> getXDatasByContentType( final XDataMappings xDataMappings, final ContentTypeName contentTypeName )
    {
        final Map<XDataName, XDataOption> result = new LinkedHashMap<>();

        xDataMappings.stream().filter( xDataMapping -> {
            final String pattern = xDataMapping.getAllowContentTypes();
            final ApplicationKey applicationKey = xDataMapping.getXDataName().getApplicationKey();

            return nullToEmpty( pattern ).isBlank() || new ApplicationWildcardMatcher<>( applicationKey, ContentTypeName::toString,
                                                                                         ApplicationWildcardMatcher.Mode.MATCH ).matches(
                pattern, contentTypeName );
        } ).forEach( xDataMapping -> {
            final XData xData = this.xDataService.getByName( xDataMapping.getXDataName() );
            if ( xData != null )
            {
                result.compute( xData.getName(), ( k, v ) -> v == null || v.optional()
                    ? new XDataOption( xData, xDataMapping.getOptional() )
                    : new XDataOption( xData, false ) );
            }
        } );

        return result.values().stream().toList();
    }
}
