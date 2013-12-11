package com.enonic.wem.admin.json.content.page.part;

import java.util.List;

import com.google.common.collect.ImmutableList;

import com.enonic.wem.api.content.page.part.PartTemplate;
import com.enonic.wem.api.content.page.part.PartTemplates;

public class PartTemplateListJson
{

    private List<PartTemplateSummaryJson> list;

    public PartTemplateListJson(PartTemplates partTemplates)
    {
        ImmutableList.Builder<PartTemplateSummaryJson> builder = ImmutableList.builder();
        for (PartTemplate partTemplate: partTemplates)
        {
            builder.add( new PartTemplateSummaryJson(partTemplate) );
        }
        list = builder.build();
    }

    public int getTotal()
    {
        return list.size();
    }

    public List<PartTemplateSummaryJson> getTemplates()
    {
        return list;
    }
}
