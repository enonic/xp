package com.enonic.xp.repo.impl.dump.upgrade.htmlarea;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.io.Resources;

import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.data.Value;
import com.enonic.xp.index.IndexConfig;
import com.enonic.xp.index.IndexValueProcessor;
import com.enonic.xp.index.PatternIndexConfigDocument;
import com.enonic.xp.node.NodeVersion;
import com.enonic.xp.util.Reference;

public class HtmlAreaNodeDataUpgraderTest
{
    private HtmlAreaNodeDataUpgrader htmlAreaNodeDataUpgrader;

    @Before
    public void setup()
    {
        this.htmlAreaNodeDataUpgrader = new HtmlAreaNodeDataUpgrader();
    }

    @Test
    public void testUpgrade_nonContent()
    {
        final NodeVersion nodeVersion = NodeVersion.create().build();
        final boolean upgraded = htmlAreaNodeDataUpgrader.upgrade( nodeVersion, null );
        Assert.assertFalse( upgraded );
    }

    @Test
    public void testUpgrade()
        throws IOException
    {
        final PropertyTree data = new PropertyTree();
        data.setString( "data.htmlarea", readTestResource( "htmlarea-source.xml" ) );
        final NodeVersion nodeVersion = NodeVersion.create().
            nodeType( ContentConstants.CONTENT_NODE_COLLECTION ).
            data( data ).
            build();
        final PatternIndexConfigDocument indexConfigDocument = createIndexConfigDocument();

        final boolean upgraded = htmlAreaNodeDataUpgrader.upgrade( nodeVersion, indexConfigDocument );
        Assert.assertTrue( upgraded );

        final PropertyTree upgradedData = nodeVersion.getData();
        final Collection<Reference> upgradedProcessedReferences =
            (Collection<Reference>) upgradedData.getReferences( "processedReferences" );
        Assert.assertEquals( 5, upgradedProcessedReferences.size() );

        final List<Reference> expectedReferences = Arrays.stream(
            new String[]{"e1f57280-d672-4cd8-b674-98e26e5b69ae", "be1ca151-cf61-4a54-9ea4-c8d01ce83e0e",
                "81b1e3cd-575f-4565-a618-3c85d56224f6", "43d54e23-d8ce-4058-befb-777abe1a0d9f", "32169e70-49e1-444c-a6ac-d38f22438134"} ).
            map( Reference::from ).
            collect( Collectors.toList() );
        Assert.assertTrue( expectedReferences.containsAll( upgradedProcessedReferences ) );

        Assert.assertEquals( readTestResource( "htmlarea-expected.xml" ), upgradedData.getString( "data.htmlarea" ) );
    }

    private PatternIndexConfigDocument createIndexConfigDocument()
    {
        return PatternIndexConfigDocument.
            create().
            add( "data.htmlarea", IndexConfig.create().
                addIndexValueProcessor( new IndexValueProcessor()
                {
                    @Override
                    public Value process( final Value value )
                    {
                        return null;
                    }

                    @Override
                    public String getName()
                    {
                        return "htmlStripper";
                    }
                } ).
                build() ).
            build();
    }

    private String readTestResource( final String resourceName )
        throws IOException
    {
        final String resourcePrefix = "/" + getClass().getPackage().getName().replace( '.', '/' ) + "/";
        final URL resource = getClass().getResource( resourcePrefix + resourceName );
        return Resources.toString( resource, StandardCharsets.UTF_8 );
    }
}
