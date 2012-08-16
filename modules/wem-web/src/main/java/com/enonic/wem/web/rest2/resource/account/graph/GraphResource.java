package com.enonic.wem.web.rest2.resource.account.graph;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.wem.web.rest2.service.account.group.GroupGraphService;
import com.enonic.wem.web.rest2.service.account.user.UserGraphService;

import com.enonic.cms.core.security.group.GroupEntity;
import com.enonic.cms.core.security.group.GroupKey;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.store.dao.GroupDao;
import com.enonic.cms.store.dao.UserDao;

@Path("account/graph")
@Produces(MediaType.APPLICATION_JSON)
@Component
public final class GraphResource
{

    private UserDao userDao;

    private GroupDao groupDao;

    private UserGraphService userGraphService;

    private GroupGraphService groupGraphService;

    @GET
    @Path("{key}")
    public GraphResult getInfo( @PathParam("key") final String key )
    {
        UserEntity userEntity = userDao.findByKey( key );
        GroupEntity groupEntity = groupDao.findByKey( new GroupKey( key ) );
        if ( userEntity != null )
        {
            return userGraphService.generateGraph( userEntity );
        }
        else if ( groupEntity != null )
        {
            return groupGraphService.generateGraph( groupEntity );
        }
        else
        {
            return null;
        }

    }

    @Autowired
    public void setUserGraphService( final UserGraphService userGraphService )
    {
        this.userGraphService = userGraphService;
    }

    @Autowired
    public void setGroupGraphService( final GroupGraphService groupGraphService )
    {
        this.groupGraphService = groupGraphService;
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
}
