package com.enonic.wem.admin.json.form;

import com.enonic.wem.api.form.FormItemSet;

@SuppressWarnings("UnusedDeclaration")
public class FormItemSetJson
    extends FormItemJson
{
    private final FormItemSet formItemSet;

    private final FormItemJsonArray items;

    private final OccurrencesJson occurrences;

    public FormItemSetJson( final FormItemSet formItemSet )
    {
        super( formItemSet );
        this.formItemSet = formItemSet;
        this.items = new FormItemJsonArray( formItemSet.getFormItems() );
        this.occurrences = new OccurrencesJson( formItemSet.getOccurrences() );
    }

    public String getLabel()
    {
        return formItemSet.getLabel();
    }

    public boolean isImmutable()
    {
        return formItemSet.isImmutable();
    }

    public String getCustomText()
    {
        return formItemSet.getCustomText();
    }

    public String getHelpText()
    {
        return formItemSet.getHelpText();
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
