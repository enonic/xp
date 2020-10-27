package com.enonic.xp.core.impl.content;

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

    private ContentSynchronizer contentSynchronizer;

    @Activate
    public ProjectEventListener( @Reference final ProjectService projectService, @Reference final ContentSynchronizer contentSynchronizer )
    {
        this.projectService = projectService;
        this.contentSynchronizer = contentSynchronizer;

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
        final String projectName = (String) nodes.get( "name" );

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
                contentSynchronizer.sync( ContentSyncParams.create().
                    sourceProject( parentProject.getName() ).
                    targetProject( project.getName() ).
                    build() );
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
