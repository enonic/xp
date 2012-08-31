package com.enonic.wem.web.rest2.resource.userstore;

import java.util.Collection;
import java.util.List;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.base.Strings;

import com.enonic.wem.web.rest2.service.userstore.UserStoreUpdateService;

import com.enonic.cms.core.security.SecurityService;
import com.enonic.cms.core.security.group.GroupEntity;
import com.enonic.cms.core.security.group.GroupSpecification;
import com.enonic.cms.core.security.user.QualifiedUsername;
import com.enonic.cms.core.security.user.User;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.userstore.UserStoreConnectorManager;
import com.enonic.cms.core.security.userstore.UserStoreEntity;
import com.enonic.cms.core.security.userstore.UserStoreKey;
import com.enonic.cms.core.security.userstore.UserStoreService;
import com.enonic.cms.core.security.userstore.connector.config.UserStoreConnectorConfig;
import com.enonic.cms.store.dao.UserStoreDao;

@Path("userstore")
@Produces(MediaType.APPLICATION_JSON)
@Component
public class UserStoreResource
{
    @Autowired
    private UserStoreDao userStoreDao;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private UserStoreUpdateService userStoreUpdateService;

    @Autowired
    private UserStoreConnectorManager connectorManager;

    @Autowired
    private UserStoreService userStoreService;

    @GET
    public UserStoreResults getAll()
    {
        final List<UserStoreEntity> userStores = userStoreDao.findAll();
        return new UserStoreResults( userStores );
    }

    @GET
    @Path("{key}")
    public UserStoreDetailsResult getDetails( @PathParam("key") final String key )
    {
        if ( Strings.isNullOrEmpty( key ) )
        {
            return null;
        }

        final UserStoreEntity userStore = userStoreDao.findByKey( new UserStoreKey( key ) );
        if ( userStore == null )
        {
            return null;
        }
        else
        {
            final GroupSpecification groupSpec = new GroupSpecification();
            groupSpec.setUserStoreKey( userStore.getKey() );
            final List<GroupEntity> groups = userStoreService.getGroups( groupSpec );
            final List<UserEntity> users = userStoreService.getUsers( userStore.getKey() );
            UserStoreConnectorConfig connectorConfig = null;
            if ( !userStore.isLocal() )
            {
                connectorConfig = connectorManager.getUserStoreConnectorConfig( userStore.getKey() );
            }
            return new UserStoreDetailsResult( userStore, connectorConfig, groups, users );
        }
    }

    @POST
    @Path("{key}")
    public Response updateUserstore( @PathParam("key") String key, @FormParam("name") String name,
                                     @FormParam("defaultUserstore") Boolean defaultUserstore, @FormParam("config") String config,
                                     @FormParam("connector") String connector, @FormParam("administrators") List<String> administrators )
    {
        User user = securityService.getUser( new QualifiedUsername( "system", "admin" ) );
        if ( user == null )
        {
            //Anonymous users can't create or update userstores
            return Response.status( Response.Status.FORBIDDEN ).build();
        }

        UserStoreEntity duplicate = userStoreDao.findByName( name );
        if ( duplicate != null && key.equals( duplicate.getKey().toString() ) )
        {

            //The userstore with such name already exists
            return Response.status( Response.Status.NOT_ACCEPTABLE ).build();
        }

        userStoreUpdateService.updateUserStore( user, key, name, defaultUserstore, config, connector, administrators );
        return Response.ok().build();
    }

    @GET
    @Path("connectors")
    public ConnectorResults getConnectors()
    {
        Collection<UserStoreConnectorConfig> connectors = connectorManager.getUserStoreConnectorConfigs().values();

        return new ConnectorResults( connectors );
    }


    public UserStoreDao getUserStoreDao()
    {
        return userStoreDao;
    }

    public void setUserStoreDao( final UserStoreDao userStoreDao )
    {
        this.userStoreDao = userStoreDao;
    }

    public void setSecurityService( final SecurityService securityService )
    {
        this.securityService = securityService;
    }

    public void setUserStoreUpdateService( final UserStoreUpdateService userStoreUpdateService )
    {
        this.userStoreUpdateService = userStoreUpdateService;
    }

    public UserStoreConnectorManager getConnectorManager()
    {
        return connectorManager;
    }

    public void setConnectorManager( final UserStoreConnectorManager connectorManager )
    {
        this.connectorManager = connectorManager;
    }

    public void setUserStoreService( final UserStoreService userStoreService )
    {
        this.userStoreService = userStoreService;
    }
}
