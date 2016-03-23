package com.enonic.xp.core.impl.schema.content;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.core.impl.schema.AbstractSchemaTest;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypes;
import com.enonic.xp.schema.content.GetAllContentTypesParams;
import com.enonic.xp.schema.content.GetContentTypeParams;
import com.enonic.xp.schema.mixin.MixinService;

import static org.junit.Assert.*;

public class ContentTypeServiceTest
    extends AbstractSchemaTest
{
    protected ContentTypeServiceImpl service;

    protected MixinService mixinService;

    @Override
    protected void initialize()
        throws Exception
    {
        this.mixinService = Mockito.mock( MixinService.class );
        this.service = new ContentTypeServiceImpl();
        this.service.setMixinService( this.mixinService );
        this.service.setApplicationService( this.applicationService );
        this.service.setResourceService( this.resourceService );
    }

    @Test
    public void testEmpty()
    {
        final ContentTypes types1 = this.service.getAll( new GetAllContentTypesParams() );
        assertNotNull( types1 );
        assertEquals( 22, types1.getSize() );

        final ContentTypes types2 = this.service.getByApplication( ApplicationKey.from( "other" ) );
        assertNotNull( types2 );
        assertEquals( 0, types2.getSize() );

        final ContentType contentType = service.getByName( new GetContentTypeParams().contentTypeName( "other:mytype" ) );
        assertEquals( null, contentType );
    }

    @Test
    public void testApplications()
        throws Exception
    {
        initializeApps();

        final ContentTypes types1 = this.service.getAll( new GetAllContentTypesParams() );
        assertNotNull( types1 );
        assertEquals( 24, types1.getSize() );

        final ContentTypes types2 = this.service.getByApplication( ApplicationKey.from( "myapp1" ) );
        assertNotNull( types2 );
        assertEquals( 1, types2.getSize() );

        final ContentTypes types3 = this.service.getByApplication( ApplicationKey.from( "myapp2" ) );
        assertNotNull( types3 );
        assertEquals( 1, types3.getSize() );

        final ContentType contentType = service.getByName( new GetContentTypeParams().contentTypeName( "myapp1:tag" ) );
        assertNotNull( contentType );
    }

    @Test
    public void testSystemApplication()
    {
        ContentTypes contentTypes = this.service.getAll( new GetAllContentTypesParams() );
        assertNotNull( contentTypes );
        assertEquals( 22, contentTypes.getSize() );

        ContentType contentType = service.getByName( new GetContentTypeParams().contentTypeName( ContentTypeName.folder() ) );
        assertNotNull( contentType );

        contentTypes = service.getByApplication( ApplicationKey.BASE );
        assertNotNull( contentTypes );
        assertEquals( contentTypes.getSize(), 5 );

        contentTypes = service.getByApplication( ApplicationKey.PORTAL );
        assertNotNull( contentTypes );
        assertEquals( contentTypes.getSize(), 4 );

        contentTypes = service.getByApplication( ApplicationKey.MEDIA_MOD );
        assertNotNull( contentTypes );
        assertEquals( contentTypes.getSize(), 13 );

        contentType = service.getByName( new GetContentTypeParams().contentTypeName( ContentTypeName.site() ) );
        assertNotNull( contentType );
    }
}
