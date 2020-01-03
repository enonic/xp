package com.enonic.xp.repo.impl.dump.upgrade.htmlarea;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.data.Value;
import com.enonic.xp.dump.DumpUpgradeStepResult;
import com.enonic.xp.index.IndexConfig;
import com.enonic.xp.index.IndexValueProcessor;
import com.enonic.xp.index.PatternIndexConfigDocument;
import com.enonic.xp.node.NodeVersion;
import com.enonic.xp.util.Reference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HtmlAreaNodeDataUpgraderTest
{
    private HtmlAreaNodeDataUpgrader htmlAreaNodeDataUpgrader;

    @BeforeEach
    public void setup()
    {
        this.htmlAreaNodeDataUpgrader = new HtmlAreaNodeDataUpgrader();
    }

    @Test
    public void testUpgrade_nonContent()
    {
        final NodeVersion nodeVersion = NodeVersion.create().build();
        final DumpUpgradeStepResult.Builder result = DumpUpgradeStepResult.create();
        final boolean upgraded = htmlAreaNodeDataUpgrader.upgrade( nodeVersion, null, result );
        assertFalse( upgraded );
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

        final DumpUpgradeStepResult.Builder result = DumpUpgradeStepResult.create();
        final boolean upgraded = htmlAreaNodeDataUpgrader.upgrade( nodeVersion, indexConfigDocument, result );
        assertTrue( upgraded );

        final PropertyTree upgradedData = nodeVersion.getData();
        final Collection<Reference> upgradedProcessedReferences =
            (Collection<Reference>) upgradedData.getReferences( "processedReferences" );
        assertEquals( 5, upgradedProcessedReferences.size() );

        final List<Reference> expectedReferences = Arrays.stream(
            new String[]{"e1f57280-d672-4cd8-b674-98e26e5b69ae", "be1ca151-cf61-4a54-9ea4-c8d01ce83e0e",
                "81b1e3cd-575f-4565-a618-3c85d56224f6", "43d54e23-d8ce-4058-befb-777abe1a0d9f", "32169e70-49e1-444c-a6ac-d38f22438134"} ).
            map( Reference::from ).
            collect( Collectors.toList() );
        assertTrue( expectedReferences.containsAll( upgradedProcessedReferences ) );

        assertEquals( readTestResource( "htmlarea-expected.xml" ), upgradedData.getString( "data.htmlarea" ) );
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
        try (final InputStream stream = getClass().getResourceAsStream( resourcePrefix + resourceName ))
        {
            return new String( stream.readAllBytes(), StandardCharsets.UTF_8 );
        }
    }
}
