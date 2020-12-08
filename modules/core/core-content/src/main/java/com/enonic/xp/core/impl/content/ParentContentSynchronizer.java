package com.enonic.xp.core.impl.content;

import java.util.ArrayDeque;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.function.Function;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.FindContentByParentParams;
import com.enonic.xp.content.FindContentByParentResult;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.media.MediaInfoService;
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

    public Context sourceContext;

    private Context targetContext;

    private ContentService contentService;

    @Activate
    public ParentContentSynchronizer( @Reference final ContentService contentService, @Reference final MediaInfoService mediaInfoService )
    {
        this.contentService = contentService;

        syncCommandCreators = new LinkedHashMap();

        syncCommandCreators.put( ContentSyncEventType.CREATED, params -> CreatedEventSyncCommand.create().
            contentService( contentService ).
            params( params ).
            build() );
        syncCommandCreators.put( ContentSyncEventType.MOVED, params -> MovedEventSyncCommand.create().
            contentService( contentService ).
            params( params ).
            build() );
        syncCommandCreators.put( ContentSyncEventType.RENAMED, params -> RenamedEventSyncCommand.create().
            contentService( contentService ).
            params( params ).
            build() );
        syncCommandCreators.put( ContentSyncEventType.SORTED, params -> SortedEventSyncCommand.create().
            contentService( contentService ).
            params( params ).
            build() );
        syncCommandCreators.put( ContentSyncEventType.MANUAL_ORDER_UPDATED, params -> ManualOrderUpdatedEventSyncCommand.create().
            contentService( contentService ).
            params( params ).
            build() );
        syncCommandCreators.put( ContentSyncEventType.UPDATED, params -> UpdatedEventSyncCommand.create().
            contentService( contentService ).
            mediaInfoService( mediaInfoService ).
            params( params ).
            build() );
        syncCommandCreators.put( ContentSyncEventType.DELETED, params -> DeletedEventSyncCommand.create().
            contentService( contentService ).
            params( params ).
            build() );
    }

    // must be called from every public method
    private void initContext( final ProjectName sourceProject, final ProjectName targetProject )
    {
        this.targetContext = ContextBuilder.from( ContextAccessor.current() ).
            repositoryId( targetProject.getRepoId() ).
            branch( ContentConstants.BRANCH_DRAFT ).
            authInfo( adminAuthInfo() ).
            build();

        this.sourceContext = ContextBuilder.from( ContextAccessor.current() ).
            repositoryId( sourceProject.getRepoId() ).
            branch( ContentConstants.BRANCH_DRAFT ).
            authInfo( adminAuthInfo() ).
            build();
    }

    @Override
    public void sync( final ContentSyncParams params )
    {
        initContext( params.getSourceProject(), params.getTargetProject() );

        sourceContext.runWith( () -> {
            if ( params.getContentId() != null && contentService.contentExists( params.getContentId() ) )
            {
                if ( params.isIncludeChildren() )
                {
                    this.doSyncWithChildren( contentService.getById( params.getContentId() ) );
                }
                else
                {
                    this.doSync( contentService.getById( params.getContentId() ) );
                }
            }
            else
            {
                this.doSyncWithChildren( contentService.getByPath( ContentPath.ROOT ) );
            }
        } );
    }

    @Override
    public void sync( final ContentEventsSyncParams params )
    {
        initContext( params.getSourceProject(), params.getTargetProject() );

        sourceContext.runWith( () -> {
            final Content sourceContent =
                contentService.contentExists( params.getContentId() ) ? contentService.getById( params.getContentId() ) : null;

            targetContext.runWith( () -> {
                final Content targetContent =
                    contentService.contentExists( params.getContentId() ) ? contentService.getById( params.getContentId() ) : null;

                final ContentEventSyncCommandParams commandParams = createEventCommandParams( sourceContent, targetContent );
                final List<AbstractContentEventSyncCommand> commands = createEventCommands( commandParams, params.getSyncTypes() );

                commands.forEach( AbstractContentEventSyncCommand::sync );
            } );
        } );
    }

    private void doSyncWithChildren( final Content sourceContent )
    {
        final Queue<Content> queue = new ArrayDeque();

        sourceContext.runWith( () -> {

            queue.add( sourceContent );

            final Content root = contentService.getByPath( ContentPath.ROOT );

            if ( !root.getId().equals( sourceContent.getId() ) )
            {
                this.doSync( sourceContent );
            }

            while ( queue.size() > 0 )
            {
                final Content currentContent = queue.poll();

                final FindContentByParentResult result = contentService.findByParent( FindContentByParentParams.create().
                    parentId( currentContent.getId() ).
                    recursive( false ).
                    childOrder( currentContent.getChildOrder() ).
                    size( -1 ).
                    build() );

                for ( final Content content : result.getContents() )
                {
                    this.doSync( content );

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
                cleanDeletedContents( contentService.getById( sourceContent.getId() ) );
                return;
            }

            final Content root = sourceContext.callWith( () -> contentService.getByPath( ContentPath.ROOT ) );

            if ( root.getId().equals( sourceContent.getId() ) )
            {
                cleanDeletedContents( contentService.getByPath( ContentPath.ROOT ) );
            }
        } );
    }

    private void doSync( final Content sourceContent )
    {
        targetContext.runWith( () -> {
            final Content targetContent =
                contentService.contentExists( sourceContent.getId() ) ? contentService.getById( sourceContent.getId() ) : null;

            final ContentEventSyncCommandParams commandParams = createEventCommandParams( sourceContent, targetContent );

            final List<AbstractContentEventSyncCommand> commands = createEventCommands( commandParams, targetContent != null ? EnumSet.of(
                ContentSyncEventType.MOVED, ContentSyncEventType.RENAMED, ContentSyncEventType.SORTED,
                ContentSyncEventType.MANUAL_ORDER_UPDATED, ContentSyncEventType.UPDATED ) : EnumSet.of( ContentSyncEventType.CREATED ) );

            commands.forEach( AbstractContentEventSyncCommand::sync );
        } );
    }

    private ContentEventSyncCommandParams createEventCommandParams( final Content sourceContent, final Content targetContent )
    {
        return ContentEventSyncCommandParams.create().
            sourceContext( sourceContext ).
            targetContext( targetContext ).
            sourceContent( sourceContent ).
            targetContent( targetContent ).
            build();
    }

    private List<AbstractContentEventSyncCommand> createEventCommands( final ContentEventSyncCommandParams params,
                                                                       final EnumSet<ContentSyncEventType> syncTypes )
    {
        final ImmutableList.Builder<AbstractContentEventSyncCommand> commands = ImmutableList.builder();

        syncTypes.forEach( type -> commands.add( syncCommandCreators.get( type ).apply( params ) ) );

        return commands.build();
    }

    private void cleanDeletedContents( final Content targetContent )
    {
        targetContext.runWith( () -> {
            final Queue<Content> queue = new ArrayDeque<>( Set.of( targetContent ) );

            while ( queue.size() > 0 )
            {
                final Content currentContent = queue.poll();
                createEventCommands( createEventCommandParams( null, currentContent ), EnumSet.of( ContentSyncEventType.DELETED ) ).
                    forEach( AbstractContentEventSyncCommand::sync );

                if ( currentContent.hasChildren() )
                {
                    final FindContentByParentResult result = contentService.findByParent( FindContentByParentParams.create().
                        parentId( currentContent.getId() ).
                        recursive( false ).
                        childOrder( currentContent.getChildOrder() ).
                        size( -1 ).
                        build() );

                    for ( final Content content : result.getContents() )
                    {
                        queue.offer( content );
                    }
                }
            }
        } );
    }

    private AuthenticationInfo adminAuthInfo()
    {
        return AuthenticationInfo.create().
            principals( RoleKeys.ADMIN ).
            user( User.create().
                key( PrincipalKey.ofSuperUser() ).
                login( PrincipalKey.ofSuperUser().getId() ).
                build() ).
            build();
    }
}
