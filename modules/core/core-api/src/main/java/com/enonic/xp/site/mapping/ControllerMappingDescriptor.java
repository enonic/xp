package com.enonic.xp.site.mapping;

import java.util.Objects;
import java.util.regex.Pattern;

import com.google.common.annotations.Beta;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.resource.ResourceKey;

@Beta
public final class ControllerMappingDescriptor
    implements Comparable<ControllerMappingDescriptor>
{
    private static final int DEFAULT_ORDER = 50;

    private static final Pattern DEFAULT_PATTERN = Pattern.compile( "/.*" );

    private final ResourceKey controller;

    private final Pattern pattern;

    private final MappingConstraint match;

    private final int order;

    private ControllerMappingDescriptor( final Builder builder )
    {
        Preconditions.checkNotNull( builder.controller, "controller cannot be null" );
        this.controller = builder.controller;
        this.pattern = builder.pattern != null ? builder.pattern : DEFAULT_PATTERN;
        this.match = builder.match;
        this.order = builder.order;
    }

    public ApplicationKey getApplication()
    {
        return this.controller.getApplicationKey();
    }

    public ResourceKey getController()
    {
        return controller;
    }

    public Pattern getPattern()
    {
        return pattern;
    }

    public MappingConstraint getMatch()
    {
        return match;
    }

    public int getOrder()
    {
        return order;
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
        final ControllerMappingDescriptor that = (ControllerMappingDescriptor) o;
        return order == that.order &&
            Objects.equals( controller, that.controller ) &&
            Objects.equals( pattern.toString(), that.pattern.toString() ) &&
            Objects.equals( match, that.match );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( controller, pattern.toString(), match, order );
    }

    @Override
    public int compareTo( final ControllerMappingDescriptor o )
    {
        return Integer.compare( o.getOrder(), this.getOrder() );
    }

    @Override
    public String toString()
    {
        return MoreObjects.toStringHelper( this ).
            add( "controller", controller ).
            add( "pattern", pattern ).
            add( "match", match ).
            add( "order", order ).toString();
    }

    public static ControllerMappingDescriptor.Builder create()
    {
        return new Builder();
    }

    public static ControllerMappingDescriptor.Builder copyOf( final ControllerMappingDescriptor mappingDescriptor )
    {
        return new Builder( mappingDescriptor );
    }

    public static class Builder
    {
        private ResourceKey controller;

        private Pattern pattern;

        private MappingConstraint match;

        private int order = DEFAULT_ORDER;

        private Builder( final ControllerMappingDescriptor mappingDescriptor )
        {
            this.controller = mappingDescriptor.getController();
            this.pattern = mappingDescriptor.getPattern();
            this.match = mappingDescriptor.getMatch();
            this.order = mappingDescriptor.getOrder();
        }

        private Builder()
        {
        }

        public Builder controller( final ResourceKey controller )
        {
            this.controller = controller;
            return this;
        }

        public Builder pattern( final Pattern pattern )
        {
            this.pattern = pattern;
            return this;
        }

        public Builder pattern( final String pattern )
        {
            this.pattern = pattern != null ? Pattern.compile( pattern ) : null;
            return this;
        }

        public Builder match( final MappingConstraint match )
        {
            this.match = match;
            return this;
        }

        public Builder match( final String match )
        {
            this.match = match != null ? MappingConstraint.parse( match ) : null;
            return this;
        }

        public Builder order( final int order )
        {
            this.order = order;
            return this;
        }

        public ControllerMappingDescriptor build()
        {
            return new ControllerMappingDescriptor( this );
        }
    }

}
