package com.enonic.wem.portal.view;

public interface ViewService
{
    public String renderView( RenderViewSpec spec )
        throws ViewException;
}
