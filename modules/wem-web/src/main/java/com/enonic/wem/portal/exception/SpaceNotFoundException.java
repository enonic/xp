package com.enonic.wem.portal.exception;

public class SpaceNotFoundException
    extends AbstractPortalException
{

    public SpaceNotFoundException( final String spaceName )
    {
        super( "Space " + spaceName + " not found" );
    }
}
