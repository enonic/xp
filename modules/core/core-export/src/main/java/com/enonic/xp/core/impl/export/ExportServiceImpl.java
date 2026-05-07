package com.enonic.xp.core.impl.export;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.Comparator;
import java.util.stream.Stream;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.core.impl.export.reader.ZipVirtualFile;
import com.enonic.xp.core.impl.export.writer.ExportWriter;
import com.enonic.xp.core.impl.export.writer.ZipExportWriter;
import com.enonic.xp.core.internal.FileNames;
import com.enonic.xp.core.internal.FilePredicates;
import com.enonic.xp.export.ExportInfo;
import com.enonic.xp.export.ExportNodesParams;
import com.enonic.xp.export.ExportService;
import com.enonic.xp.export.ImportNodesParams;
import com.enonic.xp.export.ListExportsResult;
import com.enonic.xp.export.NodeExportResult;
import com.enonic.xp.export.NodeImportResult;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.server.VersionInfo;
import com.enonic.xp.vfs.VirtualFile;

@Component(immediate = true)
public class ExportServiceImpl
    implements ExportService
{
    private final String xpVersion;

    private final NodeService nodeService;

    private final ExportConfigurationDynamic exportConfiguration;

    @Activate
    public ExportServiceImpl( @Reference final ExportConfigurationDynamic exportConfiguration, @Reference final NodeService nodeService )
    {
        this.xpVersion = VersionInfo.get().getVersion();
        this.nodeService = nodeService;
        this.exportConfiguration = exportConfiguration;
    }

    @Override
    public NodeExportResult exportNodes( final ExportNodesParams params )
    {
        final Path targetDirectory = exportConfiguration.getExportsDir().resolve( params.getExportName() );

        final ExportWriter exportWriter = ZipExportWriter.create( exportConfiguration.getExportsDir(), params.getExportName() );

        try (exportWriter)
        {
            return NodeExporter.create()
                .sourceNodePath( params.getSourceNodePath() )
                .batchSize( params.getBatchSize() )
                .nodeService( this.nodeService )
                .nodeExportWriter( exportWriter )
                .targetDirectory( targetDirectory )
                .xpVersion( xpVersion )
                .nodeExportListener( params.getNodeExportListener() )
                .build()
                .execute();
        }
        catch ( IOException e )
        {
            throw new UncheckedIOException( e );
        }
    }

    @Override
    public NodeImportResult importNodes( final ImportNodesParams params )
    {
        VirtualFile source = params.getSource();

        if ( source == null )
        {
            final Path zipPath = exportConfiguration.getExportsDir().resolve( params.getExportName() + ".zip" );
            try
            {
                source = ZipVirtualFile.from( zipPath );
            }
            catch ( IOException e )
            {
                throw new UncheckedIOException( e );
            }
        }

        return NodeImporter.create()
            .nodeService( this.nodeService )
            .sourceDirectory( source )
            .targetNodePath( params.getTargetNodePath() )
            .importNodeIds( params.isImportNodeIds() )
            .importPermissions( params.isImportPermissions() )
            .xslt( params.getXslt() )
            .xsltParams( params.getXsltParams() )
            .nodeImportListener( params.getNodeImportListener() )
            .versionAttributesResolver( params.getVersionAttributesResolver() )
            .build()
            .execute();
    }

    @Override
    public ListExportsResult list()
    {
        final Path exportsDir = exportConfiguration.getExportsDir();

        final ListExportsResult.Builder builder = ListExportsResult.create();

        if ( !Files.isDirectory( exportsDir ) )
        {
            return builder.build();
        }

        try (Stream<Path> entries = Files.list( exportsDir ))
        {
            entries.filter( Files::isRegularFile )
                .filter( FilePredicates::isVisible )
                .filter( path -> FileNames.isAnyOfExtensions( path.getFileName().toString(), "zip" ) )
                .map( TimedExport::new )
                .sorted( Comparator.comparing( TimedExport::created ).reversed() )
                .map( e -> e.path.getFileName().toString() )
                .map( name -> name.substring( 0, name.length() - ".zip".length() ) )
                .map( ExportInfo::new )
                .forEach( builder::addExport );
        }
        catch ( IOException e )
        {
            throw new UncheckedIOException( e );
        }

        return builder.build();
    }

    private static final class TimedExport
    {
        private final Path path;

        private final FileTime created;

        private TimedExport( final Path path )
        {
            this.path = path;
            this.created = readCreationTime( path );
        }

        private FileTime created()
        {
            return created;
        }

        private static FileTime readCreationTime( final Path path )
        {
            try
            {
                return Files.readAttributes( path, BasicFileAttributes.class ).creationTime();
            }
            catch ( IOException e )
            {
                throw new UncheckedIOException( e );
            }
        }
    }
}
