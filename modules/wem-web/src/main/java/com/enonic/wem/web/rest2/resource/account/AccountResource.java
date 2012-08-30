package com.enonic.wem.web.rest2.resource.account;

import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.wem.core.search.Facets;
import com.enonic.wem.core.search.SearchSortOrder;
import com.enonic.wem.core.search.account.AccountIndexField;
import com.enonic.wem.core.search.account.AccountKey;
import com.enonic.wem.core.search.account.AccountSearchHit;
import com.enonic.wem.core.search.account.AccountSearchQuery;
import com.enonic.wem.core.search.account.AccountSearchResults;
import com.enonic.wem.core.search.account.AccountSearchService;
import com.enonic.wem.core.search.account.AccountType;
import com.enonic.wem.web.rest2.service.account.AccountCsvExportService;

import com.enonic.cms.core.security.group.DeleteGroupCommand;
import com.enonic.cms.core.security.group.GroupEntity;
import com.enonic.cms.core.security.group.GroupKey;
import com.enonic.cms.core.security.group.GroupSpecification;
import com.enonic.cms.core.security.user.DeleteUserCommand;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.user.UserKey;
import com.enonic.cms.core.security.user.UserSpecification;
import com.enonic.cms.core.security.userstore.UserStoreService;
import com.enonic.cms.store.dao.GroupDao;
import com.enonic.cms.store.dao.UserDao;

@Path("account")
@Produces(MediaType.APPLICATION_JSON)
@Component
public final class AccountResource
{

    private UserDao userDao;

    private GroupDao groupDao;

    private UserStoreService userStoreService;

    private AccountSearchService accountSearchService;

    private AccountCsvExportService accountCsvExportService;

    @GET
    public AccountsResult search( @QueryParam("start") @DefaultValue("0") int start, @QueryParam("limit") @DefaultValue("10") int limit,
                                  @QueryParam("sort") @DefaultValue("") String sort, @QueryParam("dir") @DefaultValue("ASC") String sortDir,
                                  @QueryParam("query") @DefaultValue("") String query,
                                  @QueryParam("types") @DefaultValue("user,group,role") String types,
                                  @QueryParam("userstores") @DefaultValue("") String userStores,
                                  @QueryParam("organizations") @DefaultValue("") String organizations )
    {
        final AccountSearchQuery searchQuery = buildSearchQuery( query, types, userStores, organizations, start, limit, sort, sortDir );
        final AccountSearchResults searchResults = accountSearchService.search( searchQuery );
        List list = populateSearchResults( searchResults );
        Facets facets = searchResults != null ? searchResults.getFacets() : new Facets();
        int total = searchResults != null ? searchResults.getTotal() : 0;
        return new AccountsResult( list, facets, total );
    }

    @GET
    @Path("export-query")
    public Response exportQuery( @QueryParam("sort") @DefaultValue("") String sort, @QueryParam("dir") @DefaultValue("ASC") String sortDir,
                                 @QueryParam("query") @DefaultValue("") String query,
                                 @QueryParam("type") @DefaultValue("users,groups,roles") String types,
                                 @QueryParam("userstores") @DefaultValue("") String userStores,
                                 @QueryParam("organizations") @DefaultValue("") String organizations,
                                 @QueryParam("encoding") @DefaultValue("ISO-8859-1") String characterEncoding,
                                 @QueryParam("separator") @DefaultValue("\t") String separator )
        throws UnsupportedEncodingException
    {
        final int accountsExportLimit = 5000;
        final AccountSearchQuery searchQuery =
            buildSearchQuery( query, types, userStores, organizations, 0, accountsExportLimit, sort, sortDir );
        final AccountSearchResults searchResults = accountSearchService.search( searchQuery );

        return createCsvExportResponse( searchResults, separator, characterEncoding );
    }

    @GET
    @Path("export-keys")
    public Response exportKeys( @QueryParam("key") List<String> keys,
                                @QueryParam("encoding") @DefaultValue("ISO-8859-1") String characterEncoding,
                                @QueryParam("separator") @DefaultValue("\t") String separator )
        throws UnsupportedEncodingException
    {
        AccountSearchResults searchResults = getAccountListForKeys( keys );

        return createCsvExportResponse( searchResults, separator, characterEncoding );
    }

