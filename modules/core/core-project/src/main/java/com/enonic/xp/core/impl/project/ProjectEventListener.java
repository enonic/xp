package com.enonic.xp.core.impl.project;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.Executors;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ProjectSynchronizer;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.core.internal.concurrent.SimpleExecutor;
import com.enonic.xp.event.Event;
import com.enonic.xp.event.EventListener;
import com.enonic.xp.project.Project;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.project.ProjectService;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.User;
import com.enonic.xp.security.auth.AuthenticationInfo;

@Component(immediate = true)
public final class ProjectEventListener
    implements EventListener
{
    private static final Logger LOG = LoggerFactory.getLogger( ProjectEventListener.class );

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

            if ( this.isAllowedProjectEvent( event.getType() ) )
            {
                this.handleProjectEvent( event );

            }
        }
    }

    private void handleProjectEvent( final Event event )
    {
        final Map<String, Object> nodes = event.getData();
        final String projectName = (String) nodes.get( ProjectEvents.PROJECT_NAME_KEY );

        this.simpleExecutor.execute( () -> createAdminContext().runWith( () -> handleProjectCreated( ProjectName.from( projectName ) ) ) );

    }

    private void handleProjectCreated( final ProjectName projectName )
    {
        final Project project = this.projectService.get( projectName );

        if ( project != null && project.getParent() != null )
        {
            final Project parentProject = this.projectService.get( project.getParent() );

            if ( parentProject != null )
            {
                projectSynchronizer.syncWithChildren( ContentPath.ROOT, parentProject, project );
            }
        }
    }

    private boolean isAllowedProjectEvent( final String type )
    {
        return ProjectEvents.CREATED_EVENT_TYPE.equals( type );
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
