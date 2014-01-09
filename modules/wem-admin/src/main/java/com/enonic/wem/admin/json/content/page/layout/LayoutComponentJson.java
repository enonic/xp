package com.enonic.wem.admin.json.content.page.layout;


import java.util.List;

import com.enonic.wem.admin.json.content.page.PageComponentJson;
import com.enonic.wem.admin.json.data.DataJson;
import com.enonic.wem.admin.json.data.RootDataSetJson;
import com.enonic.wem.api.content.page.layout.LayoutComponent;

@SuppressWarnings("UnusedDeclaration")
public class LayoutComponentJson
    extends PageComponentJson
{
    private final LayoutComponent layout;

    private final List<DataJson> config;

    public LayoutComponentJson( final LayoutComponent component )
    {
        super( component );
        this.layout = component;
        this.config = new RootDataSetJson( layout.getConfig() ).getSet();
    }

    public String getName()
    {
        return layout.getName().toString();
    }

    public List<DataJson> getConfig()
    {
        return config;
    }
}