    @POST
    @Path("delete")
    public AccountGenericResult deleteAccount( @QueryParam("key") @DefaultValue("") final List<String> keys )
    {
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
                        break;

                    case GROUP:
                        final GroupSpecification groupSpec = new GroupSpecification();
                        groupSpec.setKey( new GroupKey( accountKey ) );
                        final DeleteGroupCommand deleteGroupCommand = new DeleteGroupCommand( deleter, groupSpec );
                        userStoreService.deleteGroup( deleteGroupCommand );
                        break;
                }
                removeAccountIndex( accountKey );
            }
            catch ( Exception e )
            {
                final String errorMsg = MessageFormat.format( "Unable to delete account with key {0}", accountKey );
                return new AccountGenericResult( false, errorMsg );
            }
        }

        return new AccountGenericResult( true );
    }

    private void removeAccountIndex( final String accountKey )
    {
        accountSearchService.deleteIndex( accountKey, true );
    }

    private Response createCsvExportResponse( AccountSearchResults results, String separator, String encoding )
        throws UnsupportedEncodingException
    {
        String content = accountCsvExportService.generateCsv( results, separator );
        String attachmentHeader = "attachment; filename=" + accountCsvExportService.getExportFileName( new Date() );
        return Response.ok( content.getBytes( encoding ) ).type( "text/csv" ).
            header( "Content-Encoding", encoding ).
            header( "Content-Disposition", attachmentHeader ).build();
    }

    AccountSearchResults getAccountListForKeys( final List<String> keys )
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

    private AccountType findAccountType( final String accountKey )
    {
        return userDao.findByKey( accountKey ) == null ? AccountType.GROUP : AccountType.USER;
    }

    AccountSearchQuery buildSearchQuery( String query, String types, String userstores, String organizations, int start, int limit,
                                         String sort, String dir )
    {

        String[] userstoreList = ( userstores == null ) ? new String[0] : userstores.split( "," );
        final String[] organizationList = ( organizations == null ) ? new String[0] : organizations.split( "," );
        final boolean isSelectUsers = types.contains( "user" );
        final boolean isSelectGroups = types.contains( "group" );
        final boolean isSelectRoles = types.contains( "role" );
        AccountIndexField sortField = AccountIndexField.parse( sort );
        if ( sortField == null )
        {
            sortField = AccountIndexField.DISPLAY_NAME_FIELD;
        }
        SearchSortOrder sortOrder = SearchSortOrder.valueOf( dir );
        if ( sortOrder == null )
        {
            sortOrder = SearchSortOrder.ASC;
        }

        return new AccountSearchQuery().setIncludeResults( true ).setIncludeFacets( true ).setQuery( query ).setUsers(
            isSelectUsers ).setGroups( isSelectGroups ).setRoles( isSelectRoles ).setUserStores( userstoreList ).setOrganizations(
            organizationList ).setCount( limit ).setFrom( start ).setSortField( sortField ).setSortOrder( sortOrder );

    }

    List populateSearchResults( AccountSearchResults results )
    {
        final List list = new ArrayList();
        if ( results != null )
        {
            for ( AccountSearchHit hit : results )
            {
                switch ( hit.getAccountType() )
                {
                    case ROLE:
                    case GROUP:
                        GroupEntity groupEntity = this.groupDao.findByKey( new GroupKey( hit.getKey().toString() ) );
                        if ( groupEntity != null )
                        {
                            list.add( groupEntity );
                        }
                        break;

                    case USER:
                        UserEntity userEntity = this.userDao.findByKey( new UserKey( hit.getKey().toString() ) );
                        if ( userEntity != null )
                        {
                            list.add( userEntity );
                        }
                        break;
                }
            }
        }
        return list;
    }

    private UserEntity getCurrentUser()
    {
        return userDao.findBuiltInEnterpriseAdminUser();
    }

    @Autowired
    public void setUserDao( final UserDao userDao )
    {
        this.userDao = userDao;
    }

    @Autowired
    public void setGroupDao( final GroupDao groupDao )
    {
        this.groupDao = groupDao;
    }

    @Autowired
    public void setAccountSearchService( final AccountSearchService accountSearchService )
    {
        this.accountSearchService = accountSearchService;
    }

    @Autowired
    public void setAccountCsvExportService( AccountCsvExportService accountCsvExportService )
    {
        this.accountCsvExportService = accountCsvExportService;
    }

    @Autowired
    public AccountCsvExportService getAccountCsvExportService()
    {
        return this.accountCsvExportService;
    }

    @Autowired
    public void setUserStoreService( final UserStoreService userStoreService )
    {
        this.userStoreService = userStoreService;
    }
}
