package com.enonic.xp.module;


import org.junit.Test;

import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.mixin.MixinName;
import com.enonic.xp.schema.relationship.RelationshipTypeName;

import static org.junit.Assert.*;

public class ModuleRelativeResolverTest
{
    @Test
    public void toContentTypeName()
    {
        final ModuleRelativeResolver resolver = new ModuleRelativeResolver( ModuleKey.from( "aaa" ) );
        ContentTypeName contentTypeName = resolver.toContentTypeName( "bbb" );
        assertEquals( contentTypeName.getLocalName(), "bbb" );

        contentTypeName = resolver.toContentTypeName( "ccc:ddd" );
        assertEquals( contentTypeName.getLocalName(), "ddd" );
    }

    @Test(expected = IllegalArgumentException.class)
    public void toContentTypeNameEmpty()
    {
        final ModuleRelativeResolver resolver = new ModuleRelativeResolver( null );
        resolver.toContentTypeName( "aaa" );
    }

    @Test
    public void toMixinName()
    {
        final ModuleRelativeResolver resolver = new ModuleRelativeResolver( ModuleKey.from( "aaa" ) );
        MixinName mixinName = resolver.toMixinName( "bbb" );
        assertEquals( mixinName.getLocalName(), "bbb" );

        mixinName = resolver.toMixinName( "ccc:ddd" );
        assertEquals( mixinName.getLocalName(), "ddd" );
    }

    @Test(expected = IllegalArgumentException.class)
    public void toMixinNameEmpty()
    {
        final ModuleRelativeResolver resolver = new ModuleRelativeResolver( null );
        resolver.toMixinName( "aaa" );
    }

    @Test
    public void toRelationshipTypeName()
    {
        final ModuleRelativeResolver resolver = new ModuleRelativeResolver( ModuleKey.from( "aaa" ) );
        RelationshipTypeName relationshipTypeName = resolver.toRelationshipTypeName( "bbb" );
        assertEquals( relationshipTypeName.getLocalName(), "bbb" );

        relationshipTypeName = resolver.toRelationshipTypeName( "ccc:ddd" );
        assertEquals( relationshipTypeName.getLocalName(), "ddd" );
    }

    @Test(expected = IllegalArgumentException.class)
    public void toRelationshipTypeNameEmpty()
    {
        final ModuleRelativeResolver resolver = new ModuleRelativeResolver( null );
        resolver.toRelationshipTypeName( "aaa" );
    }
}
