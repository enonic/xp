package com.enonic.xp.core.impl.content;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.google.common.collect.ImmutableList;

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
import com.enonic.xp.media.MediaInfoService;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.User;
import com.enonic.xp.security.auth.AuthenticationInfo;

@Component
public final class ParentContentSynchronizer
    implements ContentSynchronizer, ContentEventsSynchronizer
{
    private final Map<ContentSyncEventType, Function<ContentEventSyncCommandParams, AbstractContentEventSyncCommand>> syncCommandCreators;

    private final ContentService contentService;

    @Activate
    public ParentContentSynchronizer( @Reference final ContentService contentService, @Reference final MediaInfoService mediaInfoService )
    {
        this.contentService = contentService;

        syncCommandCreators = new LinkedHashMap<>();

        syncCommandCreators.put( ContentSyncEventType.CREATED,
                                 params -> CreatedEventSyncCommand.create().contentService( contentService ).params( params ).build() );
        syncCommandCreators.put( ContentSyncEventType.MOVED,
                                 params -> MovedEventSyncCommand.create().contentService( contentService ).params( params ).build() );
        syncCommandCreators.put( ContentSyncEventType.RENAMED,
                                 params -> RenamedEventSyncCommand.create().contentService( contentService ).params( params ).build() );
        syncCommandCreators.put( ContentSyncEventType.SORTED,
                                 params -> SortedEventSyncCommand.create().contentService( contentService ).params( params ).build() );
        syncCommandCreators.put( ContentSyncEventType.MANUAL_ORDER_UPDATED, params -> ManualOrderUpdatedEventSyncCommand.create()
            .contentService( contentService )
            .params( params )
            .build() );
        syncCommandCreators.put( ContentSyncEventType.UPDATED, params -> UpdatedEventSyncCommand.create()
            .contentService( contentService )
            .mediaInfoService( mediaInfoService )
            .params( params )
            .build() );
        syncCommandCreators.put( ContentSyncEventType.DELETED,
                                 params -> DeletedEventSyncCommand.create().contentService( contentService ).params( params ).build() );
    }

    private Context initContext( final ProjectName projectName, final NodePath root )
    {
        return ContextBuilder.from( ContextAccessor.current() )
            .repositoryId( projectName.getRepoId() )
            .branch( ContentConstants.BRANCH_DRAFT )
            .authInfo( adminAuthInfo() )
            .attribute( "contentRootPath", root )
            .build();
    }

    @Override
    public void sync( final ContentSyncParams params )
    {
        final Map<NodePath, Context> sourceContexts = initContexts( params.getSourceProject() );
        final Map<NodePath, Context> targetContexts = initContexts( params.getTargetProject() );

        if ( params.getContentIds().isEmpty() )
        {
            sourceContexts.forEach( ( root, context ) -> this.doSyncWithChildren( context.callWith( () -> List.of( ContentToSync.create()
                                                                                                                       .sourceContent(
                                                                                                                           contentService.getByPath(
                                                                                                                               ContentPath.ROOT ) )
                                                                                                                       .sourceContext(
                                                                                                                           context )
                                                                                                                       .targetContext(
                                                                                                                           targetContexts.get(
                                                                                                                               root ) )
                                                                                                                       .build() ) ) ) );
            return;
        }

        final ImmutableList.Builder<ContentToSync> contentsToSync = ImmutableList.builder();

        params.getContentIds().forEach( contentId -> {
            final Context actualSourceContext = getActualContext( contentId, sourceContexts.values() );
            Context actualTargetContext = getActualContext( contentId, targetContexts.values() );

            if ( actualSourceContext != null )
            {
                final Content sourceContent = actualSourceContext.callWith( () -> contentService.getById( contentId ) );
                Content targetContent = null;
                if ( actualTargetContext == null )
                {
                    actualTargetContext = targetContexts.get( (NodePath) actualSourceContext.getAttribute( "contentRootPath" ) );
                }
                else
                {
                    targetContent = actualTargetContext.callWith( () -> contentService.getById( contentId ) );
                }

                contentsToSync.add( ContentToSync.create()
                                        .sourceContent( sourceContent )
                                        .targetContent( targetContent )
                                        .sourceContext( actualSourceContext )
                                        .targetContext( actualTargetContext )
                                        .build() );

                if ( params.isIncludeChildren() )
                {
                    this.doSyncWithChildren( contentsToSync.build() );
                }
                else
                {
                    this.doSync( contentsToSync.build() );
                }
            }
            else if ( actualTargetContext != null )
            {
                actualTargetContext.runWith( () -> this.sync( ContentEventsSyncParams.create()
                                                                  .addContentIds( params.getContentIds() )
                                                                  .sourceProject( params.getSourceProject() )
                                                                  .targetProject( params.getTargetProject() )
                                                                  .syncEventType( ContentSyncEventType.DELETED )
                                                                  .build() ) );
            }
        } );

    }

    @Override
    public void sync( final ContentEventsSyncParams params )
    {
        final Map<NodePath, Context> sourceContexts = initContexts( params.getSourceProject() );
        final Map<NodePath, Context> targetContexts = initContexts( params.getTargetProject() );

        final List<ContentToSync> contents = params.getContentIds().stream().map( contentId -> {
            Context sourceContextToSync = getActualContext( contentId, sourceContexts.values() );
            Context targetContextToSync = getActualContext( contentId, targetContexts.values() );

            final ContentToSync.Builder contentToSync = ContentToSync.create()
                .sourceContent(
                    sourceContextToSync != null ? sourceContextToSync.callWith( () -> this.contentService.getById( contentId ) ) : null )
                .targetContent(
                    targetContextToSync != null ? targetContextToSync.callWith( () -> this.contentService.getById( contentId ) ) : null );

            if ( sourceContextToSync != null )
            {
                if ( targetContextToSync == null )
                {
                    targetContextToSync = targetContexts.get( (NodePath) sourceContextToSync.getAttribute( "contentRootPath" ) );
                }
            }
            else if ( targetContextToSync != null )
            {
                sourceContextToSync = sourceContexts.get( (NodePath) targetContextToSync.getAttribute( "contentRootPath" ) );
            }

            return contentToSync.sourceContext( sourceContextToSync ).targetContext( targetContextToSync ).build();
        } ).collect( Collectors.toList() );

        final ContentEventSyncCommandParams commandParams = createEventCommandParams( contents );
        final AbstractContentEventSyncCommand command = createEventCommand( commandParams, params.getSyncType() );

        command.sync();
    }

    private void doSyncWithChildren( final Collection<ContentToSync> sourceContents )
    {
        final Queue<ContentToSync> queue = new ArrayDeque<>( sourceContents );

        final Map<NodePath, Context> targetContexts = !sourceContents.isEmpty() ? initContexts(
            ProjectName.from( sourceContents.stream().findAny().get().getTargetContext().getRepositoryId() ) ) : Map.of();

        final List<ContentToSync> contentsToSync = sourceContents.stream().filter( sourceContent -> {

            final Content root = sourceContent.getSourceContext().callWith( () -> contentService.getByPath( ContentPath.ROOT ) );
            return !root.getId().equals( sourceContent.getSourceContent().getId() );

        } ).collect( Collectors.toList() );

        if ( !contentsToSync.isEmpty() )
        {
            this.doSync( contentsToSync );
        }

        while ( queue.size() > 0 )
        {
            final ContentToSync currentContentToSync = queue.poll();

            final FindContentByParentResult result = currentContentToSync.getSourceContext()
                .callWith( () -> contentService.findByParent( FindContentByParentParams.create()
                                                                  .parentId( currentContentToSync.getId() )
                                                                  .recursive( false )
                                                                  .childOrder( currentContentToSync.getSourceContent().getChildOrder() )
                                                                  .size( -1 )
                                                                  .build() ) );

            if ( result.getContents().isNotEmpty() )
            {
                final List<ContentToSync> childrenToSync = result.getContents().stream().map( content -> {
                    final Context actualTargetContext = getActualContext( content.getId(), targetContexts.values() );

                    return ContentToSync.create()
                        .sourceContent( content )
                        .targetContent( actualTargetContext != null
                                            ? actualTargetContext.callWith( () -> contentService.getById( content.getId() ) )
                                            : null )
                        .sourceContext( currentContentToSync.getSourceContext() )
                        .targetContext( actualTargetContext != null ? actualTargetContext : currentContentToSync.getTargetContext() )
                        .build();
                } ).collect( Collectors.toList() );

                this.doSync( childrenToSync );

                for ( final ContentToSync content : childrenToSync )
                {
                    if ( content.getSourceContent().hasChildren() )
                    {
                        queue.offer( content );
                    }
                }
            }
        }

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
        final ContentEventSyncCommandParams commandParams = createEventCommandParams( sourceContents );

        final List<AbstractContentEventSyncCommand> commands =
            sourceContents.stream().allMatch( contentToSync -> contentToSync.getTargetContent() != null )
                ? List.of( createEventCommand( commandParams, ContentSyncEventType.MOVED ),
                           createEventCommand( commandParams, ContentSyncEventType.RENAMED ),
                           createEventCommand( commandParams, ContentSyncEventType.SORTED ),
                           createEventCommand( commandParams, ContentSyncEventType.MANUAL_ORDER_UPDATED ),
                           createEventCommand( commandParams, ContentSyncEventType.UPDATED ) )
                : List.of( createEventCommand( commandParams, ContentSyncEventType.CREATED ) );

        commands.forEach( AbstractContentEventSyncCommand::sync );
    }

    private ContentEventSyncCommandParams createEventCommandParams( final Collection<ContentToSync> contents )
    {
        return ContentEventSyncCommandParams.create().addContents( contents ).build();
    }

    private AbstractContentEventSyncCommand createEventCommand( final ContentEventSyncCommandParams params,
                                                                final ContentSyncEventType syncType )
    {
        return syncCommandCreators.get( syncType ).apply( params );
    }

    private void cleanDeletedContents( final ContentToSync contentToSync )
    {
        contentToSync.getTargetContext().runWith( () -> {
            final Queue<Content> queue = new ArrayDeque<>( Set.of( contentToSync.getTargetContent() ) );

            while ( queue.size() > 0 )
            {
                final Content currentContent = queue.poll();
                createEventCommand( createEventCommandParams( List.of( ContentToSync.create()
                                                                           .targetContent( currentContent )
                                                                           .sourceContext( contentToSync.getSourceContext() )
                                                                           .targetContext( contentToSync.getTargetContext() )
                                                                           .build() ) ), ContentSyncEventType.DELETED ).sync();

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

    private Map<NodePath, Context> initContexts( final ProjectName projectName )
    {
        final Context contentContext = initContext( projectName, ContentConstants.CONTENT_ROOT_PATH );
        final Context archiveContext = initContext( projectName, ArchiveConstants.ARCHIVE_ROOT_PATH );

        return Map.of( (NodePath) contentContext.getAttribute( "contentRootPath" ), contentContext,
                       (NodePath) archiveContext.getAttribute( "contentRootPath" ), archiveContext );
    }

    private Context getActualContext( final ContentId contentId, final Collection<Context> contexts )
    {
        return contexts.stream()
            .filter( context -> context.callWith( () -> contentService.contentExists( contentId ) ) )
            .findAny()
            .orElse( null );
    }

    private AuthenticationInfo adminAuthInfo()
    {
        return AuthenticationInfo.create()
            .principals( RoleKeys.ADMIN )
            .user( User.create().key( PrincipalKey.ofSuperUser() ).login( PrincipalKey.ofSuperUser().getId() ).build() )
            .build();
    }
}
