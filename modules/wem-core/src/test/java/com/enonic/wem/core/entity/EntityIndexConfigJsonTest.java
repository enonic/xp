package com.enonic.wem.core.entity;

import org.junit.Test;

import com.enonic.wem.api.entity.EntityIndexConfig;
import com.enonic.wem.api.entity.PropertyIndexConfig;
import com.enonic.wem.api.support.JsonTestHelper;

import static junit.framework.Assert.assertEquals;

public class EntityIndexConfigJsonTest
{
    private JsonTestHelper jsonTestHelper;

    public EntityIndexConfigJsonTest()
    {
        jsonTestHelper = new JsonTestHelper( this, true );
    }

    @Test
    public void deserialize_serialization_of_EntityIndexConfig_no_analyzer()
        throws Exception
    {
        EntityIndexConfig entityIndexConfig = EntityIndexConfig.newEntityIndexConfig().
            addPropertyIndexConfig( "test", PropertyIndexConfig.newPropertyIndexConfig().
                enabled( true ).
                fulltextEnabled( true ).
                tokenizedEnabled( true ).
                build() ).
            addPropertyIndexConfig( "test2", PropertyIndexConfig.newPropertyIndexConfig().
                enabled( false ).
                fulltextEnabled( false ).
                tokenizedEnabled( false ).
                build() ).
            build();

        EntityIndexConfigJson entityIndexConfigJson = new EntityIndexConfigJson( entityIndexConfig );

        // serialize from object
        String expectedSerialization = jsonTestHelper.objectToString( entityIndexConfigJson );

        System.out.println( expectedSerialization );

        // de-serialize
        EntityIndexConfigJson parsedData = jsonTestHelper.objectMapper().readValue( expectedSerialization, EntityIndexConfigJson.class );

        // serialize from json
        String serializationOfDeSerialization = jsonTestHelper.objectToString( parsedData );

        assertEquals( expectedSerialization, serializationOfDeSerialization );
    }

    @Test
    public void deserialize_serialization_of_EntityIndexConfig()
        throws Exception
    {
        EntityIndexConfig entityIndexConfig = EntityIndexConfig.newEntityIndexConfig().
            analyzer( "myAnalyzer" ).
            collection( "myCollection" ).
            addPropertyIndexConfig( "test", PropertyIndexConfig.newPropertyIndexConfig().
                enabled( true ).
                fulltextEnabled( true ).
                tokenizedEnabled( true ).
                build() ).
            addPropertyIndexConfig( "test2", PropertyIndexConfig.newPropertyIndexConfig().
                enabled( false ).
                fulltextEnabled( false ).
                tokenizedEnabled( false ).
                build() ).
            build();

        EntityIndexConfigJson entityIndexConfigJson = new EntityIndexConfigJson( entityIndexConfig );

        // serialize from object
        String expectedSerialization = jsonTestHelper.objectToString( entityIndexConfigJson );

        // de-serialize
        EntityIndexConfigJson parsedData = jsonTestHelper.objectMapper().readValue( expectedSerialization, EntityIndexConfigJson.class );

        // serialize from json
        String serializationOfDeSerialization = jsonTestHelper.objectToString( parsedData );

        assertEquals( expectedSerialization, serializationOfDeSerialization );
    }
}
