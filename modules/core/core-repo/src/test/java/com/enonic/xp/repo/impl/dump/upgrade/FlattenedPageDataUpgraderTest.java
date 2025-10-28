package com.enonic.xp.repo.impl.dump.upgrade;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.repo.impl.dump.upgrade.flattenedpage.FlattenedPageDataUpgrader;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class FlattenedPageDataUpgraderTest
{
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    void testPageFlattened()
        throws Exception
    {
        test( "old-page-components", "new-page-components" );
    }

    @Test
    void testFragmentFlattened()
        throws Exception
    {
        test( "old-fragment-components", "new-fragment-components" );
    }

    @Test
    void testTemplateReference()
        throws Exception
    {
        test( "old-templateref", "new-templateref" );
    }

    @Test
    void testTemplateReferenceWithComponents()
        throws Exception
    {
        test( "old-templateref-with-components", "new-templateref-with-components" );
    }

    private void test( final String oldJsonFile, final String newJsonFile )
        throws Exception
    {
        final JsonNode oldPageComponents = loadJson( oldJsonFile );
        final JsonNode newPageComponents = loadJson( newJsonFile );

        final PropertyTree oldData = PropertyTree.fromMap( MAPPER.convertValue( oldPageComponents, Map.class ) );
        final PropertyTree newData = PropertyTree.fromMap( MAPPER.convertValue( newPageComponents, Map.class ) );

        final HashMap<String, String> templateControllerMap = new HashMap<>();
        templateControllerMap.put( "templateId", "com.enonic.app.features:main" );
        FlattenedPageDataUpgrader.create().
            templateControllerMap( templateControllerMap ).
            build().
            upgrade( oldData );

        // using string comparison because PropertyTree entries are same but might have different value type
        assertEquals( newData.toString(), oldData.toString() );
    }

    private JsonNode loadJson( final String name )
        throws Exception
    {
        final String resource = "/" + getClass().getName().replace( '.', '/' ) + "-" + name + ".json";
        final URL url = getClass().getResource( resource );

        assertNotNull( url, "File [" + resource + "] not found" );
        return MAPPER.readTree( url );
    }
}
