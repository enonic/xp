package com.enonic.cms.web.rest.account;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.enonic.cms.core.mail.MessageSettings;
import com.enonic.cms.core.mail.SendMailService;
import com.enonic.wem.core.search.Facet;
import com.enonic.wem.core.search.FacetEntry;
import com.enonic.wem.core.search.Facets;
import com.enonic.wem.core.search.SearchSortOrder;
import com.enonic.wem.core.search.account.AccountIndexField;
import com.enonic.wem.core.search.account.AccountKey;
import com.enonic.wem.core.search.account.AccountSearchHit;
import com.enonic.wem.core.search.account.AccountSearchQuery;
import com.enonic.wem.core.search.account.AccountSearchResults;
import com.enonic.wem.core.search.account.AccountSearchService;
import com.enonic.wem.core.search.account.AccountType;
import com.enonic.cms.core.security.group.DeleteGroupCommand;
import com.enonic.cms.core.security.group.GroupEntity;
import com.enonic.cms.core.security.group.GroupKey;
import com.enonic.cms.core.security.group.GroupSpecification;
import com.enonic.cms.core.security.user.DeleteUserCommand;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.user.UserKey;
import com.enonic.cms.core.security.user.UserSpecification;
import com.enonic.cms.core.security.userstore.UserStoreEntity;
import com.enonic.cms.core.security.userstore.UserStoreKey;
import com.enonic.cms.core.security.userstore.UserStoreService;
import com.enonic.cms.store.dao.GroupDao;
import com.enonic.cms.store.dao.UserDao;
import com.enonic.cms.store.dao.UserStoreDao;
import com.enonic.cms.store.support.EntityPageList;
import com.enonic.cms.web.rest.common.RestResponse;


@Controller
@RequestMapping(value = "/account/", produces = MediaType.APPLICATION_JSON_VALUE)
public final class AccountController
{
    private static final Logger LOG = LoggerFactory.getLogger( AccountController.class );

    private static final String SEPARATOR_PARAM_COMMA = "c";

    private static final String SEPARATOR_PARAM_SEMICOLON = "s";

    private static final String SEPARATOR_PARAM_TAB = "t";

    @Autowired
    private UserDao userDao;

    @Autowired
    private GroupDao groupDao;

    @Autowired
    private UserStoreDao userStoreDao;

    @Autowired
    private UserStoreService userStoreService;

    @Autowired
    private AccountSearchService searchService;

    @Autowired
    private AccountModelTranslator accountTranslator;

    @Autowired
    private SendMailService sendMailService;

    @RequestMapping(value = "search", method = RequestMethod.GET)
    @ResponseBody
    public RestResponse<AccountsModel> list( final AccountLoadRequest req,
                                     @RequestParam(value = "key", defaultValue = "") final String accountKeys )
    {
        if ( LOG.isInfoEnabled() )
        {
            LOG.info( "Search accounts: query='" + req.getQuery() + "' , index=" + req.getStart() + ", count=" +
                          req.getLimit() + ", selectUsers=" + req.isSelectUsers() + ", selectGroups=" +
                          req.isSelectGroups() + ", userstores=" + req.getUserstores() + ", orgs=" +
                          req.getOrganizations() + ", keys=" + accountKeys );
        }

        final AccountSearchResults searchResults;
        if ( StringUtils.isNotBlank( accountKeys ) )
        {
            searchResults = findAccountsByKey( accountKeys );
        }
        else
        {
            searchResults = search( req );
        }

        final List list = new ArrayList();
        GroupKey currentGroupKey = new GroupKey( req.getCurrentGroupKey() );
        for ( AccountSearchHit searchHit : searchResults )
        {
            switch ( searchHit.getAccountType() )
            {
                case ROLE:
                case GROUP:
                    GroupEntity groupEntity = this.groupDao.findByKey( new GroupKey( searchHit.getKey().toString() ) );
                    if ( groupEntity != null && !groupEntity.getGroupKey().equals( currentGroupKey ) )
                    {
                        list.add( groupEntity );
                    }
                    break;

                case USER:
                    UserEntity userEntity = this.userDao.findByKey( searchHit.getKey().toString() );
                    if ( userEntity != null )
                    {
                        list.add( userEntity );
                    }
                    break;
            }
        }

        final EntityPageList accountList = new EntityPageList( searchResults.getCount(), searchResults.getTotal(), list );
        AccountsModel accountsModel = accountTranslator.toInfoModel( accountList );

        setFacets( accountsModel, searchResults );

        RestResponse<AccountsModel> result = new RestResponse<AccountsModel>();
        result.setResults( accountsModel );
        result.setSuccess( true );
        return result;
    }

