package com.enonic.wem.web.rest2.resource.account.user;

import java.awt.image.BufferedImage;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.wem.web.rest.account.UserPhotoService;

import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.store.dao.UserDao;

@Path("account/user")
@Produces(MediaType.APPLICATION_JSON)
@Component
public final class UserResource
{

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserPhotoService photoService;

    @GET
    @Path("{key}")
    public UserResult getInfo( @PathParam("key") final String key )
    {
        // TODO: Implementation here. Do not implement "account graph" since this will be implemented elsewhere
        return null;
    }

    @GET
    @Path("{key}/photo")
    @Produces("image/png")
    public Response getPhoto( @PathParam("key") final String key )
        throws Exception
    {
        UserEntity entity = userDao.findByKey( key );
        if ( entity == null )
        {
            return null;
        }
        if ( entity.getPhoto() == null )
        {
            entity = userDao.findBuiltInAnonymousUser();
        }
        BufferedImage image = photoService.renderPhoto( entity, 100 );
        return Response.ok( image ).build();
    }

    public UserDao getUserDao()
    {
        return userDao;
    }

    public void setUserDao( final UserDao userDao )
    {
        this.userDao = userDao;
    }

    public UserPhotoService getPhotoService()
    {
        return photoService;
    }

    public void setPhotoService( final UserPhotoService photoService )
    {
        this.photoService = photoService;
    }
}
