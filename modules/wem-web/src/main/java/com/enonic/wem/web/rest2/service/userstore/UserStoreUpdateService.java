package com.enonic.wem.web.rest2.service.userstore;


import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.wem.core.search.account.AccountIndexData;
import com.enonic.wem.core.search.account.AccountKey;
import com.enonic.wem.core.search.account.AccountSearchService;
import com.enonic.wem.core.search.account.Group;
import com.enonic.wem.web.rest.account.AccountModelTranslator;

import com.enonic.cms.framework.xml.XMLDocumentFactory;

import com.enonic.cms.core.security.SecurityService;
import com.enonic.cms.core.security.group.GroupEntity;
import com.enonic.cms.core.security.group.GroupKey;
import com.enonic.cms.core.security.group.GroupSpecification;
import com.enonic.cms.core.security.group.GroupType;
import com.enonic.cms.core.security.group.UpdateGroupCommand;
import com.enonic.cms.core.security.user.User;
import com.enonic.cms.core.security.userstore.StoreNewUserStoreCommand;
import com.enonic.cms.core.security.userstore.UpdateUserStoreCommand;
import com.enonic.cms.core.security.userstore.UserStoreKey;
import com.enonic.cms.core.security.userstore.UserStoreService;
import com.enonic.cms.core.security.userstore.config.UserStoreConfig;
import com.enonic.cms.core.security.userstore.config.UserStoreConfigParser;


@Component
public class UserStoreUpdateService
{

    @Autowired
    private AccountSearchService searchService;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private AccountModelTranslator accountModelTranslator;

    @Autowired
    private UserStoreService userStoreService;

    public void updateUserStore( User user, String userStoreKey, String name, Boolean defaultUserStore, String configXML, String connector,
                                 List<String> administrators )
    {
        if ( StringUtils.isNotEmpty( userStoreKey ) )
        {
            updateUserstore( user, userStoreKey, name, defaultUserStore, configXML, connector );
        }
        else
        {
            userStoreKey = createUserstore( user, name, defaultUserStore, configXML, connector );
        }
        if ( administrators != null && administrators.size() > 0 )
        {
            updateUserstoreAdministrators( user, userStoreKey, administrators );
        }
    }

    private void updateUserstore( User user, String userStoreKey, String name, Boolean defaultUserStore, String configXML,
                                  String connector )
    {

        UserStoreConfig config = new UserStoreConfig();
        if ( configXML != null && configXML.trim().length() > 0 )
        {
            config = UserStoreConfigParser.parse( XMLDocumentFactory.create( configXML ).
                getAsJDOMDocument().getRootElement(), connector != null );
        }

        final UpdateUserStoreCommand command = new UpdateUserStoreCommand();
        command.setUpdater( user.getKey() );
        command.setKey( new UserStoreKey( userStoreKey ) );
        command.setName( name );
        if ( defaultUserStore )
        {
            command.setAsNewDefaultStore();
        }
        command.setConnectorName( connector );
        command.setConfig( config );

        userStoreService.updateUserStore( command );

        userStoreService.invalidateUserStoreCachedConfig( command.getKey() );
    }

    private String createUserstore( User user, String name, Boolean defaultUserStore, String connectorName, String configXML )
    {
        UserStoreConfig config = new UserStoreConfig();
        if ( configXML != null && configXML.trim().length() > 0 )
        {
            config = UserStoreConfigParser.parse( XMLDocumentFactory.create( configXML ).
                getAsJDOMDocument().getRootElement(), connectorName != null );
        }

        final StoreNewUserStoreCommand command = new StoreNewUserStoreCommand();
        if ( defaultUserStore )
        {
            command.setDefaultStore( defaultUserStore );
        }
        command.setStorer( user.getKey() );
        command.setName( name );
        command.setDefaultStore( defaultUserStore );
        command.setConnectorName( connectorName );
        command.setConfig( config );

        UserStoreKey key = userStoreService.storeNewUserStore( command );
        return key.toString();
    }

    private void updateUserstoreAdministrators( User user, String userStoreKey, List<String> administrators )
    {
        GroupSpecification spec = new GroupSpecification();
        spec.setType( GroupType.USERSTORE_ADMINS );
        spec.setDeletedState( GroupSpecification.DeletedState.NOT_DELETED );
        spec.setUserStoreKey( new UserStoreKey( userStoreKey ) );
        List<GroupEntity> adminGroups = userStoreService.getGroups( spec );
        if ( adminGroups.size() == 1 )
        {
            GroupEntity adminGroup = adminGroups.get( 0 );
            UpdateGroupCommand command = new UpdateGroupCommand( user.getKey(), adminGroup.getGroupKey() );
            command.setName( adminGroup.getName() );
            for ( String administratorKey : administrators )
            {
                final GroupKey groupKey = accountModelTranslator.getGroupModelTranslator().getMemberGroupKey( administratorKey );
                final GroupEntity groupMember = securityService.getGroup( groupKey );
                command.addMember( groupMember );
            }
            userStoreService.updateGroup( command );

            indexGroup( adminGroup.getGroupKey() );
        }

        spec.setType( GroupType.AUTHENTICATED_USERS );
        spec.setDeletedState( GroupSpecification.DeletedState.NOT_DELETED );
        adminGroups = userStoreService.getGroups( spec );
        if ( adminGroups.size() == 1 )
        {
            indexGroup( adminGroups.get( 0 ).getGroupKey() );
        }
    }


    private void indexGroup( final GroupKey groupKey )
    {
        final GroupEntity groupEntity = securityService.getGroup( groupKey );
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
