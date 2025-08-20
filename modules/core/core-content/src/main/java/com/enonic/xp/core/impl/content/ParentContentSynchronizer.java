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
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.FindContentByParentParams;
import com.enonic.xp.content.FindContentByParentResult;
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

    private final ContentService contentService;

    @Activate
    public ParentContentSynchronizer( @Reference final ContentService contentService )
    {
        this.contentService = contentService;
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
                final NodePath root = entry.getKey();
                final Context sourceContext = entry.getValue();
                final Content rootContent = sourceContext.callWith( () -> contentService.getByPath( ContentPath.ROOT ) );

                final Context targetContext = targetContexts.get( root );
                final List<ContentToSync> contentsToSync = List.of( ContentToSync.create()
                                                                        .sourceContent( rootContent )
                                                                        .sourceContext( sourceContext )
                                                                        .targetContext( targetContext )
                                                                        .build() );

                this.doSyncWithChildren( contentsToSync );
            }
        }
        else
        {
            final List<ContentToSync> contentsToSync = createContentsToSync( params.getContentIds(), sourceContexts, targetContexts );

            if ( !contentsToSync.isEmpty() )
            {
                if ( params.isIncludeChildren() )
                {
                    this.doSyncWithChildren( contentsToSync );
                }
                else
                {
                    this.doSync( contentsToSync );
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

                final Content root = sourceContent.getSourceContext().callWith( () -> contentService.getByPath( ContentPath.ROOT ) );

                return !root.getId().equals( sourceContent.getSourceContent().getId() );

            } ).collect( Collectors.toList() );

        if ( !contentsToSync.isEmpty() )
        {
            this.doSync( contentsToSync );
        }

        sourceContents.stream().filter( sourceContent -> sourceContent.getSourceContent() != null ).forEach( currentContentToSync -> {
            final FindContentByParentResult result = currentContentToSync.getSourceContext()
                .callWith( () -> contentService.findByParent( FindContentByParentParams.create()
                                                                  .parentId( currentContentToSync.getId() )
                                                                  .recursive( false )
                                                                  .childOrder( currentContentToSync.getSourceContent().getChildOrder() )
                                                                  .size( -1 )
                                                                  .build() ) );

            if ( !result.getContents().isEmpty() )
            {
                this.sync( ContentSyncParams.create()
                               .sourceProject( ProjectName.from( currentContentToSync.getSourceContext().getRepositoryId() ) )
                               .targetProject( ProjectName.from( currentContentToSync.getTargetContext().getRepositoryId() ) )
                               .addContentIds( result.getContents().getIds().getSet() )
                               .includeChildren( true )
                               .build() );
            }
        } );

        sourceContents.forEach( sourceContent -> {
            if ( sourceContent.getTargetContent() != null )
            {
                cleanDeletedContents( sourceContent );
                return;
            }

            final Content root = sourceContent.getSourceContext().callWith( () -> contentService.getByPath( ContentPath.ROOT ) );

            if ( root.getId().equals( sourceContent.getSourceContent().getId() ) )
            {
                cleanDeletedContents( ContentToSync.create( sourceContent )
                                          .targetContent( sourceContent.getTargetContext()
                                                              .callWith( () -> contentService.getByPath( ContentPath.ROOT ) ) )
                                          .build() );
            }
        } );
    }

    private void doSync( final Collection<ContentToSync> sourceContents )
    {
        final List<ContentToSync> existedContents =
            sourceContents.stream().filter( contentToSync -> contentToSync.getTargetContent() != null ).collect( Collectors.toList() );

        final List<ContentToSync> newContents =
            sourceContents.stream().filter( contentToSync -> contentToSync.getTargetContent() == null ).collect( Collectors.toList() );

        if ( !newContents.isEmpty() )
        {
            createEventCommand( newContents, ContentSyncEventType.CREATED ).sync();
        }

        existedContents.stream().flatMap( contentToSync -> {
            final List<ContentToSync> singleContent = List.of( contentToSync );

            if ( contentToSync.getSourceContent() != null )
            {
                return Stream.of( createEventCommand( singleContent, ContentSyncEventType.MOVED ),
                                  createEventCommand( singleContent, ContentSyncEventType.RENAMED ),
                                  createEventCommand( singleContent, ContentSyncEventType.SORTED ),
                                  createEventCommand( singleContent, ContentSyncEventType.UPDATED ) );
            }
            else
            {
                return Stream.of( createEventCommand( singleContent, ContentSyncEventType.DELETED ) );
            }
        } ).forEach( AbstractContentEventSyncCommand::sync );
    }

    private List<ContentToSync> createContentsToSync( final List<ContentId> contentIds, final Map<NodePath, Context> sourceContexts,
                                                      final Map<NodePath, Context> targetContexts )
    {
        final List<ContentToSync> result = contentIds.stream().map( contentId -> {
            Context actualSourceContext = getActualContext( contentId, sourceContexts.values() );
            Context actualTargetContext = getActualContext( contentId, targetContexts.values() );

            final Content sourceContent =
                actualSourceContext != null ? actualSourceContext.callWith( () -> this.contentService.getById( contentId ) ) : null;
            final Content targetContent =
                actualTargetContext != null ? actualTargetContext.callWith( () -> this.contentService.getById( contentId ) ) : null;

            if ( actualSourceContext != null )
            {
                if ( actualTargetContext == null )
                {
                    actualTargetContext = getTargetContextByParent( actualSourceContext, targetContexts, sourceContent );
                }
            }
            else if ( actualTargetContext != null )
            {
                actualSourceContext = sourceContexts.get( (NodePath) actualTargetContext.getAttribute( CONTENT_ROOT_PATH_ATTRIBUTE ) );
            }
            else
            {
                return null;
            }

            if ( targetContent != null && ( targetContent.getOriginProject() == null ||
                !actualSourceContext.getRepositoryId().equals( targetContent.getOriginProject().getRepoId() ) ) )
            {
                return null; // do not sync contents without origin parent or from other source
            }

            return ContentToSync.create()
                .sourceContent( sourceContent )
                .targetContent( targetContent )
                .sourceContext( actualSourceContext )
                .targetContext( actualTargetContext )
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

    private AbstractContentEventSyncCommand createEventCommand( final List<ContentToSync> contentToSync,
                                                                ContentSyncEventType syncType )
    {
        return ( switch ( syncType )
        {
            case CREATED ->
                contentToSync.getFirst().getTargetContent() == null ? CreatedEventSyncCommand.create() : UpdatedEventSyncCommand.create();
            case MOVED -> MovedEventSyncCommand.create();
            case RENAMED -> RenamedEventSyncCommand.create();
            case SORTED -> SortedEventSyncCommand.create();
            case UPDATED -> UpdatedEventSyncCommand.create();
            case DELETED -> DeletedEventSyncCommand.create();
        } ).contentService( contentService ).contentToSync( contentToSync ).build();
    }

    private void cleanDeletedContents( final ContentToSync contentToSync )
    {
        contentToSync.getTargetContext().runWith( () -> {
            final Queue<Content> queue = new ArrayDeque<>( Set.of( contentToSync.getTargetContent() ) );

            while ( !queue.isEmpty() )
            {
                final Content currentContent = queue.poll();

                final ProjectName originProject = currentContent.getOriginProject();

                if ( originProject != null && contentToSync.getSourceContext().getRepositoryId().equals( originProject.getRepoId() ) )
                {
                    final Context actualSourceContext = getActualContext( currentContent.getId(), initContexts(
                        contentToSync.getSourceContext().getRepositoryId() ).values() );

                    final List<ContentToSync> contents = List.of( ContentToSync.create()
                                                                               .targetContent( currentContent )
                                                                               .sourceContext( actualSourceContext != null
                                                                                                   ? actualSourceContext
                                                                                                   : contentToSync.getSourceContext() )
                                                                               .targetContext( contentToSync.getTargetContext() )
                                                                               .build() );
                    createEventCommand( contents, ContentSyncEventType.DELETED ).sync();
                }

                if ( currentContent.hasChildren() )
                {
                    final FindContentByParentResult result = contentService.findByParent( FindContentByParentParams.create()
                                                                                              .parentId( currentContent.getId() )
                                                                                              .recursive( false )
                                                                                              .childOrder( currentContent.getChildOrder() )
                                                                                              .size( -1 )
                                                                                              .build() );

                    for ( final Content content : result.getContents() )
                    {
                        queue.offer( content );
                    }
                }
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
            .filter( context -> context.callWith( () -> contentService.contentExists( contentId ) ) )
            .findAny()
            .orElse( null );
    }

    private Context getTargetContextByParent( final Context sourceContext, final Map<NodePath, Context> availableTargetContexts,
                                              final Content sourceContent )
    {
        final ContentId parentId = sourceContext.callWith( () -> contentService.getByPath( sourceContent.getParentPath() ).getId() );

        return availableTargetContexts.values()
            .stream()
            .filter( context -> context.callWith( () -> contentService.contentExists( parentId ) ) )
            .findAny()
            .orElse( availableTargetContexts.get( (NodePath) sourceContext.getAttribute( CONTENT_ROOT_PATH_ATTRIBUTE ) ) );
    }

    private AuthenticationInfo adminAuthInfo()
    {
        return AuthenticationInfo.create()
            .principals( RoleKeys.ADMIN )
            .user( User.create().key( PrincipalKey.ofSuperUser() ).login( PrincipalKey.ofSuperUser().getId() ).build() )
            .build();
    }
}
