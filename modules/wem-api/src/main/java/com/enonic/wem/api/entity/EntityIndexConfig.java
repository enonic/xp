package com.enonic.wem.api.entity;

import java.util.Map;
import java.util.Objects;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import com.enonic.wem.api.data.DataPath;
import com.enonic.wem.api.data.Property;

public class EntityIndexConfig
{
    private final String analyzer;

    private final ImmutableMap<DataPath, PropertyIndexConfig> propertyIndexConfigs;

    public static Builder newEntityIndexConfig()
    {
        return new Builder();
    }

    private EntityIndexConfig( final Builder builder )
    {
        this.analyzer = builder.analyzer;
        this.propertyIndexConfigs = ImmutableMap.copyOf( builder.propertyIndexConfigs );
    }

    public String getAnalyzer()
    {
        return analyzer;
    }

    public PropertyIndexConfig getPropertyIndexConfig( final DataPath dataPath )
    {
        return propertyIndexConfigs.get( dataPath );
    }

    public Map<DataPath, PropertyIndexConfig> getPropertyIndexConfigs()
    {
        return propertyIndexConfigs;
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

        final EntityIndexConfig that = (EntityIndexConfig) o;
        return Objects.equals( this.analyzer, that.analyzer ) && Objects.equals( this.propertyIndexConfigs, that.propertyIndexConfigs );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( this.analyzer, this.propertyIndexConfigs );
    }

    public static class Builder
    {
        private String analyzer;

        private final Map<DataPath, PropertyIndexConfig> propertyIndexConfigs = Maps.newHashMap();

        public Builder analyzer( final String analyzer )
        {
            this.analyzer = analyzer;
            return this;
        }

        public Builder addPropertyIndexConfig( final Property property, final PropertyIndexConfig propertyIndexConfig )
        {
            propertyIndexConfigs.put( property.getPath(), propertyIndexConfig );
            return this;
        }

        public Builder addPropertyIndexConfig( final String path, final PropertyIndexConfig propertyIndexConfig )
        {
            propertyIndexConfigs.put( DataPath.from( path ), propertyIndexConfig );
            return this;
        }

        public EntityIndexConfig build()
        {
            return new EntityIndexConfig( this );
        }

    }


}
