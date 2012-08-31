package com.enonic.wem.web.rest.account;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.enonic.wem.core.search.account.AccountSearchService;
import com.enonic.wem.web.rest.common.RestResponse;

import com.enonic.cms.core.mail.MessageSettings;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.user.UserKey;
import com.enonic.cms.core.security.user.UserSpecification;
import com.enonic.cms.core.security.userstore.UserStoreEntity;
import com.enonic.cms.core.security.userstore.UserStoreService;
import com.enonic.cms.store.dao.UserDao;
import com.enonic.cms.store.dao.UserStoreDao;


@Controller
@RequestMapping(value = "/account/", produces = MediaType.APPLICATION_JSON_VALUE)
public final class AccountController
{
    private static final Logger LOG = LoggerFactory.getLogger( AccountController.class );

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserStoreDao userStoreDao;

    @Autowired
    private UserStoreService userStoreService;

    @Autowired
    private AccountSearchService searchService;


    @RequestMapping(value = "suggestusername", method = RequestMethod.GET)
    @ResponseBody
    // TODO: Port to rest2 under UserResource (account/user/suggest-name)
    public ResponseEntity suggestUsername( @RequestParam(value = "firstname", defaultValue = "") final String firstName,
                                           @RequestParam(value = "lastname", defaultValue = "") final String lastName,
                                           @RequestParam(value = "userstore", defaultValue = "") final String userStoreName )
    {
        final UserIdGenerator userIdGenerator = new UserIdGenerator( userDao );

        final UserStoreEntity store = userStoreDao.findByName( userStoreName );

        if ( store == null )
        {
            return new ResponseEntity( HttpStatus.NOT_FOUND );
        }

        final String suggestedUserName = userIdGenerator.generateUserId( firstName.trim(), lastName.trim(), store.getKey() );
        final UserRestResponse response = new UserRestResponse();
        response.setUsername( suggestedUserName );
        return new ResponseEntity( response, HttpStatus.OK );
    }

    @RequestMapping(value = "userkey", method = RequestMethod.GET)
    @ResponseBody
    // TODO: Is this in use?
    public ResponseEntity getUserKeyByUserName( @RequestParam(value = "userstore", defaultValue = "") final String userStoreName,
                                                @RequestParam(value = "username", defaultValue = "") final String userName )
    {
        final UserRestResponse response = new UserRestResponse();
        UserStoreEntity userStore = userStoreDao.findByName( userStoreName );
        if ( userStore == null )
        {
            return new ResponseEntity( HttpStatus.NOT_FOUND );
        }
        else
        {
            UserEntity user = userDao.findByUserStoreKeyAndUsername( userStore.getKey(), userName );
            if ( user != null )
            {
                response.setUserkey( user.getKey().toString() );
            }
//            else
//            {
//                response.put( "userkey", null );
//            }
        }

        return new ResponseEntity( response, HttpStatus.OK );
    }

    @RequestMapping(value = "notify", method = RequestMethod.POST)
    @ResponseBody
    // TODO: This should be a more generic service. Let's wait.
    public ResponseEntity sendNotificationEmail( @RequestParam(value = "to", defaultValue = "") final String to,
                                                 @RequestParam(value = "cc", defaultValue = "") final String cc,
                                                 @RequestParam(value = "subject", defaultValue = "") final String subject,
                                                 @RequestParam(value = "message", defaultValue = "") final String message )
    {
        final RestResponse response = new RestResponse();
        UserEntity currentUser = getCurrentUser();
        MessageSettings messageSetting = new MessageSettings();
        messageSetting.setBody( message );
        messageSetting.setFromName( currentUser.getDisplayName() );
        messageSetting.setSubject( subject );
        messageSetting.setFromMail( currentUser.getEmail() );

        // TODO: Disable mail sending for now.
        // sendMailService.sendNotificationMail( to, cc, messageSetting );

        response.setStatus( "ok" );
        return new ResponseEntity( response, HttpStatus.OK );
    }

    private UserEntity getCurrentUser()
    {
        return userDao.findBuiltInEnterpriseAdminUser();
    }
}
