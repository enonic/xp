package com.enonic.xp.impl.server.rest.api;

import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.fasterxml.jackson.core.JsonProcessingException;

import com.enonic.xp.impl.server.rest.model.RepositoriesJson;
import com.enonic.xp.impl.server.rest.model.RepositoryJson;
import com.enonic.xp.portal.universalapi.UniversalApiHandler;
import com.enonic.xp.repository.Repository;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositoryService;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;

/**
 * {@code server:repo} - the repository inventory.
 */
@Component(service = UniversalApiHandler.class, property = {"key=server:repo", "title=Repository API", "mount=management",
    "allowedPrincipals=role:system.admin"})
public class RepositoryApiHandler
    extends ManagementApiHandler
{
    static final String KEY = "server:repo";

    private final RepositoryService repositoryService;

    @Activate
    public RepositoryApiHandler( @Reference final RepositoryService repositoryService )
    {
        super( KEY );
        this.repositoryService = repositoryService;

        route( HttpMethod.GET, "/", "list", this::list );
        route( HttpMethod.GET, "/{repo}", "get", this::get );
    }

    private WebResponse list( final WebRequest request, final Map<String, String> params )
        throws JsonProcessingException
    {
        return json( RepositoriesJson.create( repositoryService.list() ) );
    }

    private WebResponse get( final WebRequest request, final Map<String, String> params )
        throws JsonProcessingException
    {
        final Repository repository = repositoryService.get( RepositoryId.from( params.get( "repo" ) ) );
        if ( repository == null )
        {
            return error( HttpStatus.NOT_FOUND, String.format( "Repository [%s] not found", params.get( "repo" ) ) );
        }
        return json( RepositoryJson.create( repository ) );
    }
}
