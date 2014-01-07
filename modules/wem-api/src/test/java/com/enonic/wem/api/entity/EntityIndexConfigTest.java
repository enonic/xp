package com.enonic.wem.api.entity;

import java.util.Map;

import org.junit.Test;

import com.enonic.wem.api.data.DataPath;
import com.enonic.wem.api.data.Property;
import com.enonic.wem.api.data.Value;

import static org.junit.Assert.*;

public class EntityIndexConfigTest
{
    @Test
    public void analyzer()
        throws Exception
    {
        final EntityIndexConfig indexConfig = EntityIndexConfig.newEntityIndexConfig().analyzer( "myAnalyzer" ).build();
        assertEquals( "myAnalyzer", indexConfig.getAnalyzer() );
    }

    @Test
    public void addPropertyIndexConfig_given_property()
        throws Exception
    {
        final PropertyIndexConfig propertyIndexConfig =
            PropertyIndexConfig.newPropertyIndexConfig().enabled( true ).tokenizedEnabled( true ).fulltextEnabled( true ).build();

        final Property myProperty = new Property( "test", new Value.String( "testValue" ) );

        final EntityIndexConfig indexConfig =
            EntityIndexConfig.newEntityIndexConfig().addPropertyIndexConfig( myProperty, propertyIndexConfig ).build();

        final Map<DataPath, PropertyIndexConfig> propertyIndexConfigs = indexConfig.getPropertyIndexConfigs();

        final PropertyIndexConfig testPropertyIndexConfig = propertyIndexConfigs.get( DataPath.from( "test" ) );

        assertNotNull( testPropertyIndexConfig );
    }

    @Test
    public void addPropertyIndexConfig_given_path()
        throws Exception
    {
        final PropertyIndexConfig propertyIndexConfig =
            PropertyIndexConfig.newPropertyIndexConfig().enabled( true ).tokenizedEnabled( true ).fulltextEnabled( true ).build();

        final EntityIndexConfig indexConfig =
            EntityIndexConfig.newEntityIndexConfig().addPropertyIndexConfig( "test/path", propertyIndexConfig ).build();

        final Map<DataPath, PropertyIndexConfig> propertyIndexConfigs = indexConfig.getPropertyIndexConfigs();

        final PropertyIndexConfig testPropertyIndexConfig = propertyIndexConfigs.get( DataPath.from( "test/path" ) );

        assertNotNull( testPropertyIndexConfig );
    }

    @Test
    public void getPropertyIndexConfig()
        throws Exception
    {
        final PropertyIndexConfig propertyIndexConfig1 =
            PropertyIndexConfig.newPropertyIndexConfig().enabled( true ).tokenizedEnabled( true ).fulltextEnabled( true ).build();

        final PropertyIndexConfig propertyIndexConfig2 =
            PropertyIndexConfig.newPropertyIndexConfig().enabled( true ).tokenizedEnabled( true ).fulltextEnabled( true ).build();

        final PropertyIndexConfig propertyIndexConfig3 =
            PropertyIndexConfig.newPropertyIndexConfig().enabled( true ).tokenizedEnabled( true ).fulltextEnabled( true ).build();

        final PropertyIndexConfig propertyIndexConfig4 =
            PropertyIndexConfig.newPropertyIndexConfig().enabled( true ).tokenizedEnabled( true ).fulltextEnabled( true ).build();

        final EntityIndexConfig indexConfig = EntityIndexConfig.newEntityIndexConfig().
            addPropertyIndexConfig( "test", propertyIndexConfig1 ).
            addPropertyIndexConfig( "test/path", propertyIndexConfig2 ).
            addPropertyIndexConfig( "test/path/child", propertyIndexConfig3 ).
            addPropertyIndexConfig( "test/path/child/sub", propertyIndexConfig4 ).
            build();

        assertEquals( propertyIndexConfig1, indexConfig.getPropertyIndexConfig( DataPath.from( "test" ) ) );
        assertEquals( propertyIndexConfig2, indexConfig.getPropertyIndexConfig( DataPath.from( "test/path" ) ) );
        assertEquals( propertyIndexConfig3, indexConfig.getPropertyIndexConfig( DataPath.from( "test/path/child" ) ) );
        assertEquals( propertyIndexConfig4, indexConfig.getPropertyIndexConfig( DataPath.from( "test/path/child/sub" ) ) );
    }

    @Test
    public void array_values_gets_one_config_and_that_is_the_last_given()
    {
        Property myArray1 = new Property.String( "myArray", "1" );
        Property myArray2 = new Property.String( "myArray", "2" );

        final EntityIndexConfig indexConfig = EntityIndexConfig.
            newEntityIndexConfig().
            addPropertyIndexConfig( myArray1, PropertyIndexConfig.INDEXNON_PROPERTY_CONFIG ).
            addPropertyIndexConfig( myArray2, PropertyIndexConfig.INDEXALL_PROPERTY_CONFIG ).
            build();

        assertNotNull( indexConfig.getPropertyIndexConfig( myArray1.getPath() ) );
        assertEquals( PropertyIndexConfig.INDEXALL_PROPERTY_CONFIG, indexConfig.getPropertyIndexConfig( myArray1.getPath() ) );
        assertEquals( indexConfig.getPropertyIndexConfig( myArray1.getPath() ), indexConfig.getPropertyIndexConfig( myArray2.getPath() ) );
    }

}
