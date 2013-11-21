package com.enonic.wem.core.exporters;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

import com.enonic.wem.api.Identity;
import com.enonic.wem.api.content.page.ImageTemplate;
import com.enonic.wem.api.content.page.LayoutTemplate;
import com.enonic.wem.api.content.page.PageTemplate;
import com.enonic.wem.api.content.page.PartTemplate;
import com.enonic.wem.api.content.site.SiteTemplate;
import com.enonic.wem.api.module.Module;

/**
 * export either to zip file or directory
 *
 * @param <T> - external entity type to export
 */
public abstract class EntityExporter<T>
{
    private final static Map<String, String> ZIP_FS_ENV = ImmutableMap.of( "create", "true" );

    private final static Map<Class, EntityExporter> classToExporterMap = new HashMap<>();

    public Path exportToZip( final T object, final Path targetDirectory )
        throws IOException
    {
        final Identity identity = Identity.class.cast( object );

        final Path zipLocation = targetDirectory.resolve( identity.getKey().toString() + ".zip" );
        if ( Files.exists( zipLocation ) )
        {
            throw new FileAlreadyExistsException( zipLocation.toString() );
        }

        final URI fileUri = zipLocation.toUri();

        final URI zipUri;
        try
        {
            zipUri = new URI( "jar:" + fileUri.getScheme(), fileUri.getPath(), null );
        }
        catch ( URISyntaxException e )
        {
            throw new RuntimeException( e );
        }

        try (final FileSystem zipFs = FileSystems.newFileSystem( zipUri, ZIP_FS_ENV ))
        {
            final Path rootPath = zipFs.getPath( "/" );
            exportFiles( getSerializingObject( object ), rootPath );
            writeXml( object, rootPath );
        }

        return zipLocation;
    }

    public Path exportToDirectory( final T object, final Path exportLocation )
        throws IOException
    {
        if ( !Files.isDirectory( exportLocation ) )
        {
            throw new FileNotFoundException( exportLocation.toString() );
        }

        final Identity identity = Identity.class.cast( object );

        final String directoryName = identity.getKey().toString();
        final Path rootPath = exportLocation.resolve( directoryName );
        createPath( rootPath );

        exportFiles( getSerializingObject( object ), rootPath );

        writeXml( object, rootPath );

        return rootPath;
    }

    protected Path createPath( final Path rootPath )
        throws IOException
    {
        if ( !Files.isDirectory( rootPath ) )
        {
            Files.createDirectory( rootPath );
        }

        return rootPath;
    }

    protected void exportFiles( final Object serializingObjects, final Path rootPath )
        throws IOException
    {

    }

    protected String getXmlFileName( final T object )
    {
        return getClass().getAnnotation( XMLFilename.class ).value();
    }

    public void writeXml( final T object, final Path rootPath )
        throws IOException
    {
        final Path xmlFile = rootPath.resolve( getXmlFileName( object ) );
        final String xml = serializeToXMLString( object );
        Files.write( xmlFile, xml.getBytes( Charset.forName( "UTF-8" ) ) );
    }

    // returns which object to serialize
    protected Object getSerializingObject( final T object )
    {
        return object;
    }

    // serializes one from object
    protected abstract String serializeToXMLString( final T object );

    protected static <R> EntityExporter<R> getExporter( R object )
    {
        return classToExporterMap.get( object.getClass() );
    }

    protected static void registerExporter( Class clazz, EntityExporter abstractExporter )
    {
        classToExporterMap.put( clazz, abstractExporter );
    }

    static {
        registerExporter( Module.class, new ModuleExporter() );
        registerExporter( SiteTemplate.class, new SiteTemplateExporter() );

        registerExporter( ImageTemplate.class, new ImageTemplateExporter() );
        registerExporter( LayoutTemplate.class, new LayoutTemplateExporter() );
        registerExporter( PageTemplate.class, new PageTemplateExporter() );
        registerExporter( PartTemplate.class, new PartTemplateExporter() );
    }
}
