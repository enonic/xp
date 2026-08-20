package com.enonic.xp.core.impl.content;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Supplier;

import org.jspecify.annotations.NullMarked;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

import com.google.common.io.ByteSource;

import com.enonic.xp.archive.ArchiveContentParams;
import com.enonic.xp.archive.RestoreContentParams;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentQuery;
import com.enonic.xp.content.ContentValidator;
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
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.core.impl.content.processor.ContentProcessor;
import com.enonic.xp.event.EventPublisher;
import com.enonic.xp.node.ListNodesParams;
import com.enonic.xp.node.ListNodesResult;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.page.PageDescriptorService;
import com.enonic.xp.region.LayoutDescriptorService;
import com.enonic.xp.region.PartDescriptorService;
import com.enonic.xp.schema.content.ContentTypeService;
import com.enonic.xp.schema.mixin.MixinService;
import com.enonic.xp.site.CmsService;
import com.enonic.xp.util.BinaryReference;

@Component(configurationPid = "com.enonic.xp.content", service = LayersContentService.class)
@NullMarked
public class LayersContentService
{
    private static final String SEARCH_PREFERENCE_ATTRIBUTE = "_search_preference";

    private static final String SEARCH_PREFERENCE_PRIMARY = "PRIMARY";

    private static final int SYNC_BATCH_SIZE = 1_000;

    private final NodeService nodeService;

    private final ContentTypeService contentTypeService;

    private final EventPublisher eventPublisher;

    private final MixinService mixinService;

    private final CmsService cmsService;

    private final PageDescriptorService pageDescriptorService;

    private final PartDescriptorService partDescriptorService;

    private final LayoutDescriptorService layoutDescriptorService;

    private final List<ContentProcessor> contentProcessors = new CopyOnWriteArrayList<>();

    private final List<ContentValidator> contentValidators = new CopyOnWriteArrayList<>();

    private final ContentConfig config;

    @Activate
    public LayersContentService( @Reference final NodeService nodeService, @Reference final ContentTypeService contentTypeService,
                                 @Reference final EventPublisher eventPublisher, @Reference final MixinService mixinService,
                                 @Reference final CmsService cmsService, @Reference final PageDescriptorService pageDescriptorService,
                                 @Reference final PartDescriptorService partDescriptorService,
                                 @Reference final LayoutDescriptorService layoutDescriptorService, ContentConfig config )
    {
        this.nodeService = nodeService;
        this.contentTypeService = contentTypeService;
        this.eventPublisher = eventPublisher;
        this.mixinService = mixinService;
        this.cmsService = cmsService;
        this.pageDescriptorService = pageDescriptorService;
        this.partDescriptorService = partDescriptorService;
        this.layoutDescriptorService = layoutDescriptorService;
        this.config = config;
    }

    @SuppressWarnings("unused")
    @Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
    public void addContentProcessor( final ContentProcessor contentProcessor )
    {
        this.contentProcessors.add( contentProcessor );
    }

    public void removeContentProcessor( final ContentProcessor contentProcessor )
    {
        this.contentProcessors.remove( contentProcessor );
    }

    @Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
    public void addContentValidator( final ContentValidator contentValidator )
    {
        this.contentValidators.add( contentValidator );
    }

    public void removeContentValidator( final ContentValidator contentValidator )
    {
        this.contentValidators.remove( contentValidator );
    }

    public void archive( final ArchiveContentParams params )
    {
        ArchiveContentCommand.create( params )
            .layersSync()
            .nodeService( nodeService )
            .eventPublisher( eventPublisher )
            .contentTypeService( contentTypeService )
            .build()
            .execute();
    }

    public void restore( final RestoreContentParams params )
    {
        RestoreContentCommand.create( params )
            .layersSync()
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
            .layersSync()
            .nodeService( this.nodeService )
            .contentTypeService( this.contentTypeService )
            .eventPublisher( this.eventPublisher )
            .cmsService( this.cmsService )
            .mixinService( this.mixinService )
            .contentProcessors( this.contentProcessors )
            .contentValidators( this.contentValidators )
            .pageDescriptorService( this.pageDescriptorService )
            .partDescriptorService( this.partDescriptorService )
            .layoutDescriptorService( this.layoutDescriptorService )
            .allowUnsafeAttachmentNames( config.attachments_allowUnsafeNames() )
            .build()
            .execute();
    }

