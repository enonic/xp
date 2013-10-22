package com.enonic.wem.admin.json.schema.mixin;

import java.util.List;

import com.google.common.collect.ImmutableList;

import com.enonic.wem.admin.json.DateTimeFormatter;
import com.enonic.wem.admin.json.ItemJson;
import com.enonic.wem.admin.json.schema.content.form.FormItemJson;
import com.enonic.wem.admin.json.schema.content.form.FormItemJsonFactory;
import com.enonic.wem.admin.rest.resource.schema.SchemaImageUriResolver;
import com.enonic.wem.api.form.FormItem;
import com.enonic.wem.api.schema.mixin.Mixin;

public class MixinJson
    implements ItemJson
{
    private final Mixin mixin;

    private final String iconUrl;

    private final boolean editable;

    private final boolean deletable;

    public MixinJson( final Mixin mixin )
    {
        this.mixin = mixin;
        this.iconUrl = SchemaImageUriResolver.resolve( mixin.getSchemaKey() );
        this.editable = true;
        this.deletable = true;
    }

    public String getName()
    {
        return mixin.getName();
    }

    public String getDisplayName()
    {
        return mixin.getDisplayName();
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

    public String getIconUrl()
    {
        return this.iconUrl;
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

    public String getCreatedTime()
    {
        return DateTimeFormatter.format( mixin.getCreatedTime() );
    }

    public String getCreator()
    {
        return mixin.getCreator() != null ? mixin.getCreator().toString() : null;
    }

    public String getModifiedTime()
    {
        return mixin.getModifiedTime() != null ? DateTimeFormatter.format( mixin.getModifiedTime() ) : null;
    }

    public String getModifier()
    {
        return mixin.getModifier() != null ? mixin.getModifier().toString() : null;
    }
}
