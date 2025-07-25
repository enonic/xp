package com.enonic.xp.site.mapping;

import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.resource.ResourceKey;

@PublicApi
public final class ControllerMappingDescriptor
    implements Comparable<ControllerMappingDescriptor>
{
    private static final int DEFAULT_ORDER = 50;

    private static final Pattern DEFAULT_PATTERN = Pattern.compile( "/.*" );

    private final ResourceKey controller;

    private final ResourceKey filter;

    private final Pattern pattern;

    private final String service;

    private final boolean invertPattern;

    private final ContentMappingConstraint contentConstraint;

    private final int order;

    private final ApplicationKey applicationKey;

    private ControllerMappingDescriptor( final Builder builder )
    {
        Preconditions.checkArgument( builder.controller != null ^ builder.filter != null,
                                     "only one of either controller or filter must be specified" );
        Preconditions.checkArgument( builder.pattern == null && builder.contentConstraint == null || builder.service == null,
                                     "pattern and contentConstraint cannot be set together with service" );
        this.service = builder.service;
        this.controller = builder.controller;
        this.filter = builder.filter;
        this.applicationKey = builder.controller != null ? builder.controller.getApplicationKey() : builder.filter.getApplicationKey();
        this.pattern = builder.pattern != null ? builder.pattern : builder.service == null ? DEFAULT_PATTERN : null;
        this.invertPattern = builder.invertPattern;
        this.contentConstraint = builder.contentConstraint;
        this.order = builder.order;
    }

    public ApplicationKey getApplication()
    {
        return this.applicationKey;
    }

    public ResourceKey getController()
    {
        return controller;
    }

    public ResourceKey getFilter()
    {
        return filter;
    }

    public Pattern getPattern()
    {
        return pattern;
    }

    public String getService()
    {
        return service;
    }

    public boolean invertPattern()
    {
        return invertPattern;
    }

    public ContentMappingConstraint getContentConstraint()
    {
        return contentConstraint;
    }

    public int getOrder()
    {
        return order;
    }

    public boolean isController()
    {
        return this.controller != null;
    }

    public boolean isFilter()
    {
        return this.filter != null;
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
        return order == that.order && invertPattern == that.invertPattern && Objects.equals( service, that.service ) &&
            Objects.equals( controller, that.controller ) && Objects.equals( filter, that.filter ) &&
            Objects.equals( contentConstraint, that.contentConstraint ) &&
            Objects.equals( Optional.ofNullable( pattern ).map( Pattern::toString ).orElse( null ),
                            Optional.ofNullable( that.pattern ).map( Pattern::toString ).orElse( null ) );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( service, controller, filter, invertPattern, contentConstraint, order,
                             Optional.ofNullable( pattern ).map( Pattern::toString ).orElse( null ) );
    }

    @Override
    public int compareTo( final ControllerMappingDescriptor o )
    {
        return Integer.compare( o.getOrder(), this.getOrder() );
    }

    @Override
    public String toString()
    {
        return MoreObjects.toStringHelper( this )
            .add( "service", service )
            .add( "controller", controller )
            .add( "filter", filter )
            .add( "pattern", pattern )
            .add( "invertPattern", invertPattern )
            .add( "contentConstraint", contentConstraint )
            .add( "order", order )
            .toString();
    }

    public static ControllerMappingDescriptor.Builder create()
    {
        return new Builder();
    }

    public static ControllerMappingDescriptor.Builder copyOf( final ControllerMappingDescriptor mappingDescriptor )
    {
        return new Builder( mappingDescriptor );
    }

    public static final class Builder
    {
        private String service;

        private ResourceKey controller;

        private ResourceKey filter;

        private Pattern pattern;

        private boolean invertPattern = false;

        private ContentMappingConstraint contentConstraint;

        private int order = DEFAULT_ORDER;

        private Builder( final ControllerMappingDescriptor mappingDescriptor )
        {
            this.service = mappingDescriptor.getService();
            this.controller = mappingDescriptor.getController();
            this.filter = mappingDescriptor.getFilter();
            this.pattern = mappingDescriptor.getPattern();
            this.invertPattern = mappingDescriptor.invertPattern();
            this.contentConstraint = mappingDescriptor.getContentConstraint();
            this.order = mappingDescriptor.getOrder();
        }

        private Builder()
        {
        }

        public Builder service( final String service )
        {
            this.service = service;
            return this;
        }

        public Builder controller( final ResourceKey controller )
        {
            this.controller = controller;
            return this;
        }

        public Builder filter( final ResourceKey filter )
        {
            this.filter = filter;
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

        public Builder invertPattern( final boolean invertPattern )
        {
            this.invertPattern = invertPattern;
            return this;
        }

        public Builder contentConstraint( final ContentMappingConstraint contentConstraint )
        {
            this.contentConstraint = contentConstraint;
            return this;
        }

        public Builder contentConstraint( final String contentConstraint )
        {
            this.contentConstraint = contentConstraint != null ? ContentMappingConstraint.parse( contentConstraint ) : null;
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
