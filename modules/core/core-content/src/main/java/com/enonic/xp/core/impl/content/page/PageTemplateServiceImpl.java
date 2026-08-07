package com.enonic.xp.core.impl.content.page;


import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentQuery;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.CreateContentParams;
import com.enonic.xp.content.FindContentIdsByQueryResult;
import com.enonic.xp.content.GetContentByIdsParams;
import com.enonic.xp.core.impl.content.ContentServiceImpl;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.page.CreatePageTemplateParams;
import com.enonic.xp.page.GetDefaultPageTemplateParams;
import com.enonic.xp.page.Page;
import com.enonic.xp.page.PageTemplate;
import com.enonic.xp.page.PageTemplateKey;
import com.enonic.xp.page.PageTemplateService;
import com.enonic.xp.page.PageTemplates;
import com.enonic.xp.query.filter.ValueFilter;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypeNames;
import com.enonic.xp.security.PrincipalKey;

import static java.util.Objects.requireNonNull;

@Component(immediate = true)
public final class PageTemplateServiceImpl
    implements PageTemplateService
{
    private final ContentService contentService;

    @Activate
    public PageTemplateServiceImpl( @Reference final ContentService contentService )
    {
        this.contentService = contentService;
    }

    @Override
    public PageTemplate create( final CreatePageTemplateParams params )
    {
        final PropertyTree data = new PropertyTree();
        final ContentTypeNames supports = params.getSupports();
        if ( supports != null )
        {
            data.addStrings( "supports", supports.stream().map( ContentTypeName::toString ).toList() );
        }

        return (PageTemplate) contentService.create( CreateContentParams.create()
                                                         .name( params.getName() )
                                                         .displayName( params.getDisplayName() )
                                                         .owner( PrincipalKey.ofAnonymous() )
                                                         .contentData( data )
                                                         .type( ContentTypeName.pageTemplate() )
                                                         .parent( ContentPath.from( params.getSite(),
                                                                                    ContentServiceImpl.TEMPLATES_FOLDER_NAME ) )
                                                         .page( Page.create()
                                                                    .descriptor( params.getController() )
                                                                    .config( params.getPageConfig() )
                                                                    .regions( params.getRegions() )
                                                                    .build() )
                                                         .build() );
    }

    @Override
    public PageTemplate getByKey( final PageTemplateKey pageTemplateKey )
    {
        requireNonNull( pageTemplateKey, "pageTemplateKey is required" );
        return (PageTemplate) contentService.getById( pageTemplateKey.getContentId() );
    }

    @Override
    public PageTemplate getDefault( GetDefaultPageTemplateParams params )
    {
        ContentPath sitePath = params.getSitePath();
        if ( sitePath == null )
        {
            sitePath = contentService.getById( params.getSite() ).getPath();
        }
        // no order expressions: the default template is the first one in the child order of the templates folder
        final FindContentIdsByQueryResult result = contentService.find( ContentQuery.create()
                                                                           .parentPath( ContentPath.from( sitePath,
                                                                                                          ContentServiceImpl.TEMPLATES_FOLDER_NAME ) )
                                                                           .addContentTypeName( ContentTypeName.pageTemplate() )
                                                                           .queryFilter( ValueFilter.create()
                                                                                             .fieldName( "data.supports" )
                                                                                             .addValues(
                                                                                                 params.getContentType().toString() )
                                                                                             .build() )
                                                                           .size( 1 )
                                                                           .build() );

        final ContentId templateId = result.getContentIds().first();
        return templateId == null ? null : (PageTemplate) contentService.getById( templateId );
    }

    @Override
    public PageTemplates getBySite( final ContentId siteId )
    {
        requireNonNull( siteId, "siteId is required" );
        final ContentPath sitePath = contentService.getById( siteId ).getPath();
        // every template of the site, so unlimited rather than the fixed page findIdsByParent used to impose
        final FindContentIdsByQueryResult result = contentService.find( ContentQuery.create()
                                                                           .parentPath( ContentPath.from( sitePath,
                                                                                                          ContentServiceImpl.TEMPLATES_FOLDER_NAME ) )
                                                                           .addContentTypeName( ContentTypeName.pageTemplate() )
                                                                           .size( -1 )
                                                                           .build() );

        return contentService.getByIds( GetContentByIdsParams.create().contentIds( result.getContentIds() ).build() )
            .stream()
            .map( content -> (PageTemplate) content )
            .collect( PageTemplates.collector() );
    }
}
