package com.enonic.wem.web.rest.userstore;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.cms.framework.util.JDOMUtil;

import com.enonic.cms.core.security.group.GroupEntity;
import com.enonic.cms.core.security.group.GroupSpecification;
import com.enonic.cms.core.security.group.GroupType;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.userstore.UserStoreConnectorManager;
import com.enonic.cms.core.security.userstore.UserStoreEntity;
import com.enonic.cms.core.security.userstore.UserStoreService;
import com.enonic.cms.core.security.userstore.config.UserStoreUserFieldConfig;
import com.enonic.cms.core.security.userstore.connector.UserStoreConnector;
import com.enonic.cms.core.security.userstore.connector.config.UserStoreConnectorConfig;
import com.enonic.wem.web.rest.account.AccountModelTranslator;
import com.enonic.wem.web.rest.account.GroupModel;

@Component
public final class UserStoreConfigModelTranslator
{
    static final public String LOCAL_CONNECTOR_NAME = "local";

    @Autowired
    private AccountModelTranslator accountTranslator;

    public UserStoreConfigModel toModel( final UserStoreService userstoreService,
                                         final UserStoreConnectorManager connectorManager, final UserStoreEntity entity,
                                         boolean includeFields, boolean includeConfig )
    {
        final UserStoreConfigModel model = new UserStoreConfigModel();
        if ( entity != null )
        {
            model.setKey( entity.getKey().toString() );
            model.setName( entity.getName() );
            model.setDefaultStore( entity.getDefaultStore() > 0 );

            // TODO: Created & lastModified on UserStoreEntity does not exist. Mocking for now.
            // model.setCreated( entity.getCreated() );
            // model.setLastModified( entity.getModified() );
            model.setCreated( new Date() );
            model.setLastModified( new Date() );

            GroupSpecification groupSpec = new GroupSpecification();
            groupSpec.setUserStoreKey( entity.getKey() );
            List<GroupEntity> groups = userstoreService.getGroups( groupSpec );
            List<UserEntity> users = userstoreService.getUsers( entity.getKey() );

            model.setUserCount( users.size() );
            model.setGroupCount( groups.size() );

            UserStoreConnector connector = null;
            // no support for remote connectors now so try
            try
            {
                connector = connectorManager.getUserStoreConnector( entity.getKey() );
            }
            catch ( Exception e )
            {
            }

            String connectorName = entity.getConnectorName();
            model.setConnectorName( connectorName != null ? connectorName : LOCAL_CONNECTOR_NAME );
            model.setPlugin( "Plugin name" );
            model.setUserPolicy( getUserPolicy( connector ) );
            model.setGroupPolicy( getGroupPolicy( connector ) );
            for ( GroupEntity group : groups )
            {
                if ( group.getType() == GroupType.USERSTORE_ADMINS )
                {
                    GroupModel groupModel = accountTranslator.getGroupModelTranslator().toInfoModel( group );
                    model.setAdministrators( groupModel.getMembers() );
                }
            }

            if ( includeFields && entity.getConfig() != null )
            {
                for ( UserStoreUserFieldConfig fieldConfig : entity.getConfig().getUserFieldConfigs() )
                {
                    model.addUserField( new UserStoreConfigFieldModel( fieldConfig ) );
                }
            }

            if ( includeConfig && entity.getConfigAsXMLDocument() != null )
            {
                model.setConfigXML( JDOMUtil.serialize( entity.getConfigAsXMLDocument(), 2, true ) );
            }
        }
        return model;
    }


    public UserStoreConfigsModel toModel( final UserStoreService userstoreService,
                                          final UserStoreConnectorManager connectorManager,
                                          final List<UserStoreEntity> list, boolean includeFields,
                                          boolean includeConfig )
    {
        final UserStoreConfigsModel model = new UserStoreConfigsModel();
        model.setTotal( list.size() );

        for ( final UserStoreEntity entity : list )
        {
            model.addUserStoreConfig(
                    toModel( userstoreService, connectorManager, entity, includeFields, includeConfig ) );
        }

        return model;
    }

    public UserStoreConnectorsModel toModel( Map<String, UserStoreConnectorConfig> map )
    {
        final UserStoreConnectorsModel model = new UserStoreConnectorsModel();
        model.setTotal( map.size() + 1 );
        model.addUserStoreConnector( createLocalConnector() );
        for ( final UserStoreConnectorConfig entity : map.values() )
        {
            model.addUserStoreConnector( toModel( entity ) );
        }

        return model;
    }

    private UserStoreConnectorModel createLocalConnector()
    {
        final UserStoreConnectorModel model = new UserStoreConnectorModel();
        model.setName( LOCAL_CONNECTOR_NAME );
        model.setPluginType( LOCAL_CONNECTOR_NAME );
        return model;
    }

    public UserStoreConnectorModel toModel( final UserStoreConnectorConfig entity )
    {
        final UserStoreConnectorModel model = new UserStoreConnectorModel();
        if ( entity != null )
        {
            model.setName( entity.getName() );
            model.setPluginType( entity.getPluginType() );
            model.setCanCreateUser( entity.canCreateUser() );
            model.setCanUpdateUser( entity.canUpdateUser() );
            model.setCanUpdateUserPassword( entity.canUpdateUserPassword() );
            model.setCanDeleteUser( entity.canDeleteUser() );
            model.setCanCreateGroup( entity.canCreateGroup() );
            model.setCanUpdateGroup( entity.canUpdateGroup() );
            model.setCanReadGroup( entity.canReadGroup() );
            model.setCanDeleteGroup( entity.canDeleteGroup() );
            model.setGroupsLocal( entity.groupsStoredLocal() );
        }
        return model;
    }


    private String getGroupPolicy( UserStoreConnector connector )
    {
        if ( connector == null )
        {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        if ( connector.canReadGroup() )
        {
            sb.append( "read, " );
        }
        if ( connector.canCreateGroup() )
        {
            sb.append( "create, " );
        }
        if ( connector.canUpdateGroup() )
        {
            sb.append( "update, " );
        }
        if ( connector.canDeleteGroup() )
        {
            sb.append( "delete, " );
        }
        return sb.length() > 0 ? sb.substring( 0, sb.length() - 2 ) : sb.toString();
    }

    private String getUserPolicy( UserStoreConnector connector )
    {
        if ( connector == null )
        {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        if ( connector.canCreateUser() )
        {
            sb.append( "create, " );
        }
        if ( connector.canUpdateUser() )
        {
            sb.append( "update, " );
        }
        if ( connector.canUpdateUserPassword() )
        {
            sb.append( "update password, " );
        }
        if ( connector.canDeleteUser() )
        {
            sb.append( "delete, " );
        }
        return sb.length() > 0 ? sb.substring( 0, sb.length() - 2 ) : sb.toString();
    }
}
