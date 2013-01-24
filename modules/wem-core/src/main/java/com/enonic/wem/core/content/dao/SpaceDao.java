package com.enonic.wem.core.content.dao;

import javax.jcr.Session;

import com.enonic.wem.api.space.Space;
import com.enonic.wem.api.space.SpaceName;
import com.enonic.wem.api.space.Spaces;

public interface SpaceDao
{
    Space createSpace( Space space, Session session );

    Space getSpace( SpaceName spaceName, Session session );

    Spaces getAllSpaces( Session session );

    void updateSpace( Space space, Session session );

    void deleteSpace( SpaceName spaceName, Session session );
}
