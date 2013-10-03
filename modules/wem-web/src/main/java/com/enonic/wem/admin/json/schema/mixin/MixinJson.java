package com.enonic.wem.admin.json.schema.mixin;

import java.util.List;

import com.google.common.collect.ImmutableList;

import com.enonic.wem.admin.json.ItemJson;
import com.enonic.wem.admin.json.schema.content.form.FormItemJson;
import com.enonic.wem.admin.json.schema.content.form.FormItemJsonFactory;
import com.enonic.wem.admin.rest.resource.schema.SchemaImageUriResolver;
import com.enonic.wem.api.schema.content.form.FormItem;
import com.enonic.wem.api.schema.mixin.Mixin;

public class MixinJson
    extends ItemJson
{
    private final Mixin model;

    private final String iconUrl;

    public MixinJson( final Mixin model )
    {
        this.model = model;
        this.iconUrl = SchemaImageUriResolver.resolve( model.getSchemaKey() );
    }

    public String getName()
    {
        return model.getName();
    }

    public String getDisplayName()
    {
        return model.getDisplayName();
    }

    public List<FormItemJson> getItems()
    {
        ImmutableList.Builder<FormItemJson> builder = ImmutableList.builder();
        for ( FormItem formItem : model.getFormItems() )
        {
            builder.add( FormItemJsonFactory.create( formItem ) );
        }
        return builder.build();
    }

    public String getIconUrl()
    {
        return this.iconUrl;
    }

    @Override
    public boolean getDeletable()
    {
        return false;
    }

    @Override
    public boolean getEditable()
    {
        return false;
    }
}
