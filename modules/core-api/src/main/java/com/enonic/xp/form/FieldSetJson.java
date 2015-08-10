package com.enonic.xp.form;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.annotations.Beta;

@Beta
@SuppressWarnings("UnusedDeclaration")
public class FieldSetJson
    extends LayoutJson<FieldSet>
{
    private final FieldSet fieldSet;

    private final List<FormItemJson> items;

    public FieldSetJson( final FieldSet fieldSet )
    {
        super( fieldSet );
        this.fieldSet = fieldSet;
        this.items = wrapFormItems( fieldSet.getFormItems() );
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
    public FieldSet getFieldSet()
    {
        return fieldSet;
    }

    public String getLabel()
    {
        return fieldSet.getLabel();
    }

    public List<FormItemJson> getItems()
    {
        return items;
    }

}
