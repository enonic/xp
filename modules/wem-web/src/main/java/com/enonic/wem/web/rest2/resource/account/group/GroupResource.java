package com.enonic.wem.web.rest2.resource.account.group;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;

import com.enonic.cms.core.security.group.GroupEntity;
import com.enonic.cms.core.security.group.GroupType;
import com.enonic.cms.store.dao.GroupDao;

@Path("account/group")
@Produces(MediaType.APPLICATION_JSON)
@Component
public final class GroupResource
{
    private GroupDao groupDao;

    @GET
    @Path("{key}")
    public GroupResult getInfo( @PathParam("key") final String key )
    {
        final GroupEntity groupEntity = this.groupDao.find( key );
        if ( groupEntity == null )
        {
            return null;
        }
        else
        {
            final Collection<Object> members = getGroupMembers( groupEntity );
            return new GroupResult( groupEntity, members );
        }
    }

    private Collection<Object> getGroupMembers( final GroupEntity groupEntity )
    {
        final List<Object> members = Lists.newArrayList();
        final Set<GroupEntity> memberGroups = groupEntity.getMembers( false );
        for ( GroupEntity memberGroup : memberGroups )
        {
            if ( memberGroup.getType() == GroupType.USER )
            {
                members.add( memberGroup.getUser() );
            }
            else
            {
                members.add( memberGroup );
            }
        }
        return members;
    }

    @Autowired
    public void setGroupDao( final GroupDao groupDao )
    {
        this.groupDao = groupDao;
    }

}
