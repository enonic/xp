package com.enonic.xp.repo.impl.dump.upgrade;

import java.net.URL;

import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.json.JsonToPropertyTreeTranslator;
import com.enonic.xp.repo.impl.dump.upgrade.flattenedpage.FlattenedPageDataUpgrader;

import static org.junit.Assert.*;

public class FlattenedPageDataUpgraderTest
{
    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    public void testPageFlattened()
        throws Exception
    {
        final JsonNode oldPageComponents = loadJson( "old-page-components" );
        final JsonNode newPageComponents = loadJson( "new-page-components" );

        final PropertyTree oldData = new JsonToPropertyTreeTranslator().translate( oldPageComponents );
        final PropertyTree newData = new JsonToPropertyTreeTranslator().translate( newPageComponents );

        new FlattenedPageDataUpgrader().upgrade( oldData );

        // using string comparison because PropertyTree entries are same but might have different value type
        assertEquals( oldData.toString(), newData.toString() );
    }

    @Test
    public void testFragmentFlattened()
        throws Exception
    {
        final JsonNode oldPageComponents = loadJson( "old-fragment-components" );
        final JsonNode newPageComponents = loadJson( "new-fragment-components" );

        final PropertyTree oldData = new JsonToPropertyTreeTranslator().translate( oldPageComponents );
        final PropertyTree newData = new JsonToPropertyTreeTranslator().translate( newPageComponents );

        new FlattenedPageDataUpgrader().upgrade( oldData );

        // using string comparison because PropertyTree entries are same but might have different value type
        assertEquals( oldData.toString(), newData.toString() );
    }

    private JsonNode loadJson( final String name )
        throws Exception
    {
        final String resource = "/" + getClass().getName().replace( '.', '/' ) + "-" + name + ".json";
        final URL url = getClass().getResource( resource );

        assertNotNull( "File [" + resource + "] not found", url );
        return this.mapper.readTree( url );
    }
}
