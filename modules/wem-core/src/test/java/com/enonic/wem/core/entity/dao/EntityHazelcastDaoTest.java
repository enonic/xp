package com.enonic.wem.core.entity.dao;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.hazelcast.config.Config;
import com.hazelcast.config.InterfacesConfig;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.config.MulticastConfig;
import com.hazelcast.config.NetworkConfig;
import com.hazelcast.config.SerializationConfig;
import com.hazelcast.config.SerializerConfig;
import com.hazelcast.config.TcpIpConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.entity.Entity;
import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.EntityIndexConfig;
import com.enonic.wem.api.entity.NoEntityWithIdFound;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;


public class EntityHazelcastDaoTest
{
    private HazelcastInstance hazelcastInstance;

    private EntityHazelcastDao dao;

    @Before
    public void before()
    {
        Config config = new Config();
        SerializationConfig serializationConfig = config.getSerializationConfig();

        final SerializerConfig entitySerializer = new SerializerConfig().
            setImplementation( new EntityStreamSerializer() ).
            setTypeClass( Entity.class );

        final SerializerConfig entityIdSerializer = new SerializerConfig().
            setImplementation( new EntityIdStreamSerializer() ).
            setTypeClass( EntityId.class );

        serializationConfig.addSerializerConfig( entityIdSerializer );
        serializationConfig.addSerializerConfig( entitySerializer );

        NetworkConfig networkConfig = config.getNetworkConfig();
        networkConfig.setInterfaces( new InterfacesConfig().setEnabled( false ) );
        JoinConfig networkJoinConfig = new JoinConfig();
        networkJoinConfig.setTcpIpConfig( new TcpIpConfig().setEnabled( false ) );
        networkJoinConfig.setMulticastConfig( new MulticastConfig().setEnabled( false ) );
        networkConfig.setJoin( networkJoinConfig );
        config.getServicesConfig().setEnableDefaults( true );

        hazelcastInstance = Hazelcast.newHazelcastInstance( config );
        dao = new EntityHazelcastDao( hazelcastInstance );
    }

    @After
    public void after()
    {
        hazelcastInstance.shutdown();
    }

    @Test
    public void create()
    {
        RootDataSet data = new RootDataSet();
        data.setProperty( "myProperty", new Value.String( "A" ) );

        EntityDao.CreateEntityArgs args = new EntityDao.CreateEntityArgs.Builder().
            data( data ).
            entityIndexConfig( EntityIndexConfig.newEntityIndexConfig().build() ).
            build();

        // exercise
        Entity createdEntity = dao.create( args );

        // verify
        assertNotNull( createdEntity.id() );
        assertNotNull( createdEntity.getCreatedTime() );
        assertNull( createdEntity.getModifiedTime() );
        assertEquals( "A", createdEntity.property( "myProperty" ).getString() );
    }

    @Test
    public void getById()
        throws InterruptedException
    {
        RootDataSet data = new RootDataSet();
        data.setProperty( "myProperty", new Value.String( "A" ) );

        EntityDao.CreateEntityArgs args = new EntityDao.CreateEntityArgs.Builder().
            data( data ).
            entityIndexConfig( EntityIndexConfig.newEntityIndexConfig().build() ).
            build();

        EntityId id = dao.create( args ).id();

        // exercise
        Entity entity = dao.getById( id );

        // verify
        assertNotNull( entity.id() );
        assertNotNull( entity.getCreatedTime() );
        assertNull( entity.getModifiedTime() );
        assertEquals( "A", entity.property( "myProperty" ).getString() );
    }

    @Test(expected = NoEntityWithIdFound.class)
    public void deleteById()
    {
        RootDataSet data = new RootDataSet();
        data.setProperty( "myProperty", new Value.String( "A" ) );

        EntityDao.CreateEntityArgs args = new EntityDao.CreateEntityArgs.Builder().
            data( data ).
            entityIndexConfig( EntityIndexConfig.newEntityIndexConfig().build() ).
            build();

        EntityId id = dao.create( args ).id();

        // exercise
        dao.deleteById( id );

        // verify
        dao.getById( id );
    }
}
