package com.enonic.xp.core.impl.project;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentService;
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
public class ProjectNodeEventListener
    implements EventListener
{
    private final static Logger LOG = LoggerFactory.getLogger( ProjectNodeEventListener.class );

    private ProjectService projectService;

    private ContentService contentService;

    private SimpleExecutor simpleExecutor;

    @Activate
    public void activate()
    {
        this.simpleExecutor = new SimpleExecutor( Executors::newSingleThreadExecutor, "project-node-sync-thread",
                                                  e -> LOG.error( "Project node sync failed", e ) );
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

            this.doHandleEvent( event );
        }
    }

    private void doHandleEvent( final Event event )
    {
        final String type = event.getType();

        switch ( type )
        {
            case "node.created":
                handleEventType( event );
                break;
            case "node.updated":
                handleEventType( event );
                break;
        }
    }

    private void handleEventType( final Event event )
    {
        try
        {
            final List<Map<String, String>> nodes = (List<Map<String, String>>) event.getData().get( "nodes" );

            nodes.forEach( nodeMap -> {
                if ( nodeMap.get( "path" ).startsWith( "/content/" ) )
                {
                    this.simpleExecutor.execute( () -> handleContentEvent( nodeMap ) );
                }
            } );
        }
        catch ( Exception e )
        {
            LOG.error( "Not able to handle node-event", e );
        }
    }

    private void handleContentEvent( final Map<String, String> nodeMap )
    {
        createAdminContext().runWith( () -> {

            final ProjectName currentProjectName = ProjectName.from( RepositoryId.from( nodeMap.get( "repo" ) ) );

            final Project sourceProject = this.projectService.list().
                stream().
                filter( project -> currentProjectName.equals( project.getName() ) ).
                findAny().
                orElse( null );

            this.projectService.list().
                stream().
                filter( project -> currentProjectName.equals( project.getParent() ) ).
                forEach( targetProject -> {
                    final ParentProjectSynchronizer parentProjectSynchronizer = ParentProjectSynchronizer.create().
                        targetProject( targetProject ).
                        sourceProject( sourceProject ).
                        contentService( contentService ).build();

                    parentProjectSynchronizer.sync( ContentId.from( nodeMap.get( "id" ) ) );

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
    public void setContentService( final ContentService contentService )
    {
        this.contentService = contentService;
    }
}
