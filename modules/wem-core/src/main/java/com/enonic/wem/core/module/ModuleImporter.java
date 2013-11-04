package com.enonic.wem.core.module;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.zip.ZipFile;

import org.apache.commons.lang.StringUtils;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.exception.SystemException;
import com.enonic.wem.api.module.Module;
import com.enonic.wem.api.module.ModuleFileEntry;
import com.enonic.wem.api.module.ModuleKey;

import static com.enonic.wem.api.module.Module.newModule;

public class ModuleImporter
{

    private final ModuleXmlSerializer xmlSerializer = new ModuleXmlSerializer();

    public Module importModuleFromZip( final Path zipFile )
        throws IOException
    {
        if ( !Files.exists( zipFile ) )
        {
            throw new NoSuchFileException( zipFile.toString() );
        }
        Preconditions.checkArgument( Files.isRegularFile( zipFile ), "Module file is not a file: " + zipFile );

        final String moduleDirName = StringUtils.substringBeforeLast( zipFile.getFileName().toString(), ".zip" );
        final ModuleKey moduleKey = ModuleKey.from( moduleDirName );
        final Module.Builder moduleBuilder = newModule().moduleKey( moduleKey );

        if ( !isValidZipFile( zipFile ) )
        {
            throw new SystemException( "Invalid zip file [{0}]", zipFile.getFileName() );
        }

        try (FileSystem zipFs = FileSystems.newFileSystem( zipFile, null ))
        {
            final Path moduleRootPath = zipFs.getPath( "/" );

            final Path xmlFile = moduleRootPath.resolve( Module.MODULE_XML );
            Preconditions.checkArgument( Files.isRegularFile( xmlFile ), "Module descriptor file not found: " + xmlFile );

            readModuleXml( moduleBuilder, xmlFile );
            final ModuleFileEntry.Builder rootEntry = moduleBuilder.getModuleDirectoryEntry();
            importFiles( rootEntry, moduleRootPath, true );
        }
        return moduleBuilder.build();
    }

    public Module importModuleFromDirectory( final Path moduleDirectoryPath )
        throws IOException
    {
        if ( !Files.exists( moduleDirectoryPath ) )
        {
            throw new NoSuchFileException( moduleDirectoryPath.toString() );
        }
        Preconditions.checkArgument( Files.isDirectory( moduleDirectoryPath ), "Module file is not a directory: " + moduleDirectoryPath );
        final Path xmlFile = moduleDirectoryPath.resolve( Module.MODULE_XML );
        Preconditions.checkArgument( Files.isRegularFile( xmlFile ), "Module descriptor file not found: " + xmlFile );

        final String moduleDirName = moduleDirectoryPath.getFileName().toString();
        final ModuleKey moduleKey = ModuleKey.from( moduleDirName );
        final Module.Builder moduleBuilder = newModule().moduleKey( moduleKey );

        readModuleXml( moduleBuilder, xmlFile );
        final ModuleFileEntry.Builder rootEntry = moduleBuilder.getModuleDirectoryEntry();
        importFiles( rootEntry, moduleDirectoryPath, true );

        return moduleBuilder.build();
    }

    private void importFiles( final ModuleFileEntry.Builder parentEntry, final Path parentDirectory, final boolean rootLevel )
        throws IOException
    {
        final String FS_SEPARATOR = parentDirectory.getFileSystem().getSeparator();
        try (DirectoryStream<Path> ds = Files.newDirectoryStream( parentDirectory ))
        {
            for ( Path path : ds )
            {
                if ( rootLevel && Module.MODULE_XML.equals( path.getFileName().toString() ) )
                {
                    continue;
                }
                if ( Files.isDirectory( path ) )
                {
                    final String dirName = path.getFileName().toString().replace( FS_SEPARATOR, "" );
                    final ModuleFileEntry.Builder directoryEntry = ModuleFileEntry.directoryBuilder( dirName );
                    importFiles( directoryEntry, path, false );
                    parentEntry.addEntry( directoryEntry );
                }
                else
                {
                    parentEntry.addFile( path );
                }
            }
        }
    }

    private void readModuleXml( final Module.Builder moduleBuilder, final Path xmlFile )
        throws IOException
    {
        final String xml = new String( Files.readAllBytes( xmlFile ), Charset.forName( "UTF-8" ) );
        xmlSerializer.toModule( xml, moduleBuilder );
    }

    private boolean isValidZipFile( final Path zipFilePath )
    {
        try (ZipFile zipfile = new ZipFile( zipFilePath.toFile() ))
        {
            return true;
        }
        catch ( IOException e )
        {
            return false;
        }
    }
}
