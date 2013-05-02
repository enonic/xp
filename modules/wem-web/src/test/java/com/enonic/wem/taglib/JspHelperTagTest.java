package com.enonic.wem.taglib;

import javax.servlet.jsp.tagext.Tag;

import org.junit.Test;
import org.springframework.mock.web.MockPageContext;

import static org.junit.Assert.*;

public class JspHelperTagTest
{
    @Test
    public void testTag()
        throws Exception
    {
        final MockPageContext pageContext = new MockPageContext();

        final JspHelperTag tag = new JspHelperTag();
        tag.setVar( "helper" );
        tag.setPageContext( pageContext );

        final int status = tag.doStartTag();
        assertEquals( Tag.SKIP_BODY, status );

        final Object helper = pageContext.getAttribute( "helper" );
        assertNotNull( helper );
        assertTrue( helper instanceof JspHelper );
    }
}
