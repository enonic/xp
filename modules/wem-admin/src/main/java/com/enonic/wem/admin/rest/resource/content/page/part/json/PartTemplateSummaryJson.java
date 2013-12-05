package com.enonic.wem.admin.rest.resource.content.page.part.json;

import com.enonic.wem.admin.json.ItemJson;
import com.enonic.wem.api.content.page.part.PartTemplate;

public class PartTemplateSummaryJson implements  ItemJson
{

    private PartTemplate partTemplate;

    private boolean deletable;

    private boolean editable;

    public PartTemplateSummaryJson( PartTemplate partTemplate )
    {
        this.partTemplate = partTemplate;
        this.editable = true;
        this.deletable = true;
    }

    public String getSiteTemplateKey()
    {
        return partTemplate.getKey().getSiteTemplateKey().toString();
    }

    public String getKey() {
        return partTemplate.getKey().toString();
    }

    public String getName()
    {
        return partTemplate.getName().toString();
    }

    public String getDisplayName()
    {
        return partTemplate.getDisplayName().toString();
    }

    public String getModuleKey()
    {
        return partTemplate.getKey().getModuleKey().toString();
    }

    public String getDescriptor()
    {
        return partTemplate.getDescriptor().toString();
    }

    @Override
    public boolean getEditable()
    {
        return editable;
    }

    @Override
    public boolean getDeletable()
    {
        return deletable;
    }
}
