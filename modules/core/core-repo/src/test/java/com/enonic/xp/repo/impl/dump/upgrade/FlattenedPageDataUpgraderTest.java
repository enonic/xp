package com.enonic.xp.repo.impl.dump.upgrade;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.repo.impl.dump.upgrade.flattenedpage.FlattenedPageDataUpgrader;
import com.enonic.xp.support.JsonTestHelper;
import com.enonic.xp.util.JsonHelper;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FlattenedPageDataUpgraderTest
{
    @Test
    public void testPageFlattened()
        throws Exception
    {
        test( "old-page-components", "new-page-components" );
    }

    @Test
    public void testFragmentFlattened()
        throws Exception
    {
        test( "old-fragment-components", "new-fragment-components" );
    }

    @Test
    public void testTemplateReference()
        throws Exception
    {
        test( "old-templateref", "new-templateref" );
    }

    @Test
    public void testTemplateReferenceWithComponents()
        throws Exception
    {
        test( "old-templateref-with-components", "new-templateref-with-components" );
    }

    private void test( final String oldJsonFile, final String newJsonFile )
        throws Exception
    {
        final JsonNode oldPageComponents = loadJson( oldJsonFile );
        final JsonNode newPageComponents = loadJson( newJsonFile );

        final PropertyTree oldData = PropertyTree.fromMap(  JsonHelper.toMap( oldPageComponents ) );
        final PropertyTree newData = PropertyTree.fromMap( JsonHelper.toMap( newPageComponents )  );

        final Map<String, String> templateControllerMap = new HashMap<>();
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
        return JsonTestHelper.loadJson( getClass(), name );
    }
}
