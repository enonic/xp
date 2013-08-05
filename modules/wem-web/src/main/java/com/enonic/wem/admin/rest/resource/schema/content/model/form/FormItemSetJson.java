package com.enonic.wem.admin.rest.resource.schema.content.model.form;

import com.enonic.wem.admin.rest.resource.schema.content.model.form.inputtype.OccurrencesJson;
import com.enonic.wem.api.schema.content.form.FormItemSet;

public class FormItemSetJson
    extends AbstractFormItem
{
    private final FormItemSet model;

    private final FormItemListJson items;

    private final OccurrencesJson occurrences;

    public FormItemSetJson( final FormItemSet formItem )
    {
        this.model = formItem;

        this.items = new FormItemListJson( formItem.getFormItems() );

        this.occurrences = new OccurrencesJson( formItem.getOccurrences() );
    }

    public String getName()
    {
        return model.getName();
    }

    public String getLabel()
    {
        return model.getLabel();
    }

    public boolean isImmutable()
    {
        return model.isImmutable();
    }

    public String getCustomText()
    {
        return model.getCustomText();
    }

    public String getHelpText()
    {
        return model.getHelpText();
    }

    public FormItemListJson getItems()
    {
        return items;
    }

    public OccurrencesJson getOccurrences()
    {
        return occurrences;
    }
}
