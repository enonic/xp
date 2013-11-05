package com.enonic.wem.admin.json.form;


import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.wem.api.form.Form;
import com.enonic.wem.api.form.FormItem;

@SuppressWarnings("UnusedDeclaration")
public class FormJson
{
    private final Form form;

    private final List<FormItemJson> items;

    public FormJson( final Form form )
    {
        this.form = form;
        items = new ArrayList<>( form.size() );
        for ( FormItem formItem : form )
        {
            items.add( FormItemJsonFactory.create( formItem ) );
        }
    }

    @JsonCreator
    public FormJson( @JsonProperty("formItems") List<FormItemJson> items )
    {
        this.items = items;
        final Form.Builder builder = Form.newForm();
        for ( FormItemJson formItemJson : items )
        {
            builder.addFormItem( formItemJson.getFormItem() );
        }
        this.form = builder.build();
    }

    public List<FormItemJson> getFormItems()
    {
        return items;
    }

    @JsonIgnore
    public Form getForm()
    {
        return this.form;
    }
}
