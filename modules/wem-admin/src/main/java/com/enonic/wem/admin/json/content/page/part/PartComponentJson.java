package com.enonic.wem.admin.json.content.page.part;


import com.enonic.wem.admin.json.content.page.PageComponentJson;
import com.enonic.wem.api.content.page.part.PartComponent;

@SuppressWarnings("UnusedDeclaration")
public class PartComponentJson
    extends PageComponentJson
{
    private final PartComponent part;

    public PartComponentJson( final PartComponent component )
    {
        super( component );
        this.part = component;
    }

    public String getName()
    {
        return part.getName().toString();
    }
}
