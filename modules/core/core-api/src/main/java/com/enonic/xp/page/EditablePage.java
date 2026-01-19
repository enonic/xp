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
    public final @NonNull Page source;

    public @Nullable DescriptorKey descriptor;

    public @Nullable PageTemplateKey template;

    public @Nullable Regions regions;

    public @Nullable Component fragment;

    public @Nullable PropertyTree config;

    public boolean customized;

    public EditablePage( @NonNull final Page source )
    {
        this.source = source;
        this.descriptor = source.getDescriptor();
        this.template = source.getTemplate();
        this.regions = source.hasRegions() ? source.getRegions().copy() : null;
        this.config = source.hasConfig() ? source.getConfig().copy() : null;
        this.customized = source.isCustomized();
        this.fragment = source.getFragment();
    }

    public @NonNull Page build()
    {
        return Page.create()
            .descriptor( descriptor )
            .template( template )
            .regions( regions )
            .config( config )
            .customized( customized )
            .fragment( fragment )
            .build();
    }
}
