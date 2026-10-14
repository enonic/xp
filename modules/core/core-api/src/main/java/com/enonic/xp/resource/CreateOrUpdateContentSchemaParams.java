package com.enonic.xp.resource;

import com.enonic.xp.icon.IconUpdate;
import com.enonic.xp.schema.BaseSchemaName;

import static java.util.Objects.requireNonNull;

/**
 * Parameters for creating or updating a content type, form fragment or mixin, see
 * {@link DynamicSchemaService#createOrUpdateContentType}, {@link DynamicSchemaService#createOrUpdateFormFragment} and
 * {@link DynamicSchemaService#createOrUpdateMixin}.
 */
public final class CreateOrUpdateContentSchemaParams
{
    private final BaseSchemaName name;

    private final String resource;

    private final IconUpdate icon;

    private CreateOrUpdateContentSchemaParams( final Builder builder )
    {
        this.name = builder.name;
        this.resource = builder.resource;
        this.icon = builder.icon;
    }

    public static Builder create()
    {
        return new Builder();
    }

    /**
     * The name of the kind of the service method called: a {@link com.enonic.xp.schema.content.ContentTypeName},
     * {@link com.enonic.xp.schema.formfragment.FormFragmentName} or {@link com.enonic.xp.schema.mixin.MixinName}.
     */
    public BaseSchemaName getName()
    {
        return name;
    }

    /**
     * The schema to store, required to create it. {@code null} on update keeps the stored schema, so that the icon can be
     * changed alone.
     */
    public String getResource()
    {
        return resource;
    }

    /**
     * What becomes of the icon, {@link IconUpdate#KEEP} unless set.
     */
    public IconUpdate getIcon()
    {
        return icon;
    }

    public static final class Builder
    {
        private BaseSchemaName name;

        private String resource;

        private IconUpdate icon = IconUpdate.KEEP;

        private Builder()
        {
        }

        public Builder name( final BaseSchemaName name )
        {
            this.name = name;
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
            requireNonNull( name, "name is required" );
            requireNonNull( icon, "icon is required" );
        }

        public CreateOrUpdateContentSchemaParams build()
        {
            validate();
            return new CreateOrUpdateContentSchemaParams( this );
        }
    }
}
