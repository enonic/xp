package com.enonic.wem.web.rest.userstore;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.enonic.esl.containers.ExtendedMap;

import com.enonic.cms.framework.xml.XMLDocumentFactory;

import com.enonic.wem.core.search.account.AccountIndexData;
import com.enonic.wem.core.search.account.AccountKey;
import com.enonic.wem.core.search.account.AccountSearchService;
import com.enonic.wem.core.search.account.Group;
import com.enonic.cms.core.security.SecurityService;
import com.enonic.cms.core.security.group.GroupEntity;
import com.enonic.cms.core.security.group.GroupKey;
import com.enonic.cms.core.security.group.GroupSpecification;
import com.enonic.cms.core.security.group.GroupType;
import com.enonic.cms.core.security.group.UpdateGroupCommand;
import com.enonic.cms.core.security.user.QualifiedUsername;
import com.enonic.cms.core.security.user.User;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.userstore.StoreNewUserStoreCommand;
import com.enonic.cms.core.security.userstore.UpdateUserStoreCommand;
import com.enonic.cms.core.security.userstore.UserStoreConnectorManager;
import com.enonic.cms.core.security.userstore.UserStoreEntity;
import com.enonic.cms.core.security.userstore.UserStoreKey;
import com.enonic.cms.core.security.userstore.UserStoreService;
import com.enonic.cms.core.security.userstore.config.UserStoreConfig;
import com.enonic.cms.core.security.userstore.config.UserStoreConfigParser;
import com.enonic.cms.core.security.userstore.connector.config.UserStoreConnectorConfig;
import com.enonic.cms.store.dao.UserDao;
import com.enonic.cms.store.dao.UserStoreDao;
import com.enonic.wem.web.rest.account.AccountModel;
import com.enonic.wem.web.rest.account.AccountModelTranslator;
import com.enonic.wem.web.rest.common.RestResponse;


@Controller
@RequestMapping(value = "/userstore", produces = MediaType.APPLICATION_JSON_VALUE)
public final class UserStoreConfigController
{
    @Autowired
    private UserStoreService userStoreService;

    @Autowired
    private UserStoreDao userStoreDao;

    @Autowired
    private UserStoreConfigModelTranslator userStoreConfigModelTranslator;

    @Autowired
    private AccountModelTranslator accountModelTranslator;

    @Autowired
    private SecurityService securityService;

    @Autowired
    UserStoreConnectorManager connectorManager;

    @Autowired
    private UserDao userDao;

    @Autowired
    private AccountSearchService searchService;


    @RequestMapping(value = "list", method = RequestMethod.GET)
    @ResponseBody
    public UserStoreConfigsModel getAll(
            @RequestParam(value = "includeFields", defaultValue = "false") final boolean includeFields,
            @RequestParam(value = "includeConfig", defaultValue = "false") final boolean includeConfig )
    {
        final List<UserStoreEntity> list = userStoreDao.findAll();
        return userStoreConfigModelTranslator.toModel( userStoreService, connectorManager, list, includeFields,
                                                       includeConfig );
    }

    @RequestMapping(value = "detail", method = RequestMethod.GET)
    @ResponseBody
    public UserStoreConfigModel getDetail( @RequestParam(value = "name", defaultValue = "") final String name )
    {
        final UserStoreEntity store = userStoreDao.findByName( name );
        return userStoreConfigModelTranslator.toModel( userStoreService, connectorManager, store, true, false );
    }

    @RequestMapping(value = "config", method = RequestMethod.GET)
    @ResponseBody
    public UserStoreConfigModel getConfig( @RequestParam(value = "name", defaultValue = "") final String name )
    {
        final UserStoreEntity store = userStoreDao.findByName( name );
        return userStoreConfigModelTranslator.toModel( userStoreService, connectorManager, store, false, true );
    }

    @RequestMapping(value = "config", method = RequestMethod.POST)
    @ResponseBody
    public RestResponse postConfig( @RequestBody UserStoreConfigModel userstoreConfig )
    {
        RestResponse out = new RestResponse();

        User user = securityService.getUser( new QualifiedUsername( "system", "admin" ) );
        if ( user == null )
        {
            out.setMsg( "Anonymous users can't create or update userstores." );
            out.setSuccess( false );
            return out;
        }

        UserStoreEntity duplicate = userStoreDao.findByName( userstoreConfig.getName() );
        if ( duplicate != null && ( StringUtils.isEmpty( userstoreConfig.getKey() ) ||
                StringUtils.isNotEmpty( userstoreConfig.getKey() ) &&
                        !userstoreConfig.getKey().equals( duplicate.getKey().toString() ) ) )
        {

            out.setMsg( "The userstore with such name already exists." );
            out.setSuccess( false );
            return out;
        }

        ExtendedMap formItems = new ExtendedMap( 5 );
        formItems.putString( "key", userstoreConfig.getKey() );
        formItems.putString( "name", userstoreConfig.getName() );
        formItems.putBoolean( "defaultUserstore", BooleanUtils.toBoolean( userstoreConfig.getDefaultStore() ) );
        formItems.putString( "config", userstoreConfig.getConfigXML() );
        if ( userstoreConfig.getRemote() )
        {
            formItems.putString( "connectorName", userstoreConfig.getConnectorName() );
        }

        if ( StringUtils.isNotEmpty( userstoreConfig.getKey() ) )
        {
            updateUserstore( user, formItems );
        }
        else
        {
            createUserstore( user, formItems );
            userstoreConfig.setKey( formItems.getString( "key" ) );
        }
        updateUserstoreAdministrators( userstoreConfig );
        out.setSuccess( true );
        return out;
    }