    private AccountSearchResults findAccountsByKey( String accountKeys )
    {
        final String[] keys = accountKeys.split( "," );
        final AccountSearchResults searchResults = new AccountSearchResults( 0, keys.length );
        for ( String key : keys )
        {
            key = key.trim();
            final AccountType type = findAccountType( key );
            searchResults.add( new AccountKey( key ), type, 1 );
        }
        return searchResults;
    }

    private AccountSearchResults search( final AccountLoadRequest req )
    {
        final String userstores = req.getUserstores();
        String[] userstoreList = ( userstores == null ) ? new String[0] : userstores.split( "," );

        final String organizations = req.getOrganizations();
        final String[] organizationList = ( organizations == null ) ? new String[0] : organizations.split( "," );

        AccountSearchQuery searchQueryCountFacets =
                new AccountSearchQuery().setIncludeResults( true ).setCount( req.getLimit() ).setFrom(
                        req.getStart() ).setQuery( req.getQuery() ).setGroups( req.isSelectGroups() ).setUsers(
                        req.isSelectUsers() ).setRoles( req.isSelectRoles() ).setOrganizations(
                        organizationList ).setSortField( AccountIndexField.parse( req.getSort() ) ).setSortOrder(
                        SearchSortOrder.valueOf( req.getDir() ) );

        String currentGroupKey =  req.getCurrentGroupKey();

        // override to return accounts from the same userstore only if editing group form remote userstore
        if ( StringUtils.isNotEmpty( currentGroupKey ) ) {
            GroupEntity currentGroup = groupDao.find( currentGroupKey );
            UserStoreEntity currentUserstore = currentGroup != null ? currentGroup.getUserStore() : null;
            if ( currentUserstore != null && currentUserstore.isRemote() ) {
                userstoreList = new String[] { currentUserstore.getName() };
            }
        }

        searchQueryCountFacets.setUserStores( userstoreList );

        final AccountSearchResults searchResults = searchService.search( searchQueryCountFacets );
        return searchResults;
    }

    private void setFacets( AccountsModel accountsModel, AccountSearchResults searchResults )
    {
        final Facets facets = searchResults.getFacets();
        facets.consolidate();

        for ( Facet facet : facets )
        {
            final SearchFacetModel searchFacetModel = new SearchFacetModel( facet.getName() );
            for ( FacetEntry facetEntry : facet )
            {
                searchFacetModel.setEntryCount( facetEntry.getTerm(), facetEntry.getCount() );
            }
            accountsModel.addFacet( searchFacetModel );
        }
    }

