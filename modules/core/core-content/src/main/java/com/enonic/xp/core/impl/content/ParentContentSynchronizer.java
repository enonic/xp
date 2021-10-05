package com.enonic.xp.core.impl.content;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.function.Function;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

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

        if ( params.getContentId() == null )
        {
            sourceContexts.forEach(
                ( root, context ) -> this.doSyncWithChildren( context.callWith( () -> contentService.getByPath( ContentPath.ROOT ) ),
                                                              context, targetContexts.get( root ) ) );
            return;
        }

        final Context actualSourceContext = getActualContext( params.getContentId(), sourceContexts.values() );
        Context actualTargetContext = getActualContext( params.getContentId(), targetContexts.values() );

        if ( actualSourceContext != null )
        {
            if ( actualTargetContext == null )
            {
                actualTargetContext = targetContexts.get( (NodePath) actualSourceContext.getAttribute( "contentRootPath" ) );
            }

            if ( params.isIncludeChildren() )
            {
                this.doSyncWithChildren( actualSourceContext.callWith( () -> contentService.getById( params.getContentId() ) ),
                                         actualSourceContext, actualTargetContext );
            }
            else
            {
                this.doSync( actualSourceContext.callWith( () -> contentService.getById( params.getContentId() ) ), actualSourceContext,
                             actualTargetContext );
            }
        }
        else if ( actualTargetContext != null )
        {
            actualTargetContext.runWith( () -> this.sync( ContentEventsSyncParams.create()
                                                              .contentId( params.getContentId() )
                                                              .sourceProject( params.getSourceProject() )
                                                              .targetProject( params.getTargetProject() )
                                                              .syncEventType( ContentSyncEventType.DELETED )
                                                              .build() ) );
        }
    }

    @Override
    public void sync( final ContentEventsSyncParams params )
    {
        final Map<NodePath, Context> sourceContexts = initContexts( params.getSourceProject() );
        final Map<NodePath, Context> targetContexts = initContexts( params.getTargetProject() );

        Context actualSourceContext = getActualContext( params.getContentId(), sourceContexts.values() );
        Context actualTargetContext = getActualContext( params.getContentId(), targetContexts.values() );

        final Content sourceContent =
            actualSourceContext != null ? actualSourceContext.callWith( () -> this.contentService.getById( params.getContentId() ) ) : null;
        final Content targetContent =
            actualTargetContext != null ? actualTargetContext.callWith( () -> this.contentService.getById( params.getContentId() ) ) : null;

        if ( actualSourceContext != null )
        {
            if ( actualTargetContext == null )
            {
                actualTargetContext = targetContexts.get( (NodePath) actualSourceContext.getAttribute( "contentRootPath" ) );
            }
        }
        else if ( actualTargetContext != null )
        {
            actualSourceContext = sourceContexts.get( (NodePath) actualTargetContext.getAttribute( "contentRootPath" ) );
        }

        final ContentEventSyncCommandParams commandParams =
            createEventCommandParams( sourceContent, targetContent, actualSourceContext, actualTargetContext );
        final AbstractContentEventSyncCommand command = createEventCommand( commandParams, params.getSyncType() );

        command.sync();
    }

    private void doSyncWithChildren( final Content sourceContent, final Context sourceContext, final Context targetContext )
    {
        final Queue<Content> queue = new ArrayDeque<>();

        final Map<NodePath, Context> targetContexts = initContexts( ProjectName.from( targetContext.getRepositoryId() ) );

        sourceContext.runWith( () -> {

            queue.add( sourceContent );

            final Content root = contentService.getByPath( ContentPath.ROOT );

            if ( !root.getId().equals( sourceContent.getId() ) )
            {
                final Context actualTargetContext = getActualContext( sourceContent.getId(), targetContexts.values() );
                this.doSync( sourceContent, sourceContext, actualTargetContext != null ? actualTargetContext : targetContext );
            }

            while ( queue.size() > 0 )
            {
                final Content currentContent = queue.poll();

                final FindContentByParentResult result = contentService.findByParent( FindContentByParentParams.create()
                                                                                          .parentId( currentContent.getId() )
                                                                                          .recursive( false )
                                                                                          .childOrder( currentContent.getChildOrder() )
                                                                                          .size( -1 )
                                                                                          .build() );

                for ( final Content content : result.getContents() )
                {
                    final Context actualTargetContext = getActualContext( content.getId(), targetContexts.values() );
                    this.doSync( content, sourceContext, actualTargetContext != null ? actualTargetContext : targetContext );

                    if ( content.hasChildren() )
                    {
                        queue.offer( content );
                    }
                }
            }
        } );

        targetContext.runWith( () -> {
            if ( contentService.contentExists( sourceContent.getId() ) )
            {
                cleanDeletedContents( contentService.getById( sourceContent.getId() ), sourceContext, targetContext );
                return;
            }

            final Content root = sourceContext.callWith( () -> contentService.getByPath( ContentPath.ROOT ) );

            if ( root.getId().equals( sourceContent.getId() ) )
            {
                cleanDeletedContents( contentService.getByPath( ContentPath.ROOT ), sourceContext, targetContext );
            }
        } );
    }

    private void doSync( final Content sourceContent, final Context sourceContext, final Context targetContext )
    {
        targetContext.runWith( () -> {
            final Content targetContent =
                contentService.contentExists( sourceContent.getId() ) ? contentService.getById( sourceContent.getId() ) : null;

            final ContentEventSyncCommandParams commandParams =
                createEventCommandParams( sourceContent, targetContent, sourceContext, targetContext );

            final List<AbstractContentEventSyncCommand> commands =
                targetContent != null
                    ? List.of( createEventCommand( commandParams, ContentSyncEventType.MOVED ),
                               createEventCommand( commandParams, ContentSyncEventType.RENAMED ),
                               createEventCommand( commandParams, ContentSyncEventType.SORTED ),
                               createEventCommand( commandParams, ContentSyncEventType.MANUAL_ORDER_UPDATED ),
                               createEventCommand( commandParams, ContentSyncEventType.UPDATED ) )
                    : List.of( createEventCommand( commandParams, ContentSyncEventType.CREATED ) );

            commands.forEach( AbstractContentEventSyncCommand::sync );
        } );
    }

    private ContentEventSyncCommandParams createEventCommandParams( final Content sourceContent, final Content targetContent,
                                                                    final Context sourceContext, final Context targetContext )
    {
        return ContentEventSyncCommandParams.create()
            .sourceContext( sourceContext )
            .targetContext( targetContext )
            .sourceContent( sourceContent )
            .targetContent( targetContent )
            .build();
    }

    private AbstractContentEventSyncCommand createEventCommand( final ContentEventSyncCommandParams params,
                                                                final ContentSyncEventType syncType )
    {
        return syncCommandCreators.get( syncType ).apply( params );
    }

    private void cleanDeletedContents( final Content targetContent, final Context sourceContext, final Context targetContext )
    {
        targetContext.runWith( () -> {
            final Queue<Content> queue = new ArrayDeque<>( Set.of( targetContent ) );

            while ( queue.size() > 0 )
            {
                final Content currentContent = queue.poll();
                createEventCommand( createEventCommandParams( null, currentContent, sourceContext, targetContext ),
                                    ContentSyncEventType.DELETED ).sync();

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
