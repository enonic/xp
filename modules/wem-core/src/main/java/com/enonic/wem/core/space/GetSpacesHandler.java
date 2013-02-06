package com.enonic.wem.core.space;

import java.util.List;

import javax.jcr.Session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;

import com.enonic.wem.api.command.space.GetSpaces;
import com.enonic.wem.api.space.Space;
import com.enonic.wem.api.space.SpaceName;
import com.enonic.wem.api.space.SpaceNames;
import com.enonic.wem.api.space.Spaces;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.space.dao.SpaceDao;

@Component
public final class GetSpacesHandler
    extends CommandHandler<GetSpaces>
{
    private SpaceDao spaceDao;

    public GetSpacesHandler()
    {
        super( GetSpaces.class );
    }

    @Override
    public void handle( final CommandContext context, final GetSpaces command )
        throws Exception
    {
        final Session session = context.getJcrSession();
        final Spaces spaces;
        if ( command.isGetAll() )
        {
            spaces = spaceDao.getAllSpaces( session );
        }
        else
        {
            final SpaceNames spaceNames = command.getSpaceNames();
            spaces = getSpaces( spaceNames, session );
        }

        command.setResult( spaces );
    }

    private Spaces getSpaces( final SpaceNames spaceNames, final Session session )
    {
        final List<Space> spaceList = Lists.newArrayList();
        for ( SpaceName spaceName : spaceNames )
        {
            final Space space = spaceDao.getSpace( spaceName, session );
            if ( space != null )
            {
                spaceList.add( space );
            }
        }
        return Spaces.from( spaceList );
    }

    @Autowired
    public void setSpaceDao( final SpaceDao spaceDao )
    {
        this.spaceDao = spaceDao;
    }
}
