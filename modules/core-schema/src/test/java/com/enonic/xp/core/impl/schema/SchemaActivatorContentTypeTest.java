package com.enonic.xp.core.impl.schema;

import java.util.List;

import com.enonic.xp.core.schema.content.ContentTypeProvider;

import static org.junit.Assert.*;

public class SchemaActivatorContentTypeTest
    extends AbstractSchemaActivatorTest
{
    @Override
    protected void validateProviders()
        throws Exception
    {
        final List<ContentTypeProvider> list1 = getServices( null, ContentTypeProvider.class );
        assertEquals( 1, list1.size() );

        final List<ContentTypeProvider> list2 = getServices( "module1", ContentTypeProvider.class );
        assertEquals( 1, list2.size() );

        final List<ContentTypeProvider> list3 = getServices( "module2", ContentTypeProvider.class );
        assertEquals( 0, list3.size() );

        final List<ContentTypeProvider> list4 = getServices( "not-module", ContentTypeProvider.class );
        assertEquals( 0, list4.size() );
    }

    @Override
    protected void validateNoProviders()
        throws Exception
    {
        final List<ContentTypeProvider> list = getServices( null, ContentTypeProvider.class );
        assertEquals( 0, list.size() );
    }
}
