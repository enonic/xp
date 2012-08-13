package com.enonic.wem.web.rest2.resource.account.user;

import java.awt.image.BufferedImage;
import java.util.List;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.wem.web.rest2.service.account.user.UserGraphService;

import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.user.UserKey;
import com.enonic.cms.core.security.user.UserSpecification;
import com.enonic.cms.core.security.userstore.UserStoreEntity;
import com.enonic.cms.core.security.userstore.UserStoreKey;
import com.enonic.cms.store.dao.UserDao;
import com.enonic.cms.store.dao.UserStoreDao;

@Path("account/user")
@Produces(MediaType.APPLICATION_JSON)
@Component
public final class UserResource
{
    private final PhotoHelper photoHelper;

    private UserDao userDao;

    private UserGraphService userGraphService;

    private UserStoreDao userStoreDao;

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

    @GET
    @Path("/verify-unique-email")
    public UniqueEmailResult verifyUniqueEmail( @QueryParam("userstore") @DefaultValue("") final String userStoreName,
                                                @QueryParam("email") @DefaultValue("") final String email )
    {
        final UserStoreEntity userStore = userStoreDao.findByName( userStoreName );
        if ( userStore == null )
        {
            return new UniqueEmailResult( false );
        }

        final UserKey existingUserWithEmail = findUserByEmail( userStore.getKey(), email );
        if ( existingUserWithEmail == null )
        {
            return new UniqueEmailResult( false );
        }
        else
        {
            return new UniqueEmailResult( true, existingUserWithEmail.toString() );
        }
    }

    private UserKey findUserByEmail( final UserStoreKey userStoreKey, final String email )
    {
        final UserSpecification userByEmailSpec = new UserSpecification();
        userByEmailSpec.setEmail( email );
        userByEmailSpec.setUserStoreKey( userStoreKey );
        userByEmailSpec.setDeletedStateNotDeleted();

        final List<UserEntity> usersWithThisEmail = userDao.findBySpecification( userByEmailSpec );

        if ( usersWithThisEmail.size() == 0 )
        {
            return null;
        }
        else
        {
            return usersWithThisEmail.get( 0 ).getKey();
        }
    }

    @Autowired
    public void setUserDao( final UserDao userDao )
    {
        this.userDao = userDao;
    }

    @Autowired
    public void setUserGraphService( final UserGraphService userGraphService )
    {
        this.userGraphService = userGraphService;
    }

    @Autowired
    public void setUserStoreDao( final UserStoreDao userStoreDao )
    {
        this.userStoreDao = userStoreDao;
    }
}
