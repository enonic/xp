package com.enonic.xp.app;


import org.junit.Test;

import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.mixin.MixinName;
import com.enonic.xp.schema.relationship.RelationshipTypeName;

import static org.junit.Assert.*;

public class ApplicationRelativeResolverTest
{
    @Test
    public void toContentTypeName()
    {
        final ApplicationRelativeResolver resolver = new ApplicationRelativeResolver( ApplicationKey.from( "aaa" ) );
        ContentTypeName contentTypeName = resolver.toContentTypeName( "bbb" );
        assertEquals( contentTypeName.getLocalName(), "bbb" );

        contentTypeName = resolver.toContentTypeName( "ccc:ddd" );
        assertEquals( contentTypeName.getLocalName(), "ddd" );
    }

    @Test(expected = IllegalArgumentException.class)
    public void toContentTypeNameEmpty()
    {
        final ApplicationRelativeResolver resolver = new ApplicationRelativeResolver( null );
        resolver.toContentTypeName( "aaa" );
    }

    @Test
    public void toMixinName()
    {
        final ApplicationRelativeResolver resolver = new ApplicationRelativeResolver( ApplicationKey.from( "aaa" ) );
        MixinName mixinName = resolver.toMixinName( "bbb" );
        assertEquals( mixinName.getLocalName(), "bbb" );

        mixinName = resolver.toMixinName( "ccc:ddd" );
        assertEquals( mixinName.getLocalName(), "ddd" );
    }

    @Test(expected = IllegalArgumentException.class)
    public void toMixinNameEmpty()
    {
        final ApplicationRelativeResolver resolver = new ApplicationRelativeResolver( null );
        resolver.toMixinName( "aaa" );
    }

    @Test
    public void toRelationshipTypeName()
    {
        final ApplicationRelativeResolver resolver = new ApplicationRelativeResolver( ApplicationKey.from( "aaa" ) );
        RelationshipTypeName relationshipTypeName = resolver.toRelationshipTypeName( "bbb" );
        assertEquals( relationshipTypeName.getLocalName(), "bbb" );

        relationshipTypeName = resolver.toRelationshipTypeName( "ccc:ddd" );
        assertEquals( relationshipTypeName.getLocalName(), "ddd" );
    }

    @Test(expected = IllegalArgumentException.class)
    public void toRelationshipTypeNameEmpty()
    {
        final ApplicationRelativeResolver resolver = new ApplicationRelativeResolver( null );
        resolver.toRelationshipTypeName( "aaa" );
    }
}
