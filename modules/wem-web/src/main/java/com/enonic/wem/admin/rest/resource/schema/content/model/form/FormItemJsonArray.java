package com.enonic.wem.admin.rest.resource.schema.content.model.form;

import java.util.Iterator;

import com.google.common.collect.ImmutableList;

import com.enonic.wem.api.schema.content.form.FormItem;

public class FormItemJsonArray
    implements Iterable<FormItemJson>
{
    private final ImmutableList<FormItemJson> list;

    public FormItemJsonArray( final Iterable<FormItem> formItems )
    {
        final ImmutableList.Builder<FormItemJson> builder = ImmutableList.builder();
        for ( final FormItem formItem : formItems )
        {
            builder.add( FormItemJsonFactory.create( formItem ) );
        }

        this.list = builder.build();
    }

    @Override
    public Iterator<FormItemJson> iterator()
    {
        return this.list.iterator();
    }
}
