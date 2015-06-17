package com.enonic.xp.form;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.annotations.Beta;

@Beta
@SuppressWarnings("UnusedDeclaration")
public class FieldSetJson
    extends LayoutJson<FieldSet>
{
    private final FieldSet fieldSet;

    private final List<FormItemJson> items;

    @JsonCreator
    public FieldSetJson( @JsonProperty("name") String name, @JsonProperty("label") String label,
                         @JsonProperty("items") List<FormItemJson> items )
    {
        super( FieldSet.create().
            name( name ).
            label( label ).
            addFormItems( unwrapFormItems( items ) ).
            build() );
        this.fieldSet = this.getFormItem();
        this.items = items;
    }

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
