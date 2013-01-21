package com.enonic.wem.core.content.type.dao;


import javax.jcr.Node;

import org.junit.Test;

import com.enonic.wem.api.content.type.ContentType;
import com.enonic.wem.api.content.type.ContentTypes;
import com.enonic.wem.api.content.type.QualifiedContentTypeName;
import com.enonic.wem.api.content.type.QualifiedContentTypeNames;
import com.enonic.wem.api.content.type.form.FormItemSet;
import com.enonic.wem.api.content.type.form.inputtype.InputTypes;
import com.enonic.wem.api.module.ModuleName;
import com.enonic.wem.core.AbstractJcrTest;
import com.enonic.wem.core.jcr.JcrHelper;

import static com.enonic.wem.api.content.type.ContentType.newContentType;
import static com.enonic.wem.api.content.type.form.FormItemSet.newFormItemSet;
import static com.enonic.wem.api.content.type.form.Input.newInput;
import static org.junit.Assert.*;

public class ContentTypeDaoImplTest
    extends AbstractJcrTest
{
    private static final byte[] SINGLE_PIXEL_GIF_PICTURE =
        {0x47, 0x49, 0x46, 0x38, 0x39, 0x61, 0x1, 0x0, 0x1, 0x0, (byte) 0x80, 0x0, 0x0, (byte) 0xff, (byte) 0xff, (byte) 0xff, 0x0, 0x0,
            0x0, 0x2c, 0x0, 0x0, 0x0, 0x0, 0x1, 0x0, 0x1, 0x0, 0x0, 0x2, 0x2, 0x44, 0x1, 0x0, 0x3b};

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
        final ContentType.Builder contentTypeBuilder = newContentType().
            module( ModuleName.from( "myModule" ) ).
            name( "myContentType" ).
            setAbstract( false ).
            displayName( "My content type" ).
            icon( SINGLE_PIXEL_GIF_PICTURE );

        final ContentType contentType = addContentTypeFormItems( contentTypeBuilder );

        // exercise
        contentTypeDao.createContentType( contentType, session );
        commit();

        // verify
        Node contentNode = session.getNode( "/" + ContentTypeDao.CONTENT_TYPES_PATH + "myModule/myContentType" );
        assertNotNull( contentNode );
        assertArrayEquals( SINGLE_PIXEL_GIF_PICTURE, JcrHelper.getPropertyBinary( contentNode, ContentTypeJcrMapper.ICON ) );
    }

    @Test
    public void retrieveContentType()
        throws Exception
    {
        // setup
        final ContentType.Builder contentTypeBuilder = newContentType().
            module( ModuleName.from( "myModule" ) ).
            name( "myContentType" ).
            setAbstract( true ).
            displayName( "My content type" ).
            icon( SINGLE_PIXEL_GIF_PICTURE );
        final ContentType contentType = addContentTypeFormItems( contentTypeBuilder );
        contentTypeDao.createContentType( contentType, session );

        // exercise
        final ContentTypes contentTypes =
            contentTypeDao.retrieveContentTypes( QualifiedContentTypeNames.from( "myModule:myContentType" ), session );
        commit();

        // verify
        assertNotNull( contentTypes );
        assertEquals( 1, contentTypes.getSize() );
        final ContentType contentType1 = contentTypes.first();
        assertEquals( "myContentType", contentType1.getName() );
        assertEquals( "myModule", contentType1.getModuleName().toString() );
        assertEquals( true, contentType1.isAbstract() );
        assertEquals( "My content type", contentType1.getDisplayName() );
        assertArrayEquals( SINGLE_PIXEL_GIF_PICTURE, contentType1.getIcon() );
    }

    @Test
    public void retrieveAllContentTypes()
        throws Exception
    {
        // setup
        final ContentType.Builder contentTypeBuilder = newContentType().
            module( ModuleName.from( "myModule" ) ).
            name( "myContentType" ).
            setAbstract( true ).
            displayName( "My content type" );
        final ContentType contentTypeCreated1 = addContentTypeFormItems( contentTypeBuilder );
        contentTypeDao.createContentType( contentTypeCreated1, session );

        final ContentType.Builder contentTypeBuilder2 = newContentType().
            module( ModuleName.from( "otherModule" ) ).
            name( "someContentType" ).
            setAbstract( false ).
            displayName( "Another content type" );
        final ContentType contentTypeCreated2 = addContentTypeFormItems( contentTypeBuilder2 );

        contentTypeDao.createContentType( contentTypeCreated2, session );

        // exercise
        final ContentTypes contentTypes = contentTypeDao.retrieveAllContentTypes( session );
        commit();

        // verify
        assertNotNull( contentTypes );
        assertEquals( 2, contentTypes.getSize() );
        final ContentType contentType1 = contentTypes.getContentType( new QualifiedContentTypeName( "myModule:myContentType" ) );
        final ContentType contentType2 = contentTypes.getContentType( new QualifiedContentTypeName( "otherModule:someContentType" ) );

        assertEquals( "myContentType", contentType1.getName() );
        assertEquals( "myModule", contentType1.getModuleName().toString() );
        assertEquals( true, contentType1.isAbstract() );
        assertEquals( "My content type", contentType1.getDisplayName() );

        assertEquals( "someContentType", contentType2.getName() );
        assertEquals( "otherModule", contentType2.getModuleName().toString() );
        assertEquals( false, contentType2.isAbstract() );
        assertEquals( "Another content type", contentType2.getDisplayName() );
    }

    @Test
    public void updateContentType()
        throws Exception
    {
        // setup
        final ContentType.Builder contentTypeBuilder = newContentType().
            module( ModuleName.from( "myModule" ) ).
            name( "myContentType" ).
            setAbstract( true ).
            displayName( "My content type" );
        final ContentType contentType = addContentTypeFormItems( contentTypeBuilder );
        contentTypeDao.createContentType( contentType, session );

        // exercise
        final ContentTypes contentTypesAfterCreate =
            contentTypeDao.retrieveContentTypes( QualifiedContentTypeNames.from( "myModule:myContentType" ), session );
        assertNotNull( contentTypesAfterCreate );
        assertEquals( 1, contentTypesAfterCreate.getSize() );

        final ContentType contentTypeUpdate = newContentType( contentType ).
            setAbstract( false ).
            displayName( "My content type-UPDATED" ).
            icon( SINGLE_PIXEL_GIF_PICTURE ).
            build();
        contentTypeDao.updateContentType( contentTypeUpdate, session );
        commit();

        // verify
        final ContentTypes contentTypesAfterUpdate =
            contentTypeDao.retrieveContentTypes( QualifiedContentTypeNames.from( "myModule:myContentType" ), session );
        assertNotNull( contentTypesAfterUpdate );
        assertEquals( 1, contentTypesAfterUpdate.getSize() );
        final ContentType contentType1 = contentTypesAfterUpdate.first();
        assertEquals( "myContentType", contentType1.getName() );
        assertEquals( "myModule", contentType1.getModuleName().toString() );
        assertEquals( false, contentType1.isAbstract() );
        assertEquals( "My content type-UPDATED", contentType1.getDisplayName() );
        assertArrayEquals( SINGLE_PIXEL_GIF_PICTURE, contentType1.getIcon() );
    }

    @Test
    public void deleteContentType()
        throws Exception
    {
        // setup
        final ContentType.Builder contentTypeBuilder = newContentType().
            module( ModuleName.from( "myModule" ) ).
            name( "myContentType" ).
            setAbstract( true ).
            displayName( "My content type" );
        final ContentType contentType = addContentTypeFormItems( contentTypeBuilder );
        contentTypeDao.createContentType( contentType, session );

        // exercise
        final ContentTypes contentTypesAfterCreate =
            contentTypeDao.retrieveContentTypes( QualifiedContentTypeNames.from( "myModule:myContentType" ), session );
        assertNotNull( contentTypesAfterCreate );
        assertEquals( 1, contentTypesAfterCreate.getSize() );

        contentTypeDao.deleteContentType( contentType.getQualifiedName(), session );
        commit();

        // verify
        final ContentTypes contentTypesAfterDelete =
            contentTypeDao.retrieveContentTypes( QualifiedContentTypeNames.from( "myModule:myContentType" ), session );
        assertNotNull( contentTypesAfterDelete );
        assertTrue( contentTypesAfterDelete.isEmpty() );
    }

    private ContentType addContentTypeFormItems( final ContentType.Builder contentTypeBuilder )
    {
        final FormItemSet formItemSet = newFormItemSet().name( "address" ).build();
        formItemSet.add( newInput().name( "label" ).label( "Label" ).type( InputTypes.TEXT_LINE ).build() );
        formItemSet.add( newInput().name( "street" ).label( "Street" ).type( InputTypes.TEXT_LINE ).build() );
        formItemSet.add( newInput().name( "postalNo" ).label( "Postal No" ).type( InputTypes.TEXT_LINE ).build() );
        formItemSet.add( newInput().name( "country" ).label( "Country" ).type( InputTypes.TEXT_LINE ).build() );
        contentTypeBuilder.addFormItem( newInput().name( "title" ).type( InputTypes.TEXT_LINE ).build() );
        contentTypeBuilder.addFormItem( formItemSet );
        return contentTypeBuilder.build();
    }
}