    @RequestMapping(value = "connectors", method = RequestMethod.GET)
    @ResponseBody
    public UserStoreConnectorsModel getConnectors()
    {
        Map<String, UserStoreConnectorConfig> map = connectorManager.getUserStoreConnectorConfigs();
        return userStoreConfigModelTranslator.toModel( map );
    }

    private void updateUserstoreAdministrators( UserStoreConfigModel userStore )
    {
        GroupSpecification spec = new GroupSpecification();
        spec.setType( GroupType.USERSTORE_ADMINS );
        spec.setDeletedState( GroupSpecification.DeletedState.NOT_DELETED );
        spec.setUserStoreKey( new UserStoreKey( userStore.getKey() ) );
        List<GroupEntity> adminGroups = userStoreService.getGroups( spec );
        if ( adminGroups.size() == 1 )
        {
            GroupEntity adminGroup = adminGroups.get( 0 );
            UpdateGroupCommand command = new UpdateGroupCommand( getCurrentUser().getKey(), adminGroup.getGroupKey() );
            command.setName( adminGroup.getName() );
            for ( AccountModel administrators : userStore.getAdministrators() )
            {
                final GroupKey groupKey =
                        accountModelTranslator.getGroupModelTranslator().getMemberGroupKey( administrators.getKey() );
                final GroupEntity groupMember = securityService.getGroup( groupKey );
                command.addMember( groupMember );
            }
            userStoreService.updateGroup( command );

            indexGroup( adminGroup.getGroupKey() );
        }

        spec.setType( GroupType.AUTHENTICATED_USERS );
        spec.setDeletedState( GroupSpecification.DeletedState.NOT_DELETED );
        spec.setUserStoreKey( new UserStoreKey( userStore.getKey() ) );
        adminGroups = userStoreService.getGroups( spec );
        if ( adminGroups.size() == 1 )
        {
            indexGroup( adminGroups.get( 0 ).getGroupKey() );
        }
    }

    private void updateUserstore( User user, ExtendedMap formItems )
    {
        final UserStoreKey userStoreKey = new UserStoreKey( formItems.getString( "key" ) );
        final boolean newDefaultUserStore = formItems.getBoolean( "defaultUserstore", false );
        final String connectorName = formItems.getString( "connectorName", null );
        final String configXmlString = formItems.getString( "config", null );

        UserStoreConfig config = new UserStoreConfig();
        if ( configXmlString != null && configXmlString.trim().length() > 0 )
        {
            config = UserStoreConfigParser.parse( XMLDocumentFactory.create( configXmlString ).
                    getAsJDOMDocument().getRootElement(), connectorName != null );
        }

        final UpdateUserStoreCommand command = new UpdateUserStoreCommand();
        command.setUpdater( user.getKey() );
        command.setKey( userStoreKey );
        command.setName( formItems.getString( "name", null ) );
        if ( newDefaultUserStore )
        {
            command.setAsNewDefaultStore();
        }
        command.setConnectorName( connectorName );
        command.setConfig( config );

        userStoreService.updateUserStore( command );

        userStoreService.invalidateUserStoreCachedConfig( command.getKey() );
    }

    private void createUserstore( User user, ExtendedMap formItems )
    {
        final String name = formItems.getString( "name", null );
        final boolean defaultUserStore = formItems.getBoolean( "defaultUserstore", false );
        final String connectorName = formItems.getString( "connectorName", null );
        final String configXmlString = formItems.getString( "config", null );

        UserStoreConfig config = new UserStoreConfig();
        if ( configXmlString != null && configXmlString.trim().length() > 0 )
        {
            config = UserStoreConfigParser.parse( XMLDocumentFactory.create( configXmlString ).
                    getAsJDOMDocument().getRootElement(), connectorName != null );
        }

        final StoreNewUserStoreCommand command = new StoreNewUserStoreCommand();
        if ( defaultUserStore )
        {
            command.setDefaultStore( defaultUserStore );
        }
        command.setStorer( user.getKey() );
        command.setName( name );
        command.setDefaultStore( formItems.getBoolean( "defaultUserstore", false ) );
        command.setConnectorName( connectorName );
        command.setConfig( config );

        UserStoreKey key = userStoreService.storeNewUserStore( command );
        formItems.put( "key", key );
    }


    private UserEntity getCurrentUser()
    {
        return userDao.findBuiltInEnterpriseAdminUser();
    }


    private void indexGroup( final GroupKey groupKey )
    {
        final GroupEntity groupEntity = this.securityService.getGroup( groupKey );
        if ( groupEntity == null )
        {
            searchService.deleteIndex( String.valueOf( groupKey ) );
            return;
        }

        final Group group = new Group();
        group.setKey( new AccountKey( groupEntity.getGroupKey().toString() ) );
        group.setName( groupEntity.getName() );

        // TODO: DisplayName does not exist on GroupEntity. Using description for now.
        // group.setDisplayName( groupEntity.getDisplayName() );
        group.setDisplayName( groupEntity.getDescription() );

        group.setGroupType( groupEntity.getType() );
        if ( groupEntity.getUserStore() != null )
        {
            group.setUserStoreName( groupEntity.getUserStore().getName() );
        }

        // TODO: LastModified does not exist on GroupEntity. Using "null" for now.
        // final DateTime lastModified = ( groupEntity.getLastModified() == null ) ? null : new DateTime( groupEntity.getLastModified() );
        // group.setLastModified( lastModified );
        group.setLastModified( null );

        final AccountIndexData accountIndexData = new AccountIndexData( group );

        searchService.index( accountIndexData );
    }
}
