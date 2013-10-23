package com.enonic.wem.portal.dispatch;

import com.enonic.wem.portal.AbstractPortalException;

public class SpaceNotFoundException
    extends AbstractPortalException
{

    public SpaceNotFoundException( final String spaceName )
    {
        super( "Space " + spaceName + " not found" );
    }
}
