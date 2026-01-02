package com.enonic.xp.core.impl.content;

import java.util.Optional;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.google.common.io.ByteSource;

import com.enonic.xp.archive.ArchiveContentParams;
import com.enonic.xp.archive.RestoreContentParams;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentQuery;
import com.enonic.xp.content.Contents;
import com.enonic.xp.content.DeleteContentParams;
import com.enonic.xp.content.FindContentIdsByQueryResult;
import com.enonic.xp.content.GetContentByIdsParams;
import com.enonic.xp.content.ImportContentParams;
import com.enonic.xp.content.ImportContentResult;
import com.enonic.xp.content.MoveContentParams;
import com.enonic.xp.content.MoveContentsResult;
import com.enonic.xp.content.PatchContentParams;
import com.enonic.xp.content.PatchContentResult;
import com.enonic.xp.content.SortContentParams;
import com.enonic.xp.content.SortContentResult;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.event.EventPublisher;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.node.NodeIndexPath;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.query.expr.CompareExpr;
import com.enonic.xp.query.expr.FieldExpr;
import com.enonic.xp.query.expr.QueryExpr;
import com.enonic.xp.query.expr.ValueExpr;
import com.enonic.xp.schema.content.ContentTypeService;
import com.enonic.xp.util.BinaryReference;

@Component
public class InternalContentService
{
    private final NodeService nodeService;

    private final ContentTypeService contentTypeService;

    private final EventPublisher eventPublisher;

    @Activate
    public InternalContentService( @Reference final NodeService nodeService, @Reference final ContentTypeService contentTypeService,
                                   @Reference final EventPublisher eventPublisher )
    {
        this.nodeService = nodeService;
        this.contentTypeService = contentTypeService;
        this.eventPublisher = eventPublisher;
    }

    public void archive( final ArchiveContentParams params )
    {
        ArchiveContentCommand.create( params )
            .nodeService( nodeService )
            .eventPublisher( eventPublisher )
            .contentTypeService( contentTypeService )
            .build()
            .execute();
    }


    public void restore( final RestoreContentParams params )
    {
        RestoreContentCommand.create( params )
            .nodeService( nodeService )
            .eventPublisher( eventPublisher )
            .contentTypeService( contentTypeService )
            .build()
            .execute();
    }

    public ImportContentResult importContent( final ImportContentParams params )
    {
        return ImportContentCommand.create()
            .params( params )
            .nodeService( nodeService )
            .contentTypeService( contentTypeService )
            .eventPublisher( eventPublisher )
            .build()
            .execute();
    }

    public PatchContentResult patch( final PatchContentParams params )
    {
        return PatchContentCommand.create( params )
            .nodeService( this.nodeService )
            .contentTypeService( this.contentTypeService )
            .eventPublisher( this.eventPublisher )
            .siteService( this.siteService )
            .xDataService( this.xDataService )
            .contentProcessors( this.contentProcessors )
            .contentValidators( this.contentValidators )
            .pageDescriptorService( this.pageDescriptorService )
            .pageTemplateService( this.pageTemplateService )
            .partDescriptorService( this.partDescriptorService )
            .layoutDescriptorService( this.layoutDescriptorService )
            .allowUnsafeAttachmentNames( config.attachments_allowUnsafeNames() )
            .build()
            .execute();
    }

    public MoveContentsResult move( final MoveContentParams params )
    {
        return MoveContentCommand.create( params )
            .nodeService( this.nodeService )
            .contentTypeService( this.contentTypeService )
            .eventPublisher( this.eventPublisher )
            .xDataService( this.xDataService )
            .contentValidators( this.contentValidators )
            .build()
            .execute();
    }

    public SortContentResult sort( final SortContentParams params )
    {
        return SortContentCommand.create( params )
            .nodeService( this.nodeService )
            .contentTypeService( this.contentTypeService )
            .eventPublisher( this.eventPublisher )
            .build()
            .execute();
    }

    public void delete( final DeleteContentParams params )
    {
        DeleteContentCommand.create()
            .nodeService( this.nodeService )
            .contentTypeService( this.contentTypeService )
            .eventPublisher( this.eventPublisher )
            .params( params )
            .build()
            .execute();
    }


    public Content getByPath( final ContentPath path )
    {
        return getByPathOptional( path ).orElseThrow( ContentNotFoundException.create()
                                                          .contentPath( path )
                                                          .repositoryId( ContextAccessor.current().getRepositoryId() )
                                                          .branch( ContextAccessor.current().getBranch() )
                                                          .contentRoot( ContentNodeHelper.getContentRoot() )::build );
    }

    public Optional<Content> getByPathOptional( final ContentPath path )
    {
        return Optional.ofNullable( GetContentByPathCommand.create( path )
                                        .nodeService( this.nodeService )
                                        .contentTypeService( this.contentTypeService )
                                        .eventPublisher( this.eventPublisher )
                                        .build()
                                        .execute() );
    }

    public Content getById( final ContentId contentId )
    {
        return getByIdOptional( contentId ).orElseThrow( ContentNotFoundException.create()
                                                             .contentId( contentId )
                                                             .repositoryId( ContextAccessor.current().getRepositoryId() )
                                                             .branch( ContextAccessor.current().getBranch() )
                                                             .contentRoot( ContentNodeHelper.getContentRoot() )::build );
    }

    public Contents getByIds( final ContentIds contentIds )
    {
        return GetContentByIdsCommand.create( GetContentByIdsParams.create().contentIds( contentIds ).build() )
            .nodeService( this.nodeService )
            .contentTypeService( this.contentTypeService )
            .eventPublisher( this.eventPublisher )
            .build()
            .execute();
    }


    public Optional<Content> getByIdOptional( final ContentId contentId )
    {
        return Optional.ofNullable( GetContentByIdCommand.create( contentId )
                                        .nodeService( this.nodeService )
                                        .contentTypeService( this.contentTypeService )
                                        .eventPublisher( this.eventPublisher )
                                        .build()
                                        .execute() );
    }

    public ByteSource getBinary( final ContentId contentId, final BinaryReference binaryReference )
    {
        return GetBinaryCommand.create( contentId, binaryReference )
            .nodeService( this.nodeService )
            .contentTypeService( this.contentTypeService )
            .eventPublisher( this.eventPublisher )
            .build()
            .execute();
    }

    public FindContentIdsByQueryResult find( final ContentQuery query )
    {
        return FindContentIdsByQueryCommand.create()
            .query( query )
            .nodeService( this.nodeService )
            .contentTypeService( this.contentTypeService )
            .eventPublisher( this.eventPublisher )
            .build()
            .execute();
    }

    public FindContentIdsByQueryResult findDirectByParent( final ContentPath contentPath, final ChildOrder childOrder )
    {
        return find( ContentQuery.create()
                         .size( -1 )
                         .queryExpr( QueryExpr.from( CompareExpr.eq( FieldExpr.from( NodeIndexPath.PATH ), ValueExpr.string(
                             ContentNodeHelper.translateContentPathToNodePath( contentPath ).toString() ) ) ) )
                         .build() );
    }

    public FindContentIdsByQueryResult findByParent( final ContentPath contentPath )
    {
        return find( ContentQuery.create()
                         .size( -1 )
                         .queryExpr( QueryExpr.from( CompareExpr.like( FieldExpr.from( NodeIndexPath.PATH ), ValueExpr.string(
                             ContentNodeHelper.translateContentPathToNodePath( contentPath ) + "/*" ) ) ) )
                         .build() );
    }

    public void findByParent(Object a) {

    }
}
