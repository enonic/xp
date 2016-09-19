package com.enonic.xp.admin.impl.json.form;

import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnore;

import com.enonic.xp.form.FormItem;
import com.enonic.xp.form.FormOptionSetOption;

public class FormOptionSetOptionJson
{

    private final FormOptionSetOption formOptionSetOption;

    private final List<FormItemJson> items;

    public FormOptionSetOptionJson( final FormOptionSetOption formOptionSetOption )
    {
        this.formOptionSetOption = formOptionSetOption;
        this.items = wrapFormItems( formOptionSetOption.getFormItems() );
    }

    private static List<FormItemJson> wrapFormItems( final List<FormItem> items )
    {
        return items.stream().map( e -> FormItemJsonFactory.create( e ) ).collect( Collectors.toList() );
    }

    public String getName()
    {
        return formOptionSetOption.getName();
    }

    public String getLabel()
    {
        return formOptionSetOption.getLabel();
    }

    public boolean isDefaultOption()
    {
        return formOptionSetOption.isDefaultOption();
    }

    public List<FormItemJson> getItems()
    {
        return items;
    }

    @JsonIgnore
    public FormOptionSetOption getFormOptionSetOption()
    {
        return formOptionSetOption;
    }
}
