package com.enonic.wem.web.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import com.google.common.base.Splitter;

public final class EllipsisTag
    extends SimpleTagSupport
{
    private String text;

    private int length = 30;

    public void setText( final String text )
    {
        this.text = text;
    }

    public void setLength( final int length )
    {
        this.length = length;
    }

    @Override
    public void doTag()
        throws JspException, IOException
    {
        final JspWriter out = getJspContext().getOut();

        if (this.text.length() <= this.length) {
            out.write( this.text );
        } else {
            final String outStr = Splitter.fixedLength( this.length ).split( this.text ).iterator().next();
            out.write( outStr  + "...");
        }
    }
}
