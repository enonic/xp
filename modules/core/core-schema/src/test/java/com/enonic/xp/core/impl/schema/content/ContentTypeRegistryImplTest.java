package com.enonic.xp.core.impl.schema.content;

import org.junit.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.core.impl.schema.AbstractSchemaTest;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypes;

import static org.junit.Assert.*;

public class ContentTypeRegistryImplTest
    extends AbstractSchemaTest
{
    protected ContentTypeRegistryImpl service;

    @Override
    protected void initialize()
        throws Exception
    {
        this.service = new ContentTypeRegistryImpl();
        this.service.setResourceService( this.resourceService );
        this.service.setApplicationService( this.applicationService );
    }

    @Test
    public void testEmpty()
    {
        addApplications();

        final ContentTypes types1 = this.service.getAll();
        assertNotNull( types1 );
        assertEquals( 21, types1.getSize() );

        final ContentTypes types2 = this.service.getByApplication( ApplicationKey.from( "other" ) );
        assertNotNull( types2 );
        assertEquals( 0, types2.getSize() );

        final ContentType contentType = service.get( ContentTypeName.from( "other:mytype" ) );
        assertEquals( null, contentType );
    }

    @Test
    public void testApplications()
    {
        addApplications( "application1", "application2" );

        final ContentTypes types1 = this.service.getAll();
        assertNotNull( types1 );
        assertEquals( 23, types1.getSize() );

        final ContentTypes types2 = this.service.getByApplication( ApplicationKey.from( "application1" ) );
        assertNotNull( types2 );
        assertEquals( 1, types2.getSize() );

        this.service.invalidate( ApplicationKey.from( "application2" ) );

        final ContentTypes types3 = this.service.getByApplication( ApplicationKey.from( "application2" ) );
        assertNotNull( types3 );
        assertEquals( 1, types3.getSize() );

        final ContentType contentType = service.get( ContentTypeName.from( "application1:tag" ) );
        assertNotNull( contentType );
    }

    @Test
    public void testSystemApplication()
    {
        addApplications();

        ContentTypes contentTypes = this.service.getAll();
        assertNotNull( contentTypes );
        assertEquals( 21, contentTypes.getSize() );

        ContentType contentType = service.get( ContentTypeName.folder() );
        assertNotNull( contentType );

        contentTypes = service.getByApplication( ApplicationKey.BASE );
        assertNotNull( contentTypes );
        assertEquals( contentTypes.getSize(), 5 );

        contentTypes = service.getByApplication( ApplicationKey.PORTAL );
        assertNotNull( contentTypes );
        assertEquals( contentTypes.getSize(), 3 );

        contentTypes = service.getByApplication( ApplicationKey.MEDIA_MOD );
        assertNotNull( contentTypes );
        assertEquals( contentTypes.getSize(), 13 );

        contentType = service.get( ContentTypeName.site() );
        assertNotNull( contentType );
    }
}
