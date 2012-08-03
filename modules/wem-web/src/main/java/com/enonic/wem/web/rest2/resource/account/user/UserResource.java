package com.enonic.wem.web.rest2.resource.account.user;

import java.awt.image.BufferedImage;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.store.dao.UserDao;

@Path("account/user")
@Produces(MediaType.APPLICATION_JSON)
@Component
public final class UserResource
{
    private final PhotoHelper photoHelper;

    private UserDao userDao;

    public UserResource()
        throws Exception
    {
        this.photoHelper = new PhotoHelper();
    }

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

    @GET
    @Path("{key}/photo")
    @Produces("image/png")
    public BufferedImage getPhoto( @PathParam("key") final String key, @QueryParam("size") @DefaultValue("100") final int size )
        throws Exception
    {
        final UserEntity entity = userDao.findByKey( key );
        if ( entity == null )
        {
            return null;
        }

        return this.photoHelper.renderPhoto( entity, size );
    }

    @Autowired
    public void setUserDao( final UserDao userDao )
    {
        this.userDao = userDao;
    }
}
