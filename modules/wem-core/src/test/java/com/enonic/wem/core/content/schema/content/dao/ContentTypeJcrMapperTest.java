package com.enonic.wem.core.content.schema.content.dao;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Before;
import org.junit.Test;

import com.enonic.wem.api.Icon;
import com.enonic.wem.api.content.schema.content.ContentType;
import com.enonic.wem.api.content.schema.content.QualifiedContentTypeName;
import com.enonic.wem.api.module.ModuleName;
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

        Node myContentType = session.getRootNode().addNode( "myContentType" );

        mapper.toJcr( ContentType.newContentType().
            name( "myContentType" ).
            module( ModuleName.from( "mymodule" ) ).
            displayName( "My module" ).
            superType( QualifiedContentTypeName.structured() ).
            setAbstract( false ).
            setFinal( true ).
            superType( null ).
            createdTime( CREATED_TIME ).
            modifiedTime( MODIFIED_TIME ).
            icon( Icon.from( new byte[]{123}, "image/gif" ) ).
            contentDisplayNameScript( "$('firstName') + ' ' +  $('lastName')" ).
            build(), myContentType );

        assertEquals( "2013-01-01T12:00:00.000Z", myContentType.getProperty( "createdTime" ).getString() );
        assertEquals( "2013-01-01T13:00:00.000Z", myContentType.getProperty( "modifiedTime" ).getString() );
        assertEquals( "image/gif", myContentType.getProperty( "iconMimeType" ).getString() );
        assertArrayEquals( new byte[]{123}, JcrHelper.getPropertyBinary( myContentType, "icon" ) );
        assertEquals( getJsonFileAsJson( "contentType-config.json" ),
                      stringToJson( myContentType.getProperty( ContentTypeJcrMapper.CONTENT_TYPE ).getString() ) );
    }

    @Test
    public void toContentType()
        throws RepositoryException
    {
        // setup
        ContentTypeJcrMapper mapper = new ContentTypeJcrMapper();

        Node relationshipNode = session.getRootNode().addNode( "myContentType" );

        mapper.toJcr( ContentType.newContentType().
            name( "myContentType" ).
            module( ModuleName.from( "mymodule" ) ).
            displayName( "My module" ).
            superType( QualifiedContentTypeName.structured() ).
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
        assertEquals( "myContentType", contentType.getName() );
        assertEquals( ModuleName.from( "mymodule" ), contentType.getModuleName() );
        assertEquals( "My module", contentType.getDisplayName() );
        assertEquals( QualifiedContentTypeName.structured(), contentType.getSuperType() );
        assertEquals( false, contentType.isAbstract() );
        assertEquals( true, contentType.isFinal() );
        assertEquals( Icon.from( new byte[]{123}, "image/gif" ), contentType.getIcon() );
        assertEquals( "$('firstName') + ' ' +  $('lastName')", contentType.getContentDisplayNameScript() );
    }
}