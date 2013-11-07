package com.enonic.wem.core.hazelcast;

import java.io.InputStream;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Provider;

import com.google.inject.Singleton;
import com.hazelcast.config.Config;
import com.hazelcast.config.SerializerConfig;
import com.hazelcast.config.XmlConfigBuilder;
import com.hazelcast.nio.serialization.StreamSerializer;

@Singleton
final class HazelcastConfigurator
    implements Provider<Config>
{
    private final Config config;

    public HazelcastConfigurator()
    {
        final InputStream in = getClass().getResourceAsStream( "hazelcast.xml" );
        final XmlConfigBuilder configBuilder = new XmlConfigBuilder( in );
        this.config = configBuilder.build();
    }

    @Inject
    public void setSerializers( final Map<Class, StreamSerializer> serializers )
    {
        for ( final Map.Entry<Class, StreamSerializer> entry : serializers.entrySet() )
        {
            addSerializer( entry.getKey(), entry.getValue() );
        }
    }

    private void addSerializer( final Class type, final StreamSerializer serializer )
    {
        final SerializerConfig serializerConfig = new SerializerConfig();
        serializerConfig.setTypeClass( type );
        serializerConfig.setImplementation( serializer );
    }

    @Override
    public Config get()
    {
        return this.config;
    }
}
