package com.enonic.cms.web.rest.account;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.enonic.wem.core.search.SearchSortOrder;
import com.enonic.wem.core.search.account.AccountIndexData;
import com.enonic.wem.core.search.account.AccountIndexField;
import com.enonic.wem.core.search.account.AccountKey;
import com.enonic.wem.core.search.account.AccountSearchHit;
import com.enonic.wem.core.search.account.AccountSearchQuery;
import com.enonic.wem.core.search.account.AccountSearchResults;
import com.enonic.wem.core.search.account.AccountSearchService;
import com.enonic.wem.core.search.account.Group;
import com.enonic.cms.core.security.SecurityService;
import com.enonic.cms.core.security.group.GroupEntity;
import com.enonic.cms.core.security.group.GroupKey;
import com.enonic.cms.core.security.group.StoreNewGroupCommand;
import com.enonic.cms.core.security.group.UpdateGroupCommand;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.user.UserKey;
import com.enonic.cms.core.security.userstore.UserStoreEntity;
import com.enonic.cms.core.security.userstore.UserStoreKey;
import com.enonic.cms.core.security.userstore.UserStoreService;
import com.enonic.cms.store.dao.GroupDao;
import com.enonic.cms.store.dao.UserDao;
import com.enonic.cms.store.dao.UserStoreDao;

@Controller
@RequestMapping(value = "/group", produces = MediaType.APPLICATION_JSON_VALUE)
public final class GroupController
{
    private static final Logger LOG = LoggerFactory.getLogger( GroupController.class );

    @Autowired
    private GroupDao groupDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserStoreDao userStoreDao;

    @Autowired
    private UserStoreService userStoreService;

    @Autowired
    private AccountSearchService searchService;

    @Autowired
    protected SecurityService securityService;

    @Autowired
    private AccountModelTranslator accountModelTranslator;

    @RequestMapping(value = "delete", method = RequestMethod.POST)
    @ResponseBody
    public void delete( @RequestParam(value = "key", defaultValue = "") final String key )
    {
        GroupEntity group = groupDao.find( key );
        if ( group != null )
        {
            groupDao.delete( group );
        }
    }


    @RequestMapping(value = "list", method = RequestMethod.GET)
    @ResponseBody
    public AccountsModel getGroups( @RequestParam(value = "query", defaultValue = "") String query,
                                    @RequestParam(value = "key", defaultValue = "") String key,
                                    @RequestParam(value = "limit", defaultValue = "50") final int limit )
    {
        if ( StringUtils.isNotEmpty( key ) )
        {
            final String[] groupKeys = StringUtils.split( key, "," );
            return getGroups( groupKeys );
        }
        final AccountSearchQuery searchQueryCountFacets =
                new AccountSearchQuery().setCount( limit ).setIncludeResults( true ).setQuery( query ).setGroups(
                        true ).setUsers( false ).setIncludeFacets( false ).setSortField(
                        AccountIndexField.DISPLAY_NAME_FIELD ).setSortOrder( SearchSortOrder.ASC );

        final AccountSearchResults searchResults = searchService.search( searchQueryCountFacets );

        final List<GroupEntity> groups = new ArrayList<GroupEntity>();

        for ( AccountSearchHit searchHit : searchResults )
        {
            GroupEntity groupEntity = this.groupDao.findByKey( new GroupKey( searchHit.getKey().toString() ) );
            groups.add( groupEntity );
        }

        return accountModelTranslator.toModel( groups );
    }

    @RequestMapping(value = "detail", method = RequestMethod.GET)
    @ResponseBody
    public AccountModel getGroupDetails( HttpServletResponse response, @RequestParam("key") final String key )
            throws IOException
    {
        final GroupEntity entity = findEntity( key );
        if ( entity != null )
        {
            return accountModelTranslator.toModel( entity );
        }
        else
        {
            response.sendError( HttpServletResponse.SC_NOT_FOUND );
            return new GroupModel();
        }
    }

    private AccountsModel getGroups( final String... groupKeys )
    {
        final List<GroupEntity> groups = new ArrayList<GroupEntity>();
        for ( String groupKey : groupKeys )
        {
            GroupEntity groupEntity = this.groupDao.findByKey( new GroupKey( groupKey.trim() ) );
            if ( groupEntity != null )
            {
                groups.add( groupEntity );
            }
        }

        return accountModelTranslator.toModel( groups );
    }

