package com.enonic.xp.admin.impl.json.schema.mixin;

import java.time.Instant;
import java.util.List;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.admin.impl.json.ItemJson;
import com.enonic.xp.admin.impl.rest.resource.schema.mixin.MixinIconUrlResolver;
import com.enonic.wem.api.form.FormItem;
import com.enonic.wem.api.form.FormItemJson;
import com.enonic.wem.api.form.FormItemJsonFactory;
import com.enonic.wem.api.schema.mixin.Mixin;

public class MixinJson
    implements ItemJson
{
    private final Mixin mixin;

    private final String iconUrl;

    public MixinJson( final Mixin mixin, final MixinIconUrlResolver iconUrlResolver )
    {
        this.mixin = mixin;
        this.iconUrl = iconUrlResolver.resolve( mixin );
    }

    public String getName()
    {
        return mixin.getName() != null ? mixin.getName().toString() : null;
    }

    public String getDisplayName()
    {
        return mixin.getDisplayName();
    }

    public String getDescription()
    {
        return mixin.getDescription();
    }

    public Instant getCreatedTime()
    {
        return mixin.getCreatedTime();
    }

    public Instant getModifiedTime()
    {
        return mixin.getModifiedTime();
    }

    public String getIconUrl()
    {
        return iconUrl;
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
        return false;
    }

    @Override
    public boolean getEditable()
    {
        return false;
    }
}
