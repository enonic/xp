package com.enonic.wem.web.rest2.resource.userstore;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.cms.core.security.userstore.UserStoreEntity;
import com.enonic.cms.store.dao.UserStoreDao;

@Path("userstore")
@Produces(MediaType.APPLICATION_JSON)
@Component
public class UserStoreResource
{
    private UserStoreDao userStoreDao;

    @GET
    public UserStoreResults getAll()
    {
        final List<UserStoreEntity> userStores = userStoreDao.findAll();
        return new UserStoreResults( userStores );
    }

    @Autowired
    public void setUserStoreDao( final UserStoreDao userStoreDao )
    {
        this.userStoreDao = userStoreDao;
    }
}
