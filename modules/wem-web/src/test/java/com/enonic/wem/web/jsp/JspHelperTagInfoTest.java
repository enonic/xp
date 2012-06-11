package com.enonic.wem.web.jsp;

import java.util.Hashtable;

import javax.servlet.jsp.tagext.TagData;
import javax.servlet.jsp.tagext.VariableInfo;

import org.junit.Test;

import static org.junit.Assert.*;

public class JspHelperTagInfoTest
{
    @Test
    public void testTag()
        throws Exception
    {
        final Hashtable<String, Object> map = new Hashtable<String, Object>();
        map.put( "var", "helper" );

        final JspHelperTagInfo info = new JspHelperTagInfo();
        final TagData tagData = new TagData( map );

        final VariableInfo[] list = info.getVariableInfo( tagData );

        assertNotNull( list );
        assertEquals( 1, list.length );

        final VariableInfo varInfo = list[0];

        assertNotNull( varInfo );
        assertEquals( "helper", varInfo.getVarName() );
        assertEquals( JspHelper.class.getName(), varInfo.getClassName() );
    }
}
