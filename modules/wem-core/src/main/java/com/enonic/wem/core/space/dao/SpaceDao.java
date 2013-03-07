package com.enonic.wem.core.space.dao;

import javax.jcr.Session;

import com.enonic.wem.api.space.Space;
import com.enonic.wem.api.space.SpaceName;
import com.enonic.wem.api.space.Spaces;

public interface SpaceDao
{
    void createSpace( Space space, Session session );

    Space getSpace( SpaceName spaceName, Session session );

    Spaces getAllSpaces( Session session );

    void updateSpace( Space space, Session session );

    void deleteSpace( SpaceName spaceName, Session session );

    boolean renameSpace( SpaceName spaceName, String newName, Session session );
}
