package com.enonic.xp.core.impl.content;

import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.event.Event;
import com.enonic.xp.event.EventListener;
import com.enonic.xp.project.Project;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.project.ProjectService;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.User;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.task.TaskService;

@Component(immediate = true)
public final class ProjectEventListener
    implements EventListener
{
    private static final Logger LOG = LoggerFactory.getLogger( ProjectEventListener.class );

    private final ProjectService projectService;

    private final TaskService taskService;

    private final ContentSynchronizer contentSynchronizer;

    @Activate
    public ProjectEventListener( @Reference final ProjectService projectService, @Reference final TaskService taskService,
                                 @Reference final ContentSynchronizer contentSynchronizer )
    {
        this.projectService = projectService;
        this.taskService = taskService;
        this.contentSynchronizer = contentSynchronizer;
    }

    @Override
    public void onEvent( final Event event )
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

    private void handleProjectEvent( final Event event )
    {
        final Map<String, Object> nodes = event.getData();
        final String projectName = (String) nodes.get( "name" );

        createAdminContext().runWith( () -> handleProjectCreated( ProjectName.from( projectName ) ) );

    }

    private void handleProjectCreated( final ProjectName projectName )
    {
        final Project project = this.projectService.get( projectName );

        if ( project != null && project.getParent() != null )
        {
            final Project parentProject = this.projectService.get( project.getParent() );

            if ( parentProject != null )
            {
                final ContentSyncTask syncTask = ContentSyncTask.create().
                    sourceProject( parentProject.getName() ).
                    targetProject( project.getName() ).
                    contentSynchronizer( contentSynchronizer ).
                    build();

                taskService.submitTask( syncTask,
                                        String.format( "sync [%s] project from parent [%s]", project.getName(), parentProject.getName() ) );
            }
        }
    }

    private boolean isAllowedProjectEvent( final String type )
    {
        return "project.created".equals( type );
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
}
