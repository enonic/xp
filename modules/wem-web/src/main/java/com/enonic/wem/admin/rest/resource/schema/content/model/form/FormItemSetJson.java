package com.enonic.wem.admin.rest.resource.schema.content.model.form;

import com.enonic.wem.api.schema.content.form.FormItemSet;

public class FormItemSetJson
    extends FormItemJson
{
    private final FormItemSet model;

    private final FormItemJsonArray items;

    private final OccurrencesJson occurrences;

    public FormItemSetJson( final FormItemSet formItem )
    {
        this.model = formItem;
        this.items = new FormItemJsonArray( formItem.getFormItems() );
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

    public FormItemJsonArray getItems()
    {
        return items;
    }

    public OccurrencesJson getOccurrences()
    {
        return occurrences;
    }
}
