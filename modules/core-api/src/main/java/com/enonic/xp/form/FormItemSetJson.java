package com.enonic.xp.form;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.annotations.Beta;

@Beta
@SuppressWarnings("UnusedDeclaration")
public class FormItemSetJson
    extends FormItemJson<FormItemSet>
{
    private final FormItemSet formItemSet;

    private final List<FormItemJson> items;

    private final OccurrencesJson occurrences;

    public FormItemSetJson( final FormItemSet formItemSet )
    {
        this.formItemSet = formItemSet;
        this.items = wrapFormItems( formItemSet.getFormItems() );
        this.occurrences = new OccurrencesJson( formItemSet.getOccurrences() );
    }

    private static Iterable<FormItem> unwrapFormItems( final List<FormItemJson> items )
    {
        final List<FormItem> formItems = new ArrayList<>( items.size() );
        for ( FormItemJson formItemJson : items )
        {
            formItems.add( formItemJson.getFormItem() );
        }
        return formItems;
    }

    private static List<FormItemJson> wrapFormItems( final FormItems items )
    {
        final List<FormItemJson> formItemJsonList = new ArrayList<>();
        for ( FormItem formItem : items )
        {
            formItemJsonList.add( FormItemJsonFactory.create( formItem ) );
        }
        return formItemJsonList;
    }

    @JsonIgnore
    @Override
    public FormItemSet getFormItem()
    {
        return getFormItemSet();
    }

    @JsonIgnore
    public FormItemSet getFormItemSet()
    {
        return formItemSet;
    }

    @Override
    public String getName()
    {
        return formItemSet.getName();
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

    public List<FormItemJson> getItems()
    {
        return items;
    }

    public OccurrencesJson getOccurrences()
    {
        return occurrences;
    }
}
