package com.enonic.wem.core.entity;

import org.junit.Test;

import com.enonic.wem.api.entity.EntityPropertyIndexConfig;
import com.enonic.wem.api.entity.PropertyIndexConfig;
import com.enonic.wem.api.support.JsonTestHelper;

import static junit.framework.Assert.assertEquals;

public class EntityPropertyIndexConfigJsonTest
{
    private JsonTestHelper jsonTestHelper;

    public EntityPropertyIndexConfigJsonTest()
    {
        jsonTestHelper = new JsonTestHelper( this, true );
    }

    @Test
    public void deserialize_serialization_of_EntityIndexConfig_no_analyzer()
        throws Exception
    {
        EntityPropertyIndexConfig entityIndexConfig = EntityPropertyIndexConfig.newEntityIndexConfig().
            addPropertyIndexConfig( "test", PropertyIndexConfig.newPropertyIndexConfig().
                enabled( true ).
                fulltextEnabled( true ).
                nGramEnabled( true ).
                build() ).
            addPropertyIndexConfig( "test2", PropertyIndexConfig.newPropertyIndexConfig().
                enabled( false ).
                fulltextEnabled( false ).
                nGramEnabled( false ).
                build() ).
            build();

        EntityPropertyIndexConfigJson entityPropertyIndexConfigJson = new EntityPropertyIndexConfigJson( entityIndexConfig );

        // serialize from object
        String expectedSerialization = jsonTestHelper.objectToString( entityPropertyIndexConfigJson );

        System.out.println( expectedSerialization );

        // de-serialize
        EntityPropertyIndexConfigJson parsedData =
            jsonTestHelper.objectMapper().readValue( expectedSerialization, EntityPropertyIndexConfigJson.class );

        // serialize from json
        String serializationOfDeSerialization = jsonTestHelper.objectToString( parsedData );

        assertEquals( expectedSerialization, serializationOfDeSerialization );
    }

    @Test
    public void deserialize_serialization_of_EntityIndexConfig()
        throws Exception
    {
        EntityPropertyIndexConfig entityIndexConfig = EntityPropertyIndexConfig.newEntityIndexConfig().
            analyzer( "myAnalyzer" ).
            collection( "myCollection" ).
            addPropertyIndexConfig( "test", PropertyIndexConfig.newPropertyIndexConfig().
                enabled( true ).
                fulltextEnabled( true ).
                nGramEnabled( true ).
                build() ).
            addPropertyIndexConfig( "test2", PropertyIndexConfig.newPropertyIndexConfig().
                enabled( false ).
                fulltextEnabled( false ).
                nGramEnabled( false ).
                build() ).
            build();

        EntityPropertyIndexConfigJson entityPropertyIndexConfigJson = new EntityPropertyIndexConfigJson( entityIndexConfig );

        // serialize from object
        String expectedSerialization = jsonTestHelper.objectToString( entityPropertyIndexConfigJson );

        // de-serialize
        EntityPropertyIndexConfigJson parsedData =
            jsonTestHelper.objectMapper().readValue( expectedSerialization, EntityPropertyIndexConfigJson.class );

        // serialize from json
        String serializationOfDeSerialization = jsonTestHelper.objectToString( parsedData );

        assertEquals( expectedSerialization, serializationOfDeSerialization );
    }
}
