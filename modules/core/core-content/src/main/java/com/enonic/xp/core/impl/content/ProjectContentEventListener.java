package com.enonic.xp.core.impl.content;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.event.Event;
import com.enonic.xp.event.EventConstants;
import com.enonic.xp.event.EventListener;
import com.enonic.xp.project.Project;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.project.ProjectNotFoundException;
import com.enonic.xp.project.ProjectService;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.User;
import com.enonic.xp.security.auth.AuthenticationInfo;

import static com.enonic.xp.core.impl.content.Constants.CONTENT_SKIP_SYNC;

@Component(immediate = true)
public final class ProjectContentEventListener
    implements EventListener
{
    private static final Logger LOG = LoggerFactory.getLogger( ProjectContentEventListener.class );

    private final ProjectService projectService;

    private final ContentEventsSynchronizer contentSynchronizer;

    private final Executor executor;

    @Activate
    public ProjectContentEventListener( @Reference final ProjectService projectService,
                                        @Reference final ContentEventsSynchronizer contentSynchronizer,
                                        @Reference(service = ProjectContentSyncExecutor.class) final Executor executor )
    {
        this.projectService = projectService;
        this.contentSynchronizer = contentSynchronizer;
        this.executor = executor;
    }

    @Override
    public void onEvent( final Event event )
    {
        if ( !event.isLocalOrigin() )
        {
            return;
        }

        if ( this.isAllowedContentEvent( event.getType() ) )
        {
            this.handleContentEvent( event );
        }
    }

    private void handleContentEvent( final Event event )
    {
        final List<Map<String, String>> nodes = (List<Map<String, String>>) event.getData().get( EventConstants.NODES_FIELD );

        final boolean isContentEvent = nodes.stream()
            .map( map -> map.get( "path" ) )
            .allMatch( path -> path.startsWith( "/content/" ) || path.startsWith( "/archive/" ) );

        final boolean isSkipSync = Boolean.parseBoolean( (String) event.getData().getOrDefault( CONTENT_SKIP_SYNC, "false" ) );

        if ( isContentEvent && !isSkipSync )
        {
            Context context = ContextBuilder.copyOf( ContextAccessor.current() ).build();
            this.executor.execute( () -> context.runWith( () -> doHandleContentEvent( nodes, event ) ) );
        }
    }

    private boolean isAllowedContentEvent( final String type )
    {
        return "node.created".equals( type ) || "node.updated".equals( type ) || "node.pushed".equals( type ) ||
            "node.duplicated".equals( type ) || "node.renamed".equals( type ) || "node.moved".equals( type ) ||
            "node.deleted".equals( type ) || "node.sorted".equals( type );
    }

    private void doHandleContentEvent( final List<Map<String, String>> nodes, final Event event )
    {
        createAdminContext().runWith( () -> {

            final Branch branch = Branch.from(
                nodes.stream().findAny().orElseThrow( () -> new IllegalArgumentException( "nodes cannot be empty" ) ).get( "branch" ) );

            if ( !ContentConstants.BRANCH_DRAFT.equals( branch ) )
            {
                return;
            }

            final List<ContentId> contentIds =
                nodes.stream().map( map -> ContentId.from( map.get( "id" ) ) ).collect( Collectors.toList() );

            final List<String> repoNames = nodes.stream().map( map -> map.get( "repo" ) ).distinct().collect( Collectors.toList() );

            if ( repoNames.size() != 1 )
            {
                throw new IllegalArgumentException( repoNames.size() > 1
                                                        ? "An event cannot contain nodes from different repositories"
                                                        : "An event must contain 'repo' property" );
            }

            final ProjectName currentProjectName = ProjectName.from( RepositoryId.from( repoNames.get( 0 ) ) );

            if ( currentProjectName == null )
            {
                return;
            }

            final Project sourceProject = this.projectService.get( currentProjectName );
            if ( sourceProject == null )
            {
                throw new ProjectNotFoundException( currentProjectName );
            }

            this.projectService.list().stream().filter( project -> project.getParents().contains( currentProjectName ) )
                .forEach( targetProject -> {

                    final ContentEventsSyncParams.Builder paramsBuilder = ContentEventsSyncParams.create()
                        .addContentIds( contentIds ).sourceProject( sourceProject.getName() ).targetProject( targetProject.getName() );

                    switch ( event.getType() )
                    {
                        case "node.created":
                        case "node.duplicated":
                            paramsBuilder.syncEventType( ContentSyncEventType.CREATED );
                            break;
                        case "node.updated":
                        case "node.pushed":
                            paramsBuilder.syncEventType( ContentSyncEventType.UPDATED );
                            break;
                        case "node.sorted":
                            paramsBuilder.syncEventType( ContentSyncEventType.SORTED );
                            break;
                        case "node.renamed":
                            paramsBuilder.syncEventType( ContentSyncEventType.RENAMED );
                            break;
                        case "node.moved":
                            paramsBuilder.syncEventType( ContentSyncEventType.MOVED );
                            break;
                        case "node.deleted":
                            paramsBuilder.syncEventType( ContentSyncEventType.DELETED );
                            break;
                        default:
                            LOG.debug( "Ignoring node type: {}", event.getType() );
                            break;
                    }
                    final ContentEventsSyncParams params = paramsBuilder.build();
                    if ( params.getSyncType() != null )
                    {
                        contentSynchronizer.sync( params );
                    }
                } );

            if ( !sourceProject.getParents().isEmpty() && "node.deleted".equals( event.getType() ) )
            {
                sourceProject.getParents()
                    .stream()
                    .filter( parentProjectName -> projectService.get( parentProjectName ) != null )
                    .forEach( parentProjectName -> contentSynchronizer.sync( ContentSyncParams.create()
                                                                                 .addContentIds( contentIds )
                                                                                 .sourceProject( parentProjectName )
                                                                                 .targetProject( sourceProject.getName() )
                                                                                 .build() )

                    );
            }
        } );
    }

    private Context createAdminContext()
    {
        final AuthenticationInfo authInfo = createAdminAuthInfo();
        return ContextBuilder.from( ContextAccessor.current() ).branch( ContentConstants.BRANCH_DRAFT ).authInfo( authInfo ).build();
    }

    private AuthenticationInfo createAdminAuthInfo()
    {
        return AuthenticationInfo.create()
            .principals( RoleKeys.ADMIN )
            .user( User.create().key( PrincipalKey.ofSuperUser() ).login( PrincipalKey.ofSuperUser().getId() ).build() )
            .build();
    }
}
