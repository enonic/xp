package com.enonic.wem.api.entity;

import java.util.Map;
import java.util.Objects;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import com.enonic.wem.api.data.DataPath;
import com.enonic.wem.api.data.Property;

public class EntityPropertyIndexConfig
    extends EntityIndexConfig
{
    private final ImmutableMap<DataPath, PropertyIndexConfig> propertyIndexConfigs;

    public static Builder newEntityIndexConfig()
    {
        return new Builder();
    }

    private EntityPropertyIndexConfig( final Builder builder )
    {
        super( builder );
        this.propertyIndexConfigs = ImmutableMap.copyOf( builder.propertyIndexConfigs );
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

        final EntityPropertyIndexConfig that = (EntityPropertyIndexConfig) o;

        return Objects.equals( this.getAnalyzer(), that.getAnalyzer() ) &&
            Objects.equals( this.propertyIndexConfigs, that.propertyIndexConfigs );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( this.getAnalyzer(), this.propertyIndexConfigs );
    }

    public static class Builder
        extends EntityIndexConfig.Builder<Builder>
    {

        private final Map<DataPath, PropertyIndexConfig> propertyIndexConfigs = Maps.newHashMap();


        public Builder addPropertyIndexConfig( final Property property, final PropertyIndexConfig propertyIndexConfig )
        {
            propertyIndexConfigs.put( property.getBasePath(), propertyIndexConfig );
            return this;
        }

        public Builder addPropertyIndexConfig( final String path, final PropertyIndexConfig propertyIndexConfig )
        {
            propertyIndexConfigs.put( DataPath.from( path ), propertyIndexConfig );
            return this;
        }


        public EntityPropertyIndexConfig build()
        {
            return new EntityPropertyIndexConfig( this );
        }

    }


}
