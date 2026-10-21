package com.enonic.xp.resource;

import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.icon.IconUpdate;

import static java.util.Objects.requireNonNull;

/**
 * Parameters for creating or updating a part, layout or page descriptor, see {@link DynamicSchemaService#createOrUpdatePart},
 * {@link DynamicSchemaService#createOrUpdateLayout} and {@link DynamicSchemaService#createOrUpdatePage}.
 */
public final class CreateOrUpdateComponentParams
{
    private final DescriptorKey key;

    private final String resource;

    private final IconUpdate icon;

    private CreateOrUpdateComponentParams( final Builder builder )
    {
        this.key = builder.key;
        this.resource = builder.resource;
        this.icon = builder.icon;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public DescriptorKey getKey()
    {
        return key;
    }

    /**
     * The descriptor to store, required to create the component. {@code null} on update keeps the stored descriptor, so that the
     * icon can be changed alone.
     */
    public String getResource()
    {
        return resource;
    }

    /**
     * What becomes of the icon, {@link IconUpdate#KEEP} unless set. Only a part has an icon.
     */
    public IconUpdate getIcon()
    {
        return icon;
    }

    public static final class Builder
    {
        private DescriptorKey key;

        private String resource;

        private IconUpdate icon = IconUpdate.KEEP;

        private Builder()
        {
        }

        public Builder key( final DescriptorKey key )
        {
            this.key = key;
            return this;
        }

        public Builder resource( final String resource )
        {
            this.resource = resource;
            return this;
        }

        public Builder icon( final IconUpdate icon )
        {
            this.icon = icon;
            return this;
        }

        private void validate()
        {
            requireNonNull( key, "key is required" );
            requireNonNull( icon, "icon is required" );
        }

        public CreateOrUpdateComponentParams build()
        {
            validate();
            return new CreateOrUpdateComponentParams( this );
        }
    }
}
