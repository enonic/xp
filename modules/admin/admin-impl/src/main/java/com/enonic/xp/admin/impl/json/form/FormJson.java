package com.enonic.xp.admin.impl.json.form;


import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.annotations.Beta;

import com.enonic.xp.form.Form;
import com.enonic.xp.form.FormItem;

@Beta
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
