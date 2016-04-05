package com.enonic.xp.page;


import com.google.common.annotations.Beta;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.region.Component;

@Beta
public final class EditablePage
{
    public DescriptorKey controller;

    public PageTemplateKey template;

    public PageRegions regions;

    public Component fragment;

    public PropertyTree config;

    public boolean customized;

    public EditablePage( final Page source )
    {
        this.controller = source.getController();
        this.template = source.getTemplate();
        this.regions = source.hasRegions() ? source.getRegions().copy() : null;
        this.config = source.hasConfig() ? source.getConfig().copy() : null;
        this.customized = source.isCustomized();
        this.fragment = source.getFragment();
    }

    public Page build()
    {
        final Page.Builder builder = Page.create();
        builder.controller( controller );
        builder.template( template );
        builder.regions( regions );
        builder.config( config );
        builder.customized( customized );
        builder.fragment( fragment );
        return builder.build();
    }
}
