package com.enonic.wem.api.content.relationship.editor;

import java.util.Map;

import org.junit.Test;

import com.google.common.collect.Maps;

import com.enonic.wem.api.content.relationship.Relationship;

import static junit.framework.Assert.assertEquals;

public class AddRelationshipPropertiesEditorTest
{
    @Test
    public void edit()
        throws Exception
    {
        Map<String, String> addProperties = Maps.newLinkedHashMap();
        addProperties.put( "prop0", "v0" );
        addProperties.put( "prop1", "changed" );
        addProperties.put( "prop4", "v4" );
        AddRelationshipPropertiesEditor editor = new AddRelationshipPropertiesEditor( addProperties );

        Relationship relationship = Relationship.newRelationship().
            property( "prop1", "v1" ).
            property( "prop2", "v2" ).
            property( "prop3", "v3" ).
            build();

        // exercise
        Relationship changed = editor.edit( relationship );

        // verify
        assertEquals( 5, changed.getProperties().size() );
        assertEquals( "v0", changed.getProperties().get( "prop0" ) );
        assertEquals( "changed", changed.getProperties().get( "prop1" ) );
        assertEquals( "v2", changed.getProperties().get( "prop2" ) );
        assertEquals( "v3", changed.getProperties().get( "prop3" ) );
        assertEquals( "v4", changed.getProperties().get( "prop4" ) );
    }
}
