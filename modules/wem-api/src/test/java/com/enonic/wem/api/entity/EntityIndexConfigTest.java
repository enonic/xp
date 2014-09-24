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
        final PorpertyIndexConfigDocumentOldShit indexConfig = PorpertyIndexConfigDocumentOldShit.create().analyzer( "myAnalyzer" ).build();
        assertEquals( "myAnalyzer", indexConfig.getAnalyzer() );
    }

    @Test
    public void addPropertyIndexConfig_given_property()
        throws Exception
    {
        final PropertyIndexConfig propertyIndexConfig =
            PropertyIndexConfig.create().enabled( true ).nGramEnabled( true ).fulltextEnabled( true ).build();

        final Property myProperty = new Property( "test", Value.newString( "testValue" ) );

        final PorpertyIndexConfigDocumentOldShit indexConfig =
            PorpertyIndexConfigDocumentOldShit.create().addPropertyIndexConfig( myProperty, propertyIndexConfig ).build();

        final Map<DataPath, PropertyIndexConfig> propertyIndexConfigs = indexConfig.getPropertyIndexConfigs();

        final PropertyIndexConfig testPropertyIndexConfig = propertyIndexConfigs.get( DataPath.from( "test" ) );

        assertNotNull( testPropertyIndexConfig );
    }

    @Test
    public void addPropertyIndexConfig_given_path()
        throws Exception
    {
        final PropertyIndexConfig propertyIndexConfig =
            PropertyIndexConfig.create().enabled( true ).nGramEnabled( true ).fulltextEnabled( true ).build();

        final PorpertyIndexConfigDocumentOldShit indexConfig =
            PorpertyIndexConfigDocumentOldShit.create().addPropertyIndexConfig( "test/path", propertyIndexConfig ).build();

        final Map<DataPath, PropertyIndexConfig> propertyIndexConfigs = indexConfig.getPropertyIndexConfigs();

        final PropertyIndexConfig testPropertyIndexConfig = propertyIndexConfigs.get( DataPath.from( "test/path" ) );

        assertNotNull( testPropertyIndexConfig );
    }

    @Test
    public void getPropertyIndexConfig()
        throws Exception
    {
        final PropertyIndexConfig propertyIndexConfig1 =
            PropertyIndexConfig.create().enabled( true ).nGramEnabled( true ).fulltextEnabled( true ).build();

        final PropertyIndexConfig propertyIndexConfig2 =
            PropertyIndexConfig.create().enabled( true ).nGramEnabled( true ).fulltextEnabled( true ).build();

        final PropertyIndexConfig propertyIndexConfig3 =
            PropertyIndexConfig.create().enabled( true ).nGramEnabled( true ).fulltextEnabled( true ).build();

        final PropertyIndexConfig propertyIndexConfig4 =
            PropertyIndexConfig.create().enabled( true ).nGramEnabled( true ).fulltextEnabled( true ).build();

        final PorpertyIndexConfigDocumentOldShit indexConfig = PorpertyIndexConfigDocumentOldShit.create().
            addPropertyIndexConfig( "test", propertyIndexConfig1 ).
            addPropertyIndexConfig( "test/path", propertyIndexConfig2 ).
            addPropertyIndexConfig( "test/path/child", propertyIndexConfig3 ).
            addPropertyIndexConfig( "test/path/child/sub", propertyIndexConfig4 ).
            build();

        assertEquals( propertyIndexConfig1, indexConfig.getIndexConfig( DataPath.from( "test" ) ) );
        assertEquals( propertyIndexConfig2, indexConfig.getIndexConfig( DataPath.from( "test/path" ) ) );
        assertEquals( propertyIndexConfig3, indexConfig.getIndexConfig( DataPath.from( "test/path/child" ) ) );
        assertEquals( propertyIndexConfig4, indexConfig.getIndexConfig( DataPath.from( "test/path/child/sub" ) ) );
    }

    @Test
    public void array_values_gets_one_config_and_that_is_the_last_given()
    {
        Property myArray1 = Property.newString( "myArray", "1" );
        Property myArray2 = Property.newString( "myArray", "2" );

        final PorpertyIndexConfigDocumentOldShit indexConfig = PorpertyIndexConfigDocumentOldShit.
            create().
            addPropertyIndexConfig( myArray1, PropertyIndexConfig.SKIP ).
            addPropertyIndexConfig( myArray2, PropertyIndexConfig.FULL ).
            build();

        assertNotNull( indexConfig.getIndexConfig( myArray1.getPath() ) );
        assertEquals( PropertyIndexConfig.FULL, indexConfig.getIndexConfig( myArray1.getPath() ) );
        assertEquals( indexConfig.getIndexConfig( myArray1.getPath() ), indexConfig.getIndexConfig( myArray2.getPath() ) );
    }

}
