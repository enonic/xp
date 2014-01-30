package com.enonic.wem.portal.view;

public interface ViewProcessor
{
    public String getName();

    public String process( RenderViewSpec spec );
}
