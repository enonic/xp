package com.enonic.wem.web.rest2.resource.userstore;

import java.util.List;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.wem.web.rest2.service.userstore.UserStoreUpdateService;

import com.enonic.cms.core.security.SecurityService;
import com.enonic.cms.core.security.user.QualifiedUsername;
import com.enonic.cms.core.security.user.User;
import com.enonic.cms.core.security.userstore.UserStoreEntity;
import com.enonic.cms.store.dao.UserStoreDao;

@Path("userstore")
@Produces(MediaType.APPLICATION_JSON)
@Component
public class UserStoreResource
{
    private UserStoreDao userStoreDao;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private UserStoreUpdateService userStoreUpdateService;

    @GET
    public UserStoreResults getAll()
    {
        final List<UserStoreEntity> userStores = userStoreDao.findAll();
        return new UserStoreResults( userStores );
    }

    @POST
    @Path("{key}")
    public Response updateUserstore( @PathParam("key") String key, @FormParam("name") String name,
                                     @FormParam("defaultUserstore") Boolean defaultUserstore, @FormParam("config") String config,
                                     @FormParam("connector") String connector, @FormParam("administrators") List<String> administrators )
    {
        User user = securityService.getUser( new QualifiedUsername( "system", "admin" ) );
        if ( user == null )
        {
            //Anonymous users can't create or update userstores
            return Response.status( Response.Status.FORBIDDEN ).build();
        }

        UserStoreEntity duplicate = userStoreDao.findByName( name );
        if ( duplicate != null && key.equals( duplicate.getKey().toString() ) )
        {

            //The userstore with such name already exists
            return Response.status( Response.Status.NOT_ACCEPTABLE ).build();
        }

        userStoreUpdateService.updateUserStore( user, key, name, defaultUserstore, config, connector, administrators );
        return Response.ok().build();
    }

    @Autowired
    public void setUserStoreDao( final UserStoreDao userStoreDao )
    {
        this.userStoreDao = userStoreDao;
    }

    public void setSecurityService( final SecurityService securityService )
    {
        this.securityService = securityService;
    }

    public void setUserStoreUpdateService( final UserStoreUpdateService userStoreUpdateService )
    {
        this.userStoreUpdateService = userStoreUpdateService;
    }
}
