package com.enonic.xp.repo.impl.dump.upgrade;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;

import com.enonic.xp.data.PropertyPath;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.index.PatternIndexConfigDocument;
import com.enonic.xp.json.JsonToPropertyTreeTranslator;
import com.enonic.xp.json.ObjectMapperHelper;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.repo.impl.dump.upgrade.flattenedpage.FlattenedPageIndexUpgrader;
import com.enonic.xp.repo.impl.node.json.IndexConfigDocumentJson;

import static org.junit.Assert.*;

public class FlattenedPageIndexUpgraderTest
{
    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    public void testPageFlattened()
        throws Exception
    {
        test( "page-components", "old-page-components-index", "new-page-components-index" );
    }

    @Test
    public void testPageFlattenedWithoutRegions()
        throws Exception
    {
        test( "page-components-without-regions", "old-page-components-index", "new-page-components-without-regions-index" );
    }

    @Test
    public void testPageFlattenedWithoutPage()
        throws Exception
    {
        test( "page-components-without-page", "old-page-components-index", "new-page-components-without-page-index" );
    }

    private void test( final String dataFile, final String oldIndexDocumentFile, final String newIndexDocumentFile )
        throws Exception
    {
        final JsonNode pageComponents = loadJson( dataFile );

        final PropertyTree data = new JsonToPropertyTreeTranslator().translate( pageComponents );

        final List<PropertySet> components = Lists.newArrayList( data.getSets( "components" ) );

        final String descriptorKeyStr = data.getString( PropertyPath.from( "components.page.descriptor" ) );

        final DescriptorKey descriptorKey = descriptorKeyStr != null ? DescriptorKey.from( descriptorKeyStr ) : null;

        final PatternIndexConfigDocument oldDocument = getIndexConfigDocument( oldIndexDocumentFile );

        final PatternIndexConfigDocument newDocument = new FlattenedPageIndexUpgrader( descriptorKey, components ).
            upgrade( oldDocument );

        assertEquals( getIndexConfigDocument( newIndexDocumentFile ), newDocument );
    }

    private PatternIndexConfigDocument getIndexConfigDocument( final String name )
        throws Exception
    {
        final JsonNode jsonNode = loadJson( name );
        try
        {
            final IndexConfigDocumentJson indexConfigDocumentJson =
                ObjectMapperHelper.create().readValue( jsonNode.toString(), IndexConfigDocumentJson.class );
            return indexConfigDocumentJson.fromJson();
        }
        catch ( IOException e )
        {
            throw new DumpUpgradeException( "Cannot read index config [" + name + "]", e );
        }
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
