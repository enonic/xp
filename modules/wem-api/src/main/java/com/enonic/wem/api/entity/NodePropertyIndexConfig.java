package com.enonic.wem.api.entity;

import java.util.Map;
import java.util.Objects;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import com.enonic.wem.api.data.DataPath;
import com.enonic.wem.api.data.Property;

public class NodePropertyIndexConfig
    extends NodeIndexConfig
{
    private final ImmutableMap<DataPath, PropertyIndexConfig> propertyIndexConfigs;

    private NodePropertyIndexConfig( final Builder builder )
    {
        super( builder );
        this.propertyIndexConfigs = ImmutableMap.copyOf( builder.propertyIndexConfigs );
    }

    public static Builder create()
    {
        return new Builder();
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

        final NodePropertyIndexConfig that = (NodePropertyIndexConfig) o;

        return Objects.equals( this.getAnalyzer(), that.getAnalyzer() ) &&
            Objects.equals( this.propertyIndexConfigs, that.propertyIndexConfigs );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( this.getAnalyzer(), this.propertyIndexConfigs );
    }

    public static class Builder
        extends NodeIndexConfig.Builder<Builder>
    {

        private final Map<DataPath, PropertyIndexConfig> propertyIndexConfigs = Maps.newHashMap();


        public Builder addPropertyIndexConfig( final Property property, final PropertyIndexConfig propertyIndexConfig )
        {
            propertyIndexConfigs.put( property.getPath().removeIndexFromLastElement(), propertyIndexConfig );
            return this;
        }

        public Builder addPropertyIndexConfig( final String path, final PropertyIndexConfig propertyIndexConfig )
        {
            propertyIndexConfigs.put( DataPath.from( path ), propertyIndexConfig );
            return this;
        }


        public NodePropertyIndexConfig build()
        {
            return new NodePropertyIndexConfig( this );
        }

    }


}
