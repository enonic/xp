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

import com.enonic.wem.web.rest.account.UserIdGenerator;
import com.enonic.wem.web.rest.account.UserModel;
import com.enonic.wem.web.rest2.resource.account.AccountGenericResult;
import com.enonic.wem.web.rest2.service.account.user.UserUpdateService;

import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.userstore.UserStoreEntity;
import com.enonic.cms.store.dao.UserDao;
import com.enonic.cms.store.dao.UserStoreDao;

@Path("account/user")
@Produces(MediaType.APPLICATION_JSON)
@Component
public final class UserResource
{
    private UserDao userDao;

    private UserStoreDao userStoreDao;

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
    public void setUserStoreDao( final UserStoreDao userStoreDao )
    {
        this.userStoreDao = userStoreDao;
    }

    @Autowired
    public void setUserUpdateService( final UserUpdateService userUpdateService )
    {
        this.userUpdateService = userUpdateService;
    }
}
