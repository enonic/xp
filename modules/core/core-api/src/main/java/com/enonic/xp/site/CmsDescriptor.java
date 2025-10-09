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

    private final XDataMappings xDataMappings;

    private final Instant modifiedTime;

    private CmsDescriptor( final Builder builder )
    {
        this.applicationKey = Objects.requireNonNull( builder.applicationKey );
        this.form = requireNonNullElse( builder.form, Form.empty() );
        this.xDataMappings = requireNonNullElse( builder.xDataMappings, XDataMappings.empty() );
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

    public XDataMappings getXDataMappings()
    {
        return xDataMappings;
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

        private XDataMappings xDataMappings;

        private Instant modifiedTime;

        private Builder()
        {
        }

        private Builder( final CmsDescriptor cmsDescriptor )
        {
            this.applicationKey = cmsDescriptor.applicationKey;
            this.form = cmsDescriptor.form != null ? Form.create( cmsDescriptor.form ).build() : Form.empty();
            this.xDataMappings = cmsDescriptor.xDataMappings;
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

        public Builder xDataMappings( final XDataMappings xDataMappings )
        {
            this.xDataMappings = xDataMappings;
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
