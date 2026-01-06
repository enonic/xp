package com.enonic.xp.core.impl.content;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.archive.ArchiveConstants;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.User;
import com.enonic.xp.security.auth.AuthenticationInfo;

import static com.enonic.xp.content.ContentConstants.BRANCH_DRAFT;
import static com.enonic.xp.content.ContentConstants.CONTENT_ROOT_PATH_ATTRIBUTE;

@Component
public final class ParentContentSynchronizer
    implements ContentSynchronizer, ContentEventsSynchronizer
{
    private static final Logger LOG = LoggerFactory.getLogger( ParentContentSynchronizer.class );

    private final LayersContentService layersContentService;

    @Activate
    public ParentContentSynchronizer( @Reference final LayersContentService layersContentService )
    {
        this.layersContentService = layersContentService;
    }

    @Override
    public void sync( final ContentSyncParams params )
    {
        final Map<NodePath, Context> sourceContexts = initContexts( params.getSourceProject().getRepoId() );
        final Map<NodePath, Context> targetContexts = initContexts( params.getTargetProject().getRepoId() );

        if ( params.getContentIds().isEmpty() )
        {
            for ( Map.Entry<NodePath, Context> entry : sourceContexts.entrySet() )
            {
                final Context sourceCtx = entry.getValue();
                final Context targetCtx = targetContexts.get( entry.getKey() );
                final Content rootContent = sourceCtx.callWith( () -> layersContentService.getByPath( ContentPath.ROOT ).orElseThrow() );

                final List<ContentToSync> contentsToSync =
                    List.of( ContentToSync.create().sourceContent( rootContent ).sourceCtx( sourceCtx ).targetCtx( targetCtx ).build() );
                doSyncWithChildren( contentsToSync );
            }
        }
        else
        {
            final List<ContentToSync> contentsToSync = createContentsToSync( params.getContentIds(), sourceContexts, targetContexts );

            if ( !contentsToSync.isEmpty() )
            {
                if ( params.isIncludeChildren() )
                {
                    doSyncWithChildren( contentsToSync );
                }
                else
                {
                    doSync( contentsToSync );
                }
            }
        }
    }

    @Override
    public void sync( final ContentEventsSyncParams params )
    {
        final Map<NodePath, Context> sourceContexts = initContexts( params.getSourceProject().getRepoId() );
        final Map<NodePath, Context> targetContexts = initContexts( params.getTargetProject().getRepoId() );

        final List<ContentToSync> contents = createContentsToSync( params.getContentIds(), sourceContexts, targetContexts );

        if ( !contents.isEmpty() )
        {
            createEventCommand( contents, params.getSyncType() ).sync();
        }
    }

    private void doSyncWithChildren( final List<ContentToSync> sourceContents )
    {
        final List<ContentToSync> contentsToSync =
            sourceContents.stream().filter( sourceContent -> sourceContent.getSourceContent() != null ).filter( sourceContent -> {

                final Content root =
                    sourceContent.getSourceCtx().callWith( () -> layersContentService.getByPath( ContentPath.ROOT ).orElseThrow() );

                return !root.getId().equals( sourceContent.getSourceContent().getId() );

            } ).collect( Collectors.toList() );

        if ( !contentsToSync.isEmpty() )
        {
            doSync( contentsToSync );
        }

        sourceContents.stream().filter( contentToSync -> contentToSync.getSourceContent() != null ).forEach( currentContentToSync -> {
            final ContentIds result = currentContentToSync.getSourceCtx()
                .callWith( () -> layersContentService.findAllChildren( currentContentToSync.getSourceContent().getPath() ) );

            if ( !result.isEmpty() )
            {
                final Map<NodePath, Context> sourceContexts = initContexts( currentContentToSync.getSourceCtx().getRepositoryId() );
                final Map<NodePath, Context> targetContexts = initContexts( currentContentToSync.getTargetCtx().getRepositoryId() );

                doSyncWithChildren( createContentsToSync( result, sourceContexts, targetContexts ) );
            }
        } );

        sourceContents.forEach( sourceContent -> {
            if ( sourceContent.getTargetContent() != null )
            {
                cleanDeletedContents( sourceContent );
            }
            else
            {
                final Content root =
                    sourceContent.getSourceCtx().callWith( () -> layersContentService.getByPath( ContentPath.ROOT ).orElseThrow() );

                if ( root.getId().equals( sourceContent.getSourceContent().getId() ) )
                {
                    cleanDeletedContents( ContentToSync.create( sourceContent )
                                              .targetContent( sourceContent.getTargetCtx()
                                                                  .callWith( () -> layersContentService.getByPath( ContentPath.ROOT )
                                                                      .orElseThrow() ) )
                                              .build() );
                }
            }
        } );
    }

    private void doSync( final Collection<ContentToSync> sourceContents )
    {
        final List<ContentToSync> existedContents =
            sourceContents.stream().filter( contentToSync -> contentToSync.getTargetContent() != null ).toList();

        final List<ContentToSync> newContents =
            sourceContents.stream().filter( contentToSync -> contentToSync.getTargetContent() == null ).toList();

        if ( !newContents.isEmpty() )
        {
            createEventCommand( newContents, ContentSyncEventType.CREATED ).sync();
        }

        existedContents.stream().flatMap( contentToSync -> {
            final List<ContentToSync> singleContent = List.of( contentToSync );

            if ( contentToSync.getSourceContent() != null )
            {
                return Stream.of( createEventCommand( singleContent, ContentSyncEventType.SORTED ),
                                  createEventCommand( singleContent, ContentSyncEventType.UPDATED ),
                                  createEventCommand( singleContent, ContentSyncEventType.MOVED ) );
            }
            else
            {
                return Stream.of( createEventCommand( singleContent, ContentSyncEventType.DELETED ) );
            }
        } ).forEach( AbstractContentEventSyncCommand::sync );
    }

    private List<ContentToSync> createContentsToSync( final ContentIds contentIds, final Map<NodePath, Context> sourceContexts,
                                                      final Map<NodePath, Context> targetContexts )
    {
        final List<ContentToSync> result = contentIds.stream().map( contentId -> {
            Context actualSourceCtx = getActualContext( contentId, sourceContexts.values() );
            Context actualTargetCtx = getActualContext( contentId, targetContexts.values() );

            final Content sourceContent = actualSourceCtx != null
                ? actualSourceCtx.callWith( () -> this.layersContentService.getById( contentId ).orElseThrow() )
                : null;

            final Content targetContent = actualTargetCtx != null
                ? actualTargetCtx.callWith( () -> this.layersContentService.getById( contentId ).orElseThrow() )
                : null;

            if ( actualSourceCtx != null )
            {
                if ( actualTargetCtx == null )
                {
                    actualTargetCtx = getTargetContextByParent( actualSourceCtx, targetContexts, sourceContent );
                }
            }
            else if ( actualTargetCtx != null )
            {
                actualSourceCtx = sourceContexts.get( (NodePath) actualTargetCtx.getAttribute( CONTENT_ROOT_PATH_ATTRIBUTE ) );
            }
            else
            {
                return null;
            }

            if ( targetContent != null && ( targetContent.getOriginProject() == null ||
                !actualSourceCtx.getRepositoryId().equals( targetContent.getOriginProject().getRepoId() ) ) )
            {
                return null; // do not sync contents without origin parent or from other source
            }

            return ContentToSync.create()
                .sourceContent( sourceContent )
                .targetContent( targetContent )
                .sourceCtx( actualSourceCtx )
                .targetCtx( actualTargetCtx )
                .build();

        } ).filter( Objects::nonNull ).collect( Collectors.toList() );

        if ( result.isEmpty() )
        {
            LOG.debug( "nothing to sync, content ids: {}, source contexts: {}, target contexts: {}",
                       contentIds.stream().map( ContentId::toString ).collect( Collectors.joining( ", " ) ), sourceContexts.values()
                           .stream()
                           .map( context -> context.getRepositoryId() + "-" + context.getBranch() )
                           .collect( Collectors.joining( ", " ) ), targetContexts.values()
                           .stream()
                           .map( context -> context.getRepositoryId() + "-" + context.getBranch() )
                           .collect( Collectors.joining( ", " ) ) );
        }

        return result;

    }

    private AbstractContentEventSyncCommand createEventCommand( final List<ContentToSync> contentToSync, ContentSyncEventType syncType )
    {
        return ( switch ( syncType )
        {
            case CREATED ->
                contentToSync.getFirst().getTargetContent() == null ? CreatedEventSyncCommand.create() : UpdatedEventSyncCommand.create();
            case MOVED -> MovedEventSyncCommand.create();
            case SORTED -> SortedEventSyncCommand.create();
            case UPDATED -> UpdatedEventSyncCommand.create();
            case DELETED -> DeletedEventSyncCommand.create();
        } ).contentService( layersContentService ).contentToSync( contentToSync ).build();
    }

    private void cleanDeletedContents( final ContentToSync contentToSync )
    {
        contentToSync.getTargetCtx().runWith( () -> {
            final Queue<Content> queue = new ArrayDeque<>( Set.of( contentToSync.getTargetContent() ) );

            while ( !queue.isEmpty() )
            {
                final Content currentContent = queue.poll();

                final ProjectName originProject = currentContent.getOriginProject();

                if ( originProject != null && contentToSync.getSourceCtx().getRepositoryId().equals( originProject.getRepoId() ) )
                {
                    final Context actualSourceCtx =
                        getActualContext( currentContent.getId(), initContexts( contentToSync.getSourceCtx().getRepositoryId() ).values() );

                    final List<ContentToSync> contents = List.of( ContentToSync.create()
                                                                      .targetContent( currentContent )
                                                                      .sourceCtx( actualSourceCtx != null
                                                                                      ? actualSourceCtx
                                                                                      : contentToSync.getSourceCtx() )
                                                                      .targetCtx( contentToSync.getTargetCtx() )
                                                                      .build() );
                    createEventCommand( contents, ContentSyncEventType.DELETED ).sync();
                }

                layersContentService.getByIds( layersContentService.findAllChildren( currentContent.getPath() ) ).forEach( queue::offer );
            }
        } );
    }

    private Context initContext( final RepositoryId repositoryId, final NodePath root )
    {
        return ContextBuilder.from( ContextAccessor.current() )
            .repositoryId( repositoryId )
            .branch( BRANCH_DRAFT )
            .authInfo( adminAuthInfo() )
            .attribute( CONTENT_ROOT_PATH_ATTRIBUTE, root )
            .build();
    }

    private Map<NodePath, Context> initContexts( final RepositoryId repositoryId )
    {
        final Context contentContext = initContext( repositoryId, ContentConstants.CONTENT_ROOT_PATH );
        final Context archiveContext = initContext( repositoryId, ArchiveConstants.ARCHIVE_ROOT_PATH );

        return Map.of( ContentConstants.CONTENT_ROOT_PATH, contentContext, ArchiveConstants.ARCHIVE_ROOT_PATH, archiveContext );
    }

    private Context getActualContext( final ContentId contentId, final Collection<Context> contexts )
    {
        return contexts.stream()
            .filter( context -> context.callWith( () -> layersContentService.getById( contentId ).isPresent() ) )
            .findAny()
            .orElse( null );
    }

    private Context getTargetContextByParent( final Context sourceContext, final Map<NodePath, Context> availableTargetContexts,
                                              final Content sourceContent )
    {
        final ContentId parentId =
            sourceContext.callWith( () -> layersContentService.getByPath( sourceContent.getParentPath() ).orElseThrow().getId() );

        return availableTargetContexts.values()
            .stream()
            .filter( context -> context.callWith( () -> layersContentService.getById( parentId ).isPresent() ) )
            .findAny()
            .orElseGet( () -> availableTargetContexts.get( (NodePath) sourceContext.getAttribute( CONTENT_ROOT_PATH_ATTRIBUTE ) ) );
    }

    private AuthenticationInfo adminAuthInfo()
    {
        return AuthenticationInfo.create()
            .principals( RoleKeys.ADMIN )
            .user( User.create().key( PrincipalKey.ofSuperUser() ).login( PrincipalKey.ofSuperUser().getId() ).build() )
            .build();
    }
}
