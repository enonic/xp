package com.enonic.wem.web.rest2.resource.account.user;

import java.awt.image.BufferedImage;
import java.util.List;

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

import com.enonic.wem.web.rest.account.UserIdGenerator;
import com.enonic.wem.web.rest2.resource.account.AccountGenericResult;

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
    private UserDao userDao;

    private UserStoreDao userStoreDao;

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
    @Path("{key}/image")
    @Produces("image/png")
    public BufferedImage getPhoto( @PathParam("key") final String key, @QueryParam("size") @DefaultValue("100") final int size )
        throws Exception
    {
        final UserEntity entity = userDao.findByKey( key );
        if ( entity == null )
        {
            return null;
        }

        return PhotoHelper.renderPhoto( entity, size );
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

    @GET
    @Path("/suggest-name")
    public NameSuggestionResult suggestUsername( @QueryParam("firstname") @DefaultValue("") final String firstName,
                                                 @QueryParam("lastname") @DefaultValue("") final String lastName,
                                                 @QueryParam("userstore") @DefaultValue("") final String userStoreName )
    {
        final UserStoreEntity store = userStoreDao.findByName( userStoreName );
        if ( store == null )
        {
            return null;
        }

        final UserIdGenerator userIdGenerator = new UserIdGenerator( userDao );
        final String suggestedUserName = userIdGenerator.generateUserId( firstName.trim(), lastName.trim(), store.getKey() );

        return new NameSuggestionResult( suggestedUserName );
    }

    @POST
    @Path("{key}/change-password")
    public AccountGenericResult changePassword( @PathParam("key") final String userKey, @FormParam("newPassword") final String newPassword )
    {

        AccountGenericResult result;
        if ( newPassword.length() <= 64 && newPassword.length() >= 8 )
        {
            result = new AccountGenericResult( true );
        }
        else
        {
            result = new AccountGenericResult( false, "Password is out of possible length" );
        }
        return result;
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
    public void setUserStoreDao( final UserStoreDao userStoreDao )
    {
        this.userStoreDao = userStoreDao;
    }
}