    /**
     * TODO: This could be set to GET method instead.
     */
    @RequestMapping(value = "export", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<byte[]> exportAsCsv( final AccountExportRequest req,
                                  @RequestParam(value = "encoding", defaultValue = "ISO-8859-1") String characterEncoding,
                                  @RequestParam(value = "separator", defaultValue = SEPARATOR_PARAM_TAB) String separator )
        throws UnsupportedEncodingException
    {
        final int accountsExportLimit = 5000;

        final AccountSearchResults searchResults;
        if ( req.getKeys().size() > 0 )
        {
            searchResults = getAccountListForKeys( req.getKeys() );
        }
        else
        {
            req.setLimit( accountsExportLimit );
            searchResults = search( req );
        }
        final AccountsCsvExport csvExport = new AccountsCsvExport( groupDao, userDao );
        final String separatorChar;
        if ( SEPARATOR_PARAM_COMMA.equals( separator ) )
        {
            separatorChar = ",";
        }
        else if ( SEPARATOR_PARAM_SEMICOLON.equals( separator ) )
        {
            separatorChar = ";";
        }
        else
        {
            separatorChar = "\t";
        }

        csvExport.setSeparator( separatorChar );
        final String content = csvExport.generateCsv( searchResults );
        final String filename = csvExport.getExportFileName( new Date() );
        final String attachmentHeader = "attachment; filename=" + filename;

        final byte[] data = content.getBytes( characterEncoding );
        HttpHeaders headers = new HttpHeaders();
        headers.add( "Content-Type",  "text/csv; charset=" + characterEncoding);
        headers.add( "Content-Encoding",  characterEncoding);
        headers.add( "Content-Disposition",  attachmentHeader);
        ResponseEntity<byte[]> responseEntity =  new ResponseEntity<byte[]>( data, headers, HttpStatus.OK );
        return responseEntity;
    }

    private AccountSearchResults getAccountListForKeys( final List<String> keys )
    {
        // TODO: refactor this when accounts API and model classes are in place
        final AccountSearchResults accounts = new AccountSearchResults( 0, keys.size() );
        for ( final String key : keys )
        {
            final AccountType type = findAccountType( key );
            final AccountSearchHit account = new AccountSearchHit( new AccountKey( key ), type, 0 );
            accounts.add( account );
        }
        return accounts;
    }

    @RequestMapping(value = "suggestusername", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity suggestUsername( @RequestParam(value = "firstname", defaultValue = "") final String firstName,
                                     @RequestParam(value = "lastname", defaultValue = "") final String lastName,
                                     @RequestParam(value = "userstore", defaultValue = "") final String userStoreName )
    {
        final UserIdGenerator userIdGenerator = new UserIdGenerator( userDao );

        final UserStoreEntity store = userStoreDao.findByName( userStoreName );

        if ( store == null )
        {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }

        final String suggestedUserName = userIdGenerator.generateUserId( firstName.trim(), lastName.trim(), store.getKey() );
        final UserRestResponse response = new UserRestResponse();
        response.setUsername( suggestedUserName );
        return new ResponseEntity( response, HttpStatus.OK );
    }

    @RequestMapping(value = "groupinfo", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity getGroupinfo( @RequestParam(value = "key", defaultValue = "") final String groupKey )
    {
        GroupEntity group = groupDao.find( groupKey );
        if ( group == null )
        {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        AccountModel groupModel = accountTranslator.toInfoModel( group );
        final GroupRestResponse response = new GroupRestResponse();
        response.setGroup( groupModel );
        return new ResponseEntity( response, HttpStatus.OK );
    }

    @RequestMapping(value = "verifyUniqueEmail", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity verifyUniqueEmail( @RequestParam(value = "userstore", defaultValue = "") final String userStoreName,
                                       @RequestParam(value = "email", defaultValue = "")  final String email)
    {
        final UserRestResponse response = new UserRestResponse();

        final UserStoreEntity userStore = userStoreDao.findByName( userStoreName );
        if ( userStore == null )
        {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        else
        {
            final UserKey existingUserWithEmail = findUserByEmail( userStore.getKey(), email );

            if ( existingUserWithEmail == null )
            {
                response.setEmailInUse( false );
            }
            else
            {
                response.setEmailInUse( true );
                response.setUserkey( existingUserWithEmail.toString() );
            }
        }
        return new ResponseEntity( response, HttpStatus.OK );
    }

    @RequestMapping(value = "userkey", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity getUserKeyByUserName( @RequestParam(value = "userstore", defaultValue = "") final String userStoreName,
                                          @RequestParam(value = "username", defaultValue = "" ) final String userName )
    {
        final UserRestResponse response = new UserRestResponse();
        UserStoreEntity userStore = userStoreDao.findByName( userStoreName );
        if ( userStore == null )
        {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
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

    @RequestMapping(value = "delete", method = RequestMethod.POST)
    @ResponseBody
    public RestResponse deleteAccount( @RequestParam("key") final List<String> keys )
    {
        final RestResponse res = new RestResponse();
        boolean success = true;

        final UserEntity deleter = getCurrentUser();
        for ( String accountKey : keys )
        {
            try
            {
                final AccountType type = findAccountType( accountKey );
                switch ( type )
                {
                    case USER:
                        final UserSpecification userSpec = new UserSpecification();
                        userSpec.setKey( new UserKey( accountKey ) );
                        final DeleteUserCommand deleteUserCommand = new DeleteUserCommand( deleter.getKey(), userSpec );
                        userStoreService.deleteUser( deleteUserCommand );
                        LOG.info( "User deleted: " + accountKey );
                        break;

                    case GROUP:
                        final GroupSpecification groupSpec = new GroupSpecification();
                        groupSpec.setKey( new GroupKey( accountKey ) );
                        final DeleteGroupCommand deleteGroupCommand = new DeleteGroupCommand( deleter, groupSpec );
                        userStoreService.deleteGroup( deleteGroupCommand );

                        LOG.info( "Group deleted: " + accountKey );
                        break;
                }
                removeAccountIndex( accountKey );
            }
            catch ( Exception e )
            {
                LOG.error( "Unable to delete account: " + accountKey, e );
                success = false;
                res.setError( "Unable to delete account with key '" + accountKey + "'" );
                break;
            }
        }

        res.setSuccess( success );
        return res;
    }

    private void removeAccountIndex( final String accountKey )
    {
        searchService.deleteIndex( accountKey, true );
    }

    private AccountType findAccountType( final String accountKey )
    {
        return userDao.findByKey( accountKey ) == null ? AccountType.GROUP : AccountType.USER;
    }

    private UserEntity getCurrentUser()
    {
        return userDao.findBuiltInEnterpriseAdminUser();
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
}
