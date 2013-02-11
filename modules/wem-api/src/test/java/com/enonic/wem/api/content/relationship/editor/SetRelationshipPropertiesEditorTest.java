package com.enonic.wem.api.content.relationship.editor;


import java.util.Map;

import org.junit.Test;

import com.google.common.collect.ImmutableMap;

import com.enonic.wem.api.content.relationship.Relationship;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;

public class SetRelationshipPropertiesEditorTest
{
    @Test
    public void edit()
        throws Exception
    {
        // setup
        Relationship relationship = Relationship.newRelationship().
            property( "a", "a1" ).
            property( "b", "b2" ).
            property( "c", "3" ).
            build();

        ImmutableMap.Builder<String, String> newProperties = ImmutableMap.builder();
        newProperties.put( "a", "1" );
        newProperties.put( "b", "2" );
        SetRelationshipPropertiesEditor editor = new SetRelationshipPropertiesEditor( newProperties.build() );

        // exercise
        Relationship changed = editor.edit( relationship );

        // verify
        Map<String, String> changedProperties = changed.getProperties();
        assertEquals( 2, changedProperties.size() );
        assertEquals( "1", changedProperties.get( "a" ) );
        assertEquals( "2", changedProperties.get( "b" ) );
        assertFalse( changedProperties.containsKey( "c" ) );
    }
}
