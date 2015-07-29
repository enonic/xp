package com.enonic.xp.form.inputtype;

import java.util.Map;
import java.util.Objects;

import com.google.common.annotations.Beta;
import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

import com.enonic.xp.data.Property;
import com.enonic.xp.form.InvalidValueException;

@Beta
public final class FileUploaderConfig
    implements InputTypeConfig
{
    private final ImmutableMap<String, String> allowTypes;

    private boolean hideDropZone;

    private FileUploaderConfig( final Builder builder )
    {
        this.hideDropZone = builder.hideDropZone;
        this.allowTypes = builder.allowTypes != null ? ImmutableMap.copyOf( builder.allowTypes ) : ImmutableMap.of();
    }

    public ImmutableSet<String> getAllowTypeNames()
    {
        return allowTypes.keySet();
    }

    public String getAllowTypeExtensions( final String typeName )
    {
        return allowTypes.get( typeName );
    }

    public boolean hideDropZone()
    {
        return hideDropZone;
    }

    @Override
    public void checkValidity( final Property property )
        throws InvalidValueException
    {

    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }
        final FileUploaderConfig that = (FileUploaderConfig) o;
        return Objects.equals( hideDropZone, that.hideDropZone ) && Objects.equals( allowTypes, that.allowTypes );
    }

    @Override
    public String toString()
    {
        return MoreObjects.toStringHelper( this ).
            add( "allowTypes", allowTypes ).
            add( "hideDropZone", hideDropZone ).toString();
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( allowTypes, hideDropZone );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private Map<String, String> allowTypes;

        private boolean hideDropZone;

        private Builder()
        {
            this.allowTypes = Maps.newHashMap();
        }

        public Builder allowType( final String name, final String extensions )
        {
            this.allowTypes.put( name, extensions );
            return this;
        }

        public Builder hideDropZone( final boolean hideDropZone )
        {
            this.hideDropZone = hideDropZone;
            return this;
        }

        public FileUploaderConfig build()
        {
            return new FileUploaderConfig( this );
        }
    }

}
