package com.enonic.wem.core.schema.content.dao;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Before;
import org.junit.Test;

import com.enonic.wem.api.Icon;

import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.core.AbstractJcrTest;
import com.enonic.wem.core.jcr.JcrHelper;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.*;


public class ContentTypeJcrMapperTest
    extends AbstractJcrTest
{
    private static final DateTime CREATED_TIME = new DateTime( 2013, 1, 1, 12, 0, 0, 0, DateTimeZone.UTC );

    private static final DateTime MODIFIED_TIME = new DateTime( 2013, 1, 1, 13, 0, 0, 0, DateTimeZone.UTC );

    @Before
    public void before()
    {
    }

    @Override
    protected void setupDao()
        throws Exception
    {
        // no dao to setup
    }

    @Test
    public void toJcr()
        throws RepositoryException
    {
        ContentTypeJcrMapper mapper = new ContentTypeJcrMapper();

        Node my_content_type = session.getRootNode().addNode( "my_content_type" );

        mapper.toJcr( ContentType.newContentType().
            name( "my_content_type" ).
            displayName( "My module" ).
            superType( ContentTypeName.structured() ).
            setAbstract( false ).
            setFinal( true ).
            superType( ContentTypeName.unstructured() ).
            createdTime( CREATED_TIME ).
            modifiedTime( MODIFIED_TIME ).
            icon( Icon.from( new byte[]{123}, "image/gif" ) ).
            contentDisplayNameScript( "$('firstName') + ' ' +  $('lastName')" ).
            build(), my_content_type );

        assertEquals( "2013-01-01T12:00:00.000Z", my_content_type.getProperty( "createdTime" ).getString() );
        assertEquals( "2013-01-01T13:00:00.000Z", my_content_type.getProperty( "modifiedTime" ).getString() );
        assertEquals( "image/gif", my_content_type.getProperty( "iconMimeType" ).getString() );
        assertArrayEquals( new byte[]{123}, JcrHelper.getPropertyBinary( my_content_type, "icon" ) );
        assertEquals( getJsonFileAsJson( "contentType-config.json" ),
                      stringToJson( my_content_type.getProperty( ContentTypeJcrMapper.CONTENT_TYPE ).getString() ) );
    }

    @Test
    public void toContentType()
        throws RepositoryException
    {
        // setup
        ContentTypeJcrMapper mapper = new ContentTypeJcrMapper();

        Node relationshipNode = session.getRootNode().addNode( "my_content_type" );

        mapper.toJcr( ContentType.newContentType().
            name( "my_content_type" ).
            displayName( "My module" ).
            superType( ContentTypeName.structured() ).
            setAbstract( false ).
            setFinal( true ).
            createdTime( CREATED_TIME ).
            modifiedTime( MODIFIED_TIME ).
            icon( Icon.from( new byte[]{123}, "image/gif" ) ).
            contentDisplayNameScript( "$('firstName') + ' ' +  $('lastName')" ).
            build(), relationshipNode );

        // exercise
        ContentType contentType = mapper.toContentType( relationshipNode );

        // verify
        assertEquals( CREATED_TIME, contentType.getCreatedTime() );
        assertEquals( MODIFIED_TIME, contentType.getModifiedTime() );
        assertEquals( "my_content_type", contentType.getName() );
        assertEquals( "My module", contentType.getDisplayName() );
        assertEquals( ContentTypeName.structured(), contentType.getSuperType() );
        assertEquals( false, contentType.isAbstract() );
        assertEquals( true, contentType.isFinal() );
        assertEquals( Icon.from( new byte[]{123}, "image/gif" ), contentType.getIcon() );
        assertEquals( "$('firstName') + ' ' +  $('lastName')", contentType.getContentDisplayNameScript() );
    }
}