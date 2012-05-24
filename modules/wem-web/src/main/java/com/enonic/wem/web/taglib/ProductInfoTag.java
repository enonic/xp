package com.enonic.wem.web.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import com.enonic.cms.core.product.ProductVersion;

public final class ProductInfoTag
    extends SimpleTagSupport
{
    @Override
    public void doTag()
        throws JspException, IOException
    {
        final JspWriter out = getJspContext().getOut();
        out.write( ProductVersion.getFullTitleAndVersion() );
    }
}
