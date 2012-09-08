package com.enonic.wem.web.rest2.resource.account.user;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.wem.web.rest.account.UserModel;
import com.enonic.wem.web.rest2.resource.account.AccountGenericResult;
import com.enonic.wem.web.rest2.service.account.user.UserUpdateService;

import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.store.dao.UserDao;

@Path("account/user")
@Produces(MediaType.APPLICATION_JSON)
@Component
public final class UserResource
{
    private UserDao userDao;

    private UserUpdateService userUpdateService;

    @GET
    @Path("{key}")
    public UserResult getInfo( @PathParam("key") final String key )
    {
        UserEntity entity = userDao.findByKey( key );
        if ( entity == null )
        {
            return null;
        }
        else
        {
            return new UserResult( entity );
        }
    }

    @POST
    @Path("{key}/update")
    public UserUpdateResult updateUser( @PathParam("key") String userKey, UserModel user )
    {
        return userUpdateService.updateUser( userKey, user );
    }

    @Autowired
    public void setUserDao( final UserDao userDao )
    {
        this.userDao = userDao;
    }

    @Autowired
    public void setUserUpdateService( final UserUpdateService userUpdateService )
    {
        this.userUpdateService = userUpdateService;
    }
}
