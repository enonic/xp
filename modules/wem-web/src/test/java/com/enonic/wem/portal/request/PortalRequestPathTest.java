package com.enonic.wem.portal.request;

import org.junit.Test;

import com.enonic.wem.api.space.SpaceName;

import static junit.framework.Assert.assertEquals;

public class PortalRequestPathTest
{
    @Test
    public void testEmptyRelativePath()
        throws Exception
    {
        PortalRequestPath path = new PortalRequestPath( SpaceName.from( "testSpace" ) );

        assertEquals( "/", path.getRelativePathAsString() );
    }


    @Test
    public void testRelativePath()
    {
        PortalRequestPath path = new PortalRequestPath( SpaceName.from( "testSpace" ) );

        path.appendPath( "element1" );
        path.appendPath( "element2" );
        path.appendPath( "element3" );

        assertEquals( "/element1/element2/element3", path.getRelativePathAsString() );
    }

    @Test
    public void testEmptyPath()
    {
        PortalRequestPath path = new PortalRequestPath( SpaceName.from( "testSpace" ) );

        assertEquals( "testSpace:/", path.getPathAsString() );


    }

}
