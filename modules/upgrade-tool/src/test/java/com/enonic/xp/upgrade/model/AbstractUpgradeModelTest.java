package com.enonic.xp.upgrade.model;

import com.google.common.base.Charsets;
import com.google.common.io.CharSource;
import com.google.common.io.Resources;

import com.enonic.xp.upgrade.xml.DomHelper;

import static org.junit.Assert.*;

public abstract class AbstractUpgradeModelTest
{
    protected void assertResult( final String upgraded, final String fileName )
        throws Exception
    {
        assertEquals( DomHelper.serialize( DomHelper.parse( upgraded ) ),
                      DomHelper.serialize( DomHelper.parse( getSource( fileName ).openStream() ) ) );
    }

    protected CharSource getSource( final String name )
    {
        return Resources.asCharSource( this.getClass().getResource( name ), Charsets.UTF_8 );
    }
}
