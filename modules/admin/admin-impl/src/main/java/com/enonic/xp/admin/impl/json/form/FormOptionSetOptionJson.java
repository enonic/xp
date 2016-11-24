package com.enonic.xp.admin.impl.json.form;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import com.enonic.xp.form.FormItem;
import com.enonic.xp.form.FormItems;
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

    private static List<FormItemJson> wrapFormItems( final FormItems items )
    {
        final List<FormItemJson> formItemJsonList = new ArrayList<>();
        for ( FormItem formItem : items )
        {
            formItemJsonList.add( FormItemJsonFactory.create( formItem ) );
        }
        return formItemJsonList;
    }

    public String getName()
    {
        return formOptionSetOption.getName();
    }

    public String getLabel()
    {
        return formOptionSetOption.getLabel();
    }

    public String getHelpText()
    {
        return formOptionSetOption.getHelpText();
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
