package com.enonic.wem.admin.rest.resource.content.page.image.json;

import com.enonic.wem.admin.json.ItemJson;
import com.enonic.wem.api.content.page.image.ImageTemplate;


public class ImageTemplateSummaryJson
    implements ItemJson
{
    protected final ImageTemplate imageTemplate;

    private final boolean editable;

    private final boolean deletable;

    public ImageTemplateSummaryJson( final ImageTemplate imageTemplate )
    {
        this.imageTemplate = imageTemplate;
        this.editable = true;
        this.deletable = true;
    }

    public String getDisplayName()
    {
        return imageTemplate.getDisplayName();
    }

    public String getName()
    {
        return imageTemplate.getName().toString();
    }

    public String getDescriptor()
    {
        return imageTemplate.getDescriptor().toString();
    }

    public String getKey()
    {
        return imageTemplate.getKey().toString();
    }

    public String getModuleKey()
    {
        return imageTemplate.getKey().getModuleKey().toString();
    }

    public String getSiteTemplateKey()
    {
        return imageTemplate.getKey().getSiteTemplateKey().toString();
    }

    @Override
    public boolean getDeletable()
    {
        return deletable;
    }

    @Override
    public boolean getEditable()
    {
        return editable;
    }

}
