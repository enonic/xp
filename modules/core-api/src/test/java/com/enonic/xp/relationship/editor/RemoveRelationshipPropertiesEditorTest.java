package com.enonic.xp.relationship.editor;


import java.util.ArrayList;

import org.junit.Test;

import com.google.common.collect.Lists;

import com.enonic.xp.relationship.Relationship;

import static org.junit.Assert.*;

public class RemoveRelationshipPropertiesEditorTest
{
    @Test
    public void edit()
        throws Exception
    {
        // setup
        Relationship relationship = Relationship.create().
            property( "prop1", "v1" ).
            property( "prop2", "v2" ).
            property( "prop3", "v3" ).
            build();

        ArrayList<String> propertiesToRemove = Lists.newArrayList( "prop2" );
        RemoveRelationshipPropertiesEditor editor = new RemoveRelationshipPropertiesEditor( propertiesToRemove );

        // exercise
        Relationship changed = editor.edit( relationship );

        // verify
        assertEquals( 2, changed.getProperties().size() );
        assertEquals( true, changed.getProperties().containsKey( "prop1" ) );
        assertEquals( false, changed.getProperties().containsKey( "prop2" ) );
        assertEquals( true, changed.getProperties().containsKey( "prop3" ) );
    }
}
