package com.enonic.wem.admin.json.content.page.part;


import java.util.List;

import com.enonic.wem.admin.json.content.page.PageComponentJson;
import com.enonic.wem.admin.json.data.DataJson;
import com.enonic.wem.admin.json.data.RootDataSetJson;
import com.enonic.wem.api.content.page.part.PartComponent;

@SuppressWarnings("UnusedDeclaration")
public class PartComponentJson
    extends PageComponentJson
{
    private final PartComponent part;

    private final List<DataJson> config;

    public PartComponentJson( final PartComponent component )
    {
        super( component );
        this.part = component;
        this.config = new RootDataSetJson( part.getConfig() ).getSet();
    }

    public String getName()
    {
        return part.getName().toString();
    }

    public List<DataJson> getConfig()
    {
        return config;
    }
}
