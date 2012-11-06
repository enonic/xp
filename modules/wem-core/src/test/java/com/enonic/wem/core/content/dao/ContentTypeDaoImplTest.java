package com.enonic.wem.core.content.dao;


import javax.jcr.Node;

import org.junit.Test;

import com.enonic.wem.api.content.type.ContentType;
import com.enonic.wem.api.content.type.ContentTypeNames;
import com.enonic.wem.api.content.type.ContentTypes;
import com.enonic.wem.api.module.Module;
import com.enonic.wem.itest.AbstractJcrTest;

import static org.junit.Assert.*;

public class ContentTypeDaoImplTest
    extends AbstractJcrTest
{
    private ContentTypeDao contentTypeDao;

    public void setupDao()
        throws Exception
    {
        contentTypeDao = new ContentTypeDaoImpl();
    }

    @Test
    public void createContentType()
        throws Exception
    {
        // setup
        final ContentType contentType = new ContentType();
        contentType.setModule( new Module( "myModule" ) );
        contentType.setName( "myContentType" );
        contentType.setAbstract( false );
        contentType.setDisplayName( "My content type" );

        // exercise
        contentTypeDao.createContentType( session, contentType );
        commit();

        // verify
        Node contentNode = session.getNode( "/" + ContentDaoConstants.CONTENT_TYPES_PATH + "myModule/myContentType" );
        assertNotNull( contentNode );
    }

    @Test
    public void retrieveContentType()
        throws Exception
    {
        // setup
        final ContentType contentType = new ContentType();
        contentType.setModule( new Module( "myModule" ) );
        contentType.setName( "myContentType" );
        contentType.setAbstract( true );
        contentType.setDisplayName( "My content type" );
        contentTypeDao.createContentType( session, contentType );

        // exercise
        final ContentTypes contentTypes = contentTypeDao.retrieveContentTypes( session, ContentTypeNames.from( "myModule:myContentType" ) );
        commit();

        // verify
        assertNotNull( contentTypes );
        assertEquals( 1, contentTypes.getSize() );
        final ContentType contentType1 = contentTypes.getFirst();
        assertEquals( "myContentType", contentType1.getName() );
        assertEquals( "myModule", contentType1.getModule().getName() );
        assertEquals( true, contentType1.isAbstract() );
        assertEquals( "My content type", contentType1.getDisplayName() );
    }

    @Test
    public void updateContentType()
        throws Exception
    {
        // setup
        final ContentType contentType = new ContentType();
        contentType.setModule( new Module( "myModule" ) );
        contentType.setName( "myContentType" );
        contentType.setAbstract( true );
        contentType.setDisplayName( "My content type" );
        contentTypeDao.createContentType( session, contentType );

        // exercise
        final ContentTypes contentTypesAfterCreate =
            contentTypeDao.retrieveContentTypes( session, ContentTypeNames.from( "myModule:myContentType" ) );
        assertNotNull( contentTypesAfterCreate );
        assertEquals( 1, contentTypesAfterCreate.getSize() );

        contentType.setAbstract( false );
        contentType.setDisplayName( "My content type-UPDATED" );
        contentTypeDao.updateContentType( session, contentType );
        commit();

        // verify
        final ContentTypes contentTypesAfterUpdate =
            contentTypeDao.retrieveContentTypes( session, ContentTypeNames.from( "myModule:myContentType" ) );
        assertNotNull( contentTypesAfterUpdate );
        assertEquals( 1, contentTypesAfterUpdate.getSize() );
        final ContentType contentType1 = contentTypesAfterUpdate.getFirst();
        assertEquals( "myContentType", contentType1.getName() );
        assertEquals( "myModule", contentType1.getModule().getName() );
        assertEquals( false, contentType1.isAbstract() );
        assertEquals( "My content type-UPDATED", contentType1.getDisplayName() );
    }

    @Test
    public void deleteContentType()
        throws Exception
    {
        // setup
        final ContentType contentType = new ContentType();
        contentType.setModule( new Module( "myModule" ) );
        contentType.setName( "myContentType" );
        contentType.setAbstract( true );
        contentType.setDisplayName( "My content type" );
        contentTypeDao.createContentType( session, contentType );

        // exercise
        final ContentTypes contentTypesAfterCreate =
            contentTypeDao.retrieveContentTypes( session, ContentTypeNames.from( "myModule:myContentType" ) );
        assertNotNull( contentTypesAfterCreate );
        assertEquals( 1, contentTypesAfterCreate.getSize() );

        int deleted = contentTypeDao.deleteContentType( session, ContentTypeNames.from( contentType.getQualifiedName() ) );
        commit();

        // verify
        assertEquals( 1, deleted );
        final ContentTypes contentTypesAfterDelete =
            contentTypeDao.retrieveContentTypes( session, ContentTypeNames.from( "myModule:myContentType" ) );
        assertNotNull( contentTypesAfterDelete );
        assertTrue( contentTypesAfterDelete.isEmpty() );
    }

}