    public MoveContentsResult move( final MoveContentParams params )
    {
        return MoveContentCommand.create( params )
            .layersSync()
            .nodeService( this.nodeService )
            .contentTypeService( this.contentTypeService )
            .eventPublisher( this.eventPublisher )
            .mixinService( this.mixinService )
            .contentValidators( this.contentValidators )
            .build()
            .execute();
    }

    public SortContentResult sort( final SortContentParams params )
    {
        return SortContentCommand.create( params )
            .layersSync()
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

    public Optional<Content> getByPath( final ContentPath path )
    {
        return callOnPrimary( () -> Optional.ofNullable( GetContentByPathCommand.create( path )
                                                             .allowRoot()
                                                             .nodeService( this.nodeService )
                                                             .contentTypeService( this.contentTypeService )
                                                             .eventPublisher( this.eventPublisher )
                                                             .build()
                                                             .execute() ) );
    }

    public Contents getByIds( final ContentIds contentIds )
    {
        return callOnPrimary( () -> GetContentByIdsCommand.create( GetContentByIdsParams.create().contentIds( contentIds ).build() )
            .allowRoot()
            .nodeService( this.nodeService )
            .contentTypeService( this.contentTypeService )
            .eventPublisher( this.eventPublisher )
            .build()
            .execute() );
    }

    public Optional<Content> getById( final ContentId contentId )
    {
        return callOnPrimary( () -> Optional.ofNullable( GetContentByIdCommand.create( contentId )
                                                             .allowRoot()
                                                             .nodeService( this.nodeService )
                                                             .contentTypeService( this.contentTypeService )
                                                             .eventPublisher( this.eventPublisher )
                                                             .build()
                                                             .execute() ) );
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
        return callOnPrimary( () -> FindContentIdsByQueryCommand.create()
            .query( query )
            .nodeService( this.nodeService )
            .contentTypeService( this.contentTypeService )
            .eventPublisher( this.eventPublisher )
            .build()
            .execute() );
    }

    public ContentIdsBatch findAllChildren( final ContentPath contentPath, final String cursor )
    {
        return list( contentPath, false, cursor );
    }

    public ContentIdsBatch findAllByParent( final ContentPath contentPath, final String cursor )
    {
        return list( contentPath, true, cursor );
    }

    /**
     * Enumerated rather than searched: syncing has to see every content that exists right now, including one written a moment ago that a
     * search would not find yet, and it has no use for the ordering or the constraints a search would spend that freshness on. And
     * consumed in batches: the sync flows once split their walks by level only to bound memory, which a single severely huge level
     * defeats - a batch stays bounded whatever the shape of the tree.
     */
    private ContentIdsBatch list( final ContentPath contentPath, final boolean recursive, final String cursor )
    {
        return callOnPrimary( () -> {
            final ListNodesResult batch = nodeService.list( ListNodesParams.create()
                                                                .parentPath(
                                                                    ContentNodeHelper.translateContentPathToNodePath( contentPath ) )
                                                                .recursive( recursive )
                                                                .batchSize( SYNC_BATCH_SIZE )
                                                                .cursor( cursor )
                                                                .build() );
            return new ContentIdsBatch( batch.getEntries()
                                            .stream()
                                            .map( entry -> ContentId.from( entry.nodeId() ) )
                                            .collect( ContentIds.collector() ), batch.getCursor() );
        } );
    }

    /**
     * One batch of an enumeration and the position it stopped at. The contract is the listing's own: continue until the cursor is
     * null, and a batch may be empty while the enumeration is not finished.
     */
    public record ContentIdsBatch(ContentIds ids, String cursor)
    {
    }

    private <T> T callOnPrimary( final Supplier<T> supplier )
    {
        return ContextBuilder.from( ContextAccessor.current() )
            .attribute( SEARCH_PREFERENCE_ATTRIBUTE, SEARCH_PREFERENCE_PRIMARY )
            .build()
            .callWith( supplier::get );
    }
}
