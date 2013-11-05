package com.enonic.wem.admin.json.schema.mixin;

import java.util.List;

import com.google.common.collect.ImmutableList;

import com.enonic.wem.admin.json.ItemJson;
import com.enonic.wem.admin.json.form.FormItemJson;
import com.enonic.wem.admin.json.form.FormItemJsonFactory;
import com.enonic.wem.admin.json.schema.SchemaJson;
import com.enonic.wem.api.form.FormItem;
import com.enonic.wem.api.schema.mixin.Mixin;

public class MixinJson
    extends SchemaJson
    implements ItemJson
{
    private final Mixin mixin;

    private final boolean editable;

    private final boolean deletable;

    public MixinJson( final Mixin mixin )
    {
        super( mixin );
        this.mixin = mixin;

        this.editable = true;
        this.deletable = true;
    }

    public List<FormItemJson> getItems()
    {
        ImmutableList.Builder<FormItemJson> builder = ImmutableList.builder();
        for ( FormItem formItem : mixin.getFormItems() )
        {
            builder.add( FormItemJsonFactory.create( formItem ) );
        }
        return builder.build();
    }

    public String getCreator()
    {
        return mixin.getCreator() != null ? mixin.getCreator().toString() : null;
    }

    public String getModifier()
    {
        return mixin.getModifier() != null ? mixin.getModifier().toString() : null;
    }

    @Override
    public boolean getDeletable()
    {
        return deletable;
    }

    @Override
    public boolean getEditable()
    {
        return editable;
    }
}
