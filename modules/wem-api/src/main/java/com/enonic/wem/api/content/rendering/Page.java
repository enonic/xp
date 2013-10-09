package com.enonic.wem.api.content.rendering;


import com.enonic.wem.api.data.RootDataSet;

public class Page
    implements Renderable
{
    private Controller controller;

    private RootDataSet config;

    private PageRegions pageRegions;

    public Controller getController()
    {
        return controller;
    }

    public PageRegions getPageRegions()
    {
        return pageRegions;
    }
}
