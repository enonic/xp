package com.enonic.xp.site;

import java.time.Instant;
import java.util.Objects;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.form.Form;
import com.enonic.xp.resource.ResourceKey;

import static java.util.Objects.requireNonNullElse;

public final class CmsDescriptor
{
    private static final String CMS_DESCRIPTOR_PATH = "cms/cms.yml";

    private final ApplicationKey applicationKey;

    private final Form form;

    private final MixinMappings mixinMappings;

    private final Instant modifiedTime;

    private CmsDescriptor( final Builder builder )
    {
        this.applicationKey = Objects.requireNonNull( builder.applicationKey );
        this.form = requireNonNullElse( builder.form, Form.empty() );
        this.mixinMappings = requireNonNullElse( builder.mixinMappings, MixinMappings.empty() );
        this.modifiedTime = builder.modifiedTime;
    }

    public ApplicationKey getApplicationKey()
    {
        return applicationKey;
    }

    public Form getForm()
    {
        return form;
    }

    public MixinMappings getMixinMappings()
    {
        return mixinMappings;
    }

    public Instant getModifiedTime()
    {
        return modifiedTime;
    }

    public static ResourceKey toResourceKey( final ApplicationKey applicationKey )
    {
        return ResourceKey.from( applicationKey, CMS_DESCRIPTOR_PATH );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static Builder copyOf( final CmsDescriptor cmsDescriptor )
    {
        return new Builder( cmsDescriptor );
    }

    public static final class Builder
    {
        private ApplicationKey applicationKey;

        private Form form;

        private MixinMappings mixinMappings;

        private Instant modifiedTime;

        private Builder()
        {
        }

        private Builder( final CmsDescriptor cmsDescriptor )
        {
            this.applicationKey = cmsDescriptor.applicationKey;
            this.form = cmsDescriptor.form != null ? Form.create( cmsDescriptor.form ).build() : Form.empty();
            this.mixinMappings = cmsDescriptor.mixinMappings;
            this.modifiedTime = cmsDescriptor.modifiedTime;
        }

        public Builder applicationKey( final ApplicationKey applicationKey )
        {
            this.applicationKey = applicationKey;
            return this;
        }

        public Builder form( final Form form )
        {
            this.form = form;
            return this;
        }

        public Builder mixinMappings( final MixinMappings mixinMappings )
        {
            this.mixinMappings = mixinMappings;
            return this;
        }

        public Builder modifiedTime( final Instant modifiedTime )
        {
            this.modifiedTime = modifiedTime;
            return this;
        }

        public CmsDescriptor build()
        {
            return new CmsDescriptor( this );
        }
    }
}
