package com.enonic.wem.core.content.type;

import java.io.StringWriter;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.WordUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.content.type.ContentType;
import com.enonic.wem.api.content.type.QualifiedContentTypeName;
import com.enonic.wem.api.content.type.QualifiedContentTypeNames;
import com.enonic.wem.api.module.Module;

import static com.enonic.wem.api.command.Commands.contentType;
import static com.enonic.wem.api.content.type.ContentType.newContentType;

@Component
public class ContentTypesInitializer
{
    private static final ContentType CONTENT = createSystemType( QualifiedContentTypeName.content(), false, true );

    private static final ContentType SPACE = createSystemType( QualifiedContentTypeName.space(), true, false );

    private static final ContentType STRUCTURED = createSystemType( QualifiedContentTypeName.structured(), false, true );

    private static final ContentType UNSTRUCTURED = createSystemType( QualifiedContentTypeName.unstructured(), false, false );

    private static final ContentType FOLDER = createSystemType( QualifiedContentTypeName.folder(), false, false );

    private static final ContentType PAGE = createSystemType( QualifiedContentTypeName.page(), true, false );

    private static final ContentType SHORTCUT = createSystemType( QualifiedContentTypeName.shortcut(), true, false );

    private static final ContentType FILE = createSystemType( QualifiedContentTypeName.file(), true, false );

    private static final ContentType[] SYSTEM_TYPES = {CONTENT, SPACE, STRUCTURED, UNSTRUCTURED, FOLDER, PAGE, SHORTCUT, FILE};

    private static final String[] TEST_CONTENT_TYPES =
        {"demo-contenttype-htmlarea.json", "demo-contenttype-fieldset.json", "demo-contenttype-set.json", "demo-contenttype-blog.json",
            "demo-contenttype-article1.json", "demo-contenttype-article2.json"};

    private static final Logger LOG = LoggerFactory.getLogger( ContentTypesInitializer.class );

    private Client client;

    public void createSystemTypes()
    {
        for ( final ContentType contentType : SYSTEM_TYPES )
        {
            addContentType( contentType );
        }

        addTestContentTypes();
    }

    private void addTestContentTypes()
    {
        for ( String testContentTypeFile : TEST_CONTENT_TYPES )
        {
            importJsonContentType( testContentTypeFile );
        }
    }

    private void addContentType( final ContentType contentType )
    {
        final QualifiedContentTypeNames qualifiedNames = QualifiedContentTypeNames.from( contentType.getQualifiedName() );
        final boolean contentTypeExists = !client.execute( contentType().get().names( qualifiedNames ) ).isEmpty();
        if ( !contentTypeExists )
        {
            client.execute( contentType().create().contentType( contentType ) );
        }
    }

    private static ContentType createSystemType( final QualifiedContentTypeName qualifiedName, final boolean isFinal,
                                                 final boolean isAbstract )
    {
        final String displayName = WordUtils.capitalize( qualifiedName.getContentTypeName() );
        final String contentTypeName = qualifiedName.getContentTypeName();
        return newContentType().
            module( Module.SYSTEM.getName() ).
            name( contentTypeName ).
            displayName( displayName ).
            setFinal( isFinal ).
            setAbstract( isAbstract ).
            build();
    }

    private void importJsonContentType( final String fileName )
    {
        final ContentType contentType = loadContentTypeJson( "/META-INF/content-types/" + fileName );
        if ( contentType != null )
        {
            addContentType( contentType );
        }
    }

    private ContentType loadContentTypeJson( final String filePath )
    {
        try
        {
            final StringWriter writer = new StringWriter();
            IOUtils.copy( getClass().getResourceAsStream( filePath ), writer );
            final String contentTypeSerialized = writer.toString();

            final ContentTypeJsonSerializer contentTypeJsonSerializer = new ContentTypeJsonSerializer();
            return contentTypeJsonSerializer.toObject( contentTypeSerialized );
        }
        catch ( Exception e )
        {
            LOG.error( "Unable to import content type from " + filePath, e );
            return null;
        }
    }

    @Autowired
    public void setClient( final Client client )
    {
        this.client = client;
    }
}
