package com.enonic.xp.page;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.region.Component;
import com.enonic.xp.region.Regions;

@PublicApi
public final class EditablePage
{
    @Nullable
    public DescriptorKey descriptor;

    @Nullable
    public PageTemplateKey template;

    @Nullable
    public Regions regions;

    @Nullable
    public Component fragment;

    @Nullable
    public PropertyTree config;

    public boolean customized;

    public EditablePage()
    {
    }

    public EditablePage( @NonNull final Page source )
    {
        this.descriptor = source.getDescriptor();
        this.template = source.getTemplate();
        this.regions = source.hasRegions() ? source.getRegions().copy() : null;
        this.config = source.hasConfig() ? source.getConfig().copy() : null;
        this.customized = source.isCustomized();
        this.fragment = source.getFragment();
    }

    @NonNull
    public Page build()
    {
        final Page.Builder builder = Page.create();
        builder.descriptor( descriptor );
        builder.template( template );
        builder.regions( regions );
        builder.config( config );
        builder.customized( customized );
        builder.fragment( fragment );
        return builder.build();
    }
}
