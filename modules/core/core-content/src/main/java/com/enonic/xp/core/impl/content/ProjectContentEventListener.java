package com.enonic.xp.core.impl.content;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ProjectSynchronizer;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.core.internal.concurrent.SimpleExecutor;
import com.enonic.xp.event.Event;
import com.enonic.xp.event.EventListener;
import com.enonic.xp.project.Project;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.project.ProjectService;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.User;
import com.enonic.xp.security.auth.AuthenticationInfo;

@Component(immediate = true)
public final class ProjectContentEventListener
    implements EventListener
{
    private static final Logger LOG = LoggerFactory.getLogger( ProjectContentEventListener.class );

    private ProjectService projectService;

    private SimpleExecutor simpleExecutor;

    private ProjectSynchronizer projectSynchronizer;

    @Activate
    public void activate()
    {
        this.simpleExecutor = new SimpleExecutor( Executors::newSingleThreadExecutor, "project-node-sync-thread",
                                                  e -> LOG.error( "Project node sync failed", e ) );
    }

    @Deactivate
    public void deactivate()
    {
        this.simpleExecutor.shutdownAndAwaitTermination( Duration.ZERO, neverCommenced -> {
        } );
    }

    @Override
    public void onEvent( final Event event )
    {
        if ( event != null )
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
    }

    private void handleContentEvent( final Event event )
    {
        try
        {
            final List<Map<String, String>> nodes = (List<Map<String, String>>) event.getData().get( "nodes" );

            nodes.forEach( nodeMap -> {
                if ( nodeMap.get( "path" ).startsWith( "/content/" ) )
                {
                    this.simpleExecutor.execute( () -> handleContentEvent( nodeMap, event.getType() ) );
                }
            } );
        }
        catch ( Exception e )
        {
            LOG.error( "Not able to handle node-event", e );
        }
    }

    private boolean isAllowedContentEvent( final String type )
    {
        return "node.created".equals( type ) || "node.updated".equals( type ) || "node.pushed".equals( type ) ||
            "node.renamed".equals( type ) || "node.moved".equals( type ) || "node.deleted".equals( type ) || "node.sorted".equals( type ) ||
            "node.manualOrderUpdated".equals( type );
    }

    private void handleContentEvent( final Map<String, String> nodeMap, final String type )
    {
        createAdminContext().runWith( () -> {

            final ProjectName currentProjectName = ProjectName.from( RepositoryId.from( nodeMap.get( "repo" ) ) );

            final Project sourceProject = this.projectService.list().
                stream().
                filter( project -> currentProjectName.equals( project.getName() ) ).
                findAny().
                orElseThrow( () -> new ProjectNotFoundException( currentProjectName ) );

            this.projectService.list().
                stream().
                filter( project -> currentProjectName.equals( project.getParent() ) ).
                forEach( targetProject -> {
                    final ContentId contentId = ContentId.from( nodeMap.get( "id" ) );

                    switch ( type )
                    {
                        case "node.created":
                            projectSynchronizer.syncCreated( contentId, sourceProject, targetProject );
                            break;
                        case "node.updated":
                            projectSynchronizer.syncUpdated( contentId, sourceProject, targetProject );
                            break;
                        case "node.pushed":
                            projectSynchronizer.syncUpdated( contentId, sourceProject, targetProject );
                            break;
                        case "node.manualOrderUpdated":
                            projectSynchronizer.syncManualOrderUpdated( contentId, sourceProject, targetProject );
                            break;
                        case "node.sorted":
                            projectSynchronizer.syncSorted( contentId, sourceProject, targetProject );
                            break;
                        case "node.renamed":
                            projectSynchronizer.syncRenamed( contentId, sourceProject, targetProject );
                            break;
                        case "node.moved":
                            projectSynchronizer.syncMoved( contentId, sourceProject, targetProject );
                            break;
                        case "node.deleted":
                            projectSynchronizer.syncDeleted( contentId, sourceProject, targetProject );
                            break;
                    }

                } );

            this.projectService.list().
                stream().
                filter( project -> project.getName().equals( sourceProject.getParent() ) ).
                forEach( parentProject -> {


                    final ContentId contentId = ContentId.from( nodeMap.get( "id" ) );

                    switch ( type )
                    {
                        case "node.deleted":
                            projectSynchronizer.syncWithChildren( contentId, parentProject, sourceProject );
                            break;
                    }

                } );
        } );
    }

    private Context createAdminContext()
    {
        final AuthenticationInfo authInfo = createAdminAuthInfo();
        return ContextBuilder.from( ContentConstants.CONTEXT_DRAFT ).
            authInfo( authInfo ).
            build();
    }

    private AuthenticationInfo createAdminAuthInfo()
    {
        return AuthenticationInfo.create().
            principals( RoleKeys.ADMIN ).
            user( User.create().
                key( PrincipalKey.ofSuperUser() ).
                login( PrincipalKey.ofSuperUser().getId() ).
                build() ).
            build();
    }

    @Reference
    public void setProjectService( final ProjectService projectService )
    {
        this.projectService = projectService;
    }

    @Reference
    public void setProjectSynchronizer( final ProjectSynchronizer projectSynchronizer )
    {
        this.projectSynchronizer = projectSynchronizer;
    }
}
