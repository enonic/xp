package com.enonic.wem.web.jsp.site;

import java.util.Hashtable;

import javax.servlet.jsp.tagext.TagData;
import javax.servlet.jsp.tagext.VariableInfo;

import org.junit.Test;

import static org.junit.Assert.*;

public class SiteBeanTagInfoTest
{
    @Test
    public void testTag()
        throws Exception
    {
        final Hashtable<String, Object> map = new Hashtable<String, Object>();
        map.put( "var", "site" );

        final SiteBeanTagInfo info = new SiteBeanTagInfo();
        final TagData tagData = new TagData( map );

        final VariableInfo[] list = info.getVariableInfo( tagData );

        assertNotNull( list );
        assertEquals( 1, list.length );

        final VariableInfo varInfo = list[0];

        assertNotNull( varInfo );
        assertEquals( "site", varInfo.getVarName() );
        assertEquals( SiteBean.class.getName(), varInfo.getClassName() );
        assertEquals( true, varInfo.getDeclare() );
        assertEquals( VariableInfo.NESTED, varInfo.getScope() );
    }
}