    @RequestMapping(value = "update", method = RequestMethod.POST,
                    consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public GroupRestResponse updateGroup( @RequestBody GroupModel group )
    {
        final String validationMsg = validateGroupData( group );
        final GroupRestResponse res = new GroupRestResponse();
        if ( validationMsg == null )
        {
            if ( group.getKey() == null )
            {
                StoreNewGroupCommand command =
                        accountModelTranslator.getGroupModelTranslator().toNewGroupCommand( group );
                command.setExecutor( getCurrentUser() );
                GroupKey groupKey = userStoreService.storeNewGroup( command );
                res.setGroupkey( groupKey.toString() );
                indexGroup( groupKey.toString() );
            }
            else
            {
                UpdateGroupCommand command =
                        accountModelTranslator.getGroupModelTranslator().toUpdateGroupCommand( group,
                                                                                               getCurrentUser().getKey() );
                userStoreService.updateGroup( command );
                res.setGroupkey( group.getKey() );
                indexGroup( group.getKey() );
            }
            res.setSuccess( true );
        }
        else
        {
            res.setSuccess( false );
            res.setError( validationMsg );
        }
        return res;
    }

    private String validateGroupData( GroupModel groupData )
    {
        if ( StringUtils.isBlank( groupData.getName() ) )
        {
            return "Group name can't be blank.";
        }
        if ( !groupData.isBuiltIn() )
        {
            String membersMsg = validateMembersInUserStore( groupData );
            if ( membersMsg != null )
            {
                return membersMsg;
            }
        }
        return null;
    }

    private String validateMembersInUserStore( GroupModel groupData )
    {
        UserStoreEntity userStore =
                ( groupData.getUserStore() == null ) ? null : userStoreDao.findByName( groupData.getUserStore() );
        if ( userStore == null )
        {
            userStore = userStoreService.getDefaultUserStore();
        }
        final UserStoreKey userStoreKey = userStore.getKey();
        final boolean isUserStoreRemote = userStore.isRemote();

        List<AccountModel> members = groupData.getMembers();
        for ( AccountModel member : members )
        {
            final String memberKey = member.getKey();
            final UserStoreKey memberUserStoreKey = getMemberUserStoreKey( memberKey );
            if ( isUserStoreRemote && !memberUserStoreKey.equals( userStoreKey ) )
            {
                String errorMsg = "'" + getMemberName( memberKey ) +
                        "' cannot be member of group '" + groupData.getName() +
                        "'. Group and member must be located in same user store if it is remote.";
                LOG.warn( errorMsg );
                return errorMsg;
            }
        }
        return null;
    }

    private UserStoreKey getMemberUserStoreKey( final String memberKey )
    {
        if ( memberKey == null )
        {
            return null;
        }
        final UserKey userKey = new UserKey( memberKey );
        final UserEntity user = securityService.getUser( userKey );
        if ( user != null )
        {
            return user.getUserStoreKey();
        }
        else
        {
            final GroupEntity group = securityService.getGroup( new GroupKey( memberKey ) );
            return group == null ? null : group.getUserStoreKey();
        }
    }

    private String getMemberName( final String memberKey )
    {
        if ( memberKey == null )
        {
            return "";
        }
        final UserKey userKey = new UserKey( memberKey );
        final UserEntity user = securityService.getUser( userKey );
        if ( user != null )
        {
            return user.getName();
        }
        else
        {
            final GroupEntity group = securityService.getGroup( new GroupKey( memberKey ) );
            if ( group != null )
            {
                return group.getName();
            }
        }
        return memberKey;
    }

    private void indexGroup( final String groupKey )
    {
        final GroupEntity groupEntity = this.groupDao.find( groupKey );
        if ( groupEntity == null )
        {
            searchService.deleteIndex( groupKey );
            return;
        }

        final Group group = new Group();
        group.setKey( new AccountKey( groupEntity.getGroupKey().toString() ) );
        group.setName( groupEntity.getName() );

        // TODO: Group does not have DisplayName field. Using description.
        // group.setDisplayName( groupEntity.getDisplayName() );
        group.setDisplayName( groupEntity.getDescription() );

        group.setGroupType( groupEntity.getType() );
        if ( groupEntity.getUserStore() != null )
        {
            group.setUserStoreName( groupEntity.getUserStore().getName() );
        }

        // TODO: Group does not have LastModified field. Using "null" instead.
        // final DateTime lastModified = ( groupEntity.getLastModified() == null ) ? null : new DateTime( groupEntity.getLastModified() );
        // group.setLastModified( lastModified );
        group.setLastModified( null );

        final AccountIndexData accountIndexData = new AccountIndexData( group );

        searchService.index( accountIndexData );
    }

    private GroupEntity findEntity( final String key )
    {
        if ( key == null )
        {
            return null;
        }

        final GroupEntity entity = this.groupDao.find( key );
        if ( ( entity == null ) || entity.isDeleted() )
        {
            return null;
        }

        return entity;
    }

    private UserEntity getCurrentUser()
    {
        return userDao.findBuiltInEnterpriseAdminUser();
    }

}
