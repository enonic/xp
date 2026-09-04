package com.enonic.xp.impl.server.rest.api;

import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.fasterxml.jackson.core.JsonProcessingException;

import com.enonic.xp.content.ContentService;
import com.enonic.xp.impl.server.rest.ProjectJsonFactory;
import com.enonic.xp.portal.universalapi.UniversalApiHandler;
import com.enonic.xp.project.ProjectService;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;

/**
 * {@code server:project} - content projects with their sites.
 */
@Component(service = UniversalApiHandler.class, property = {"key=server:project", "title=Project API", "mount=management",
    "allowedPrincipals=role:system.admin"})
public class ProjectApiHandler
    extends ManagementApiHandler
{
    static final String KEY = "server:project";

    private final ProjectJsonFactory projectJsonFactory;

    @Activate
    public ProjectApiHandler( @Reference final ProjectService projectService, @Reference final ContentService contentService )
    {
        super( KEY );
        this.projectJsonFactory = new ProjectJsonFactory( projectService, contentService );

        route( HttpMethod.GET, "/", "list", this::list );
    }

    private WebResponse list( final WebRequest request, final Map<String, String> params )
        throws JsonProcessingException
    {
        return json( Map.of( "projects", projectJsonFactory.list() ) );
    }
}
