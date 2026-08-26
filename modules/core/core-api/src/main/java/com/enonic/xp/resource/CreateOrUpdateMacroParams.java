package com.enonic.xp.resource;

import com.enonic.xp.icon.IconUpdate;
import com.enonic.xp.macro.MacroKey;

import static java.util.Objects.requireNonNull;

/**
 * Parameters for creating or updating a macro descriptor, see {@link DynamicSchemaService#createOrUpdateMacro}.
 */
public final class CreateOrUpdateMacroParams
{
    private final MacroKey key;

    private final String resource;

    private final IconUpdate icon;

    private CreateOrUpdateMacroParams( final Builder builder )
    {
        this.key = builder.key;
        this.resource = builder.resource;
        this.icon = builder.icon;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public MacroKey getKey()
    {
        return key;
    }

    /**
     * The descriptor to store, required to create the macro. {@code null} on update keeps the stored descriptor, so that the
     * icon can be changed alone.
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
        private MacroKey key;

        private String resource;

        private IconUpdate icon = IconUpdate.KEEP;

        private Builder()
        {
        }

        public Builder key( final MacroKey key )
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

        public CreateOrUpdateMacroParams build()
        {
            validate();
            return new CreateOrUpdateMacroParams( this );
        }
    }
}
