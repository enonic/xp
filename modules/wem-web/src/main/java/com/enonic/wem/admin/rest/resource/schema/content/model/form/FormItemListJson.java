package com.enonic.wem.admin.rest.resource.schema.content.model.form;

import java.util.List;

import com.google.common.collect.ImmutableList;

import com.enonic.wem.api.schema.content.form.FormItem;

public class FormItemListJson
{
    private final ImmutableList<FormItemJson> list;

    public FormItemListJson( final Iterable<FormItem> formItems )
    {
        final ImmutableList.Builder<FormItemJson> builder = ImmutableList.builder();
        for ( final FormItem formItem : formItems )
        {
            builder.add( new FormItemJson( formItem ) );
        }

        this.list = builder.build();
    }

    public int getTotal()
    {
        return this.list.size();
    }

    public List<FormItemJson> get()
    {
        return this.list;
    }
}
