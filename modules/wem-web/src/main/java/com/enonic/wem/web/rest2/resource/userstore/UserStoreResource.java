package com.enonic.wem.web.rest2.resource.userstore;

import java.util.Collection;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.cms.core.security.userstore.UserStoreConnectorManager;
import com.enonic.cms.core.security.userstore.UserStoreEntity;
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
    private UserStoreConnectorManager connectorManager;

    @GET
    public UserStoreResults getAll()
    {
        final List<UserStoreEntity> userStores = userStoreDao.findAll();
        return new UserStoreResults( userStores );
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

    public UserStoreConnectorManager getConnectorManager()
    {
        return connectorManager;
    }

    public void setConnectorManager( final UserStoreConnectorManager connectorManager )
    {
        this.connectorManager = connectorManager;
    }
}
