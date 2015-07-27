package com.enonic.xp.toolbox.app;


import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.IOUtils;

import io.airlift.airline.Command;
import io.airlift.airline.Option;

import com.enonic.xp.toolbox.ToolCommand;

@Command(name = "init-app", description = "Initiates an Enonic XP application.")
public final class InitAppCommand
    extends ToolCommand
{
    private static final int BUFFER_SIZE = 4096;

    private static final String INIT_APP_PATH = "resources/app-init.zip";

    private static final String NAME_REGEX = "(name = ')([\\w\\.]+)(')";


    @Option(name = {"-n", "--name"}, description = "Application name.", required = true)
    public String name;

    @Option(name = {"-v", "--version"}, description = "Version number.")
    public String version;

    @Option(name = {"-d", "--destination"}, description = "Project path.")
    public String destination;

    @Override
    protected void execute()
        throws Exception
    {
        try
        {
            unzip( INIT_APP_PATH, destination );
            updateBuildFile( destination + "/app-init/build.gradle", name );
        }
        catch ( Exception e )
        {
            System.err.println( e.getMessage() );
        }
    }

    private void unzip( String src, String dest )
        throws IOException
    {

        // Unzip to the root folder, if the destination not set
        dest = dest != null ? dest : "";

        File destDir = new File( dest );

        // Create a full path if doesn't exist
        if ( !destDir.exists() )
        {
            destDir.mkdirs();
        }

        ZipInputStream zipIn = new ZipInputStream( new FileInputStream( src ) );
        ZipEntry entry = zipIn.getNextEntry();

        while ( entry != null )
        {
            String filePath = dest + File.separator + entry.getName();

            if ( !entry.isDirectory() )
            {
                extract( zipIn, filePath );
            }
            else
            {
                File dir = new File( filePath );
                dir.mkdirs();
            }
            zipIn.closeEntry();
            entry = zipIn.getNextEntry();
        }

        zipIn.close();
    }

    private void extract( ZipInputStream zipIn, String filePath )
        throws IOException
    {
        BufferedOutputStream bos = new BufferedOutputStream( new FileOutputStream( filePath ) );
        byte[] bytesIn = new byte[BUFFER_SIZE];
        int read;

        while ( ( read = zipIn.read( bytesIn ) ) != -1 )
        {
            bos.write( bytesIn, 0, read );
        }

        bos.close();
    }

    private void updateBuildFile( String src, String name )
        throws IOException
    {
        File buildFile = new File( src );
        Charset charset = StandardCharsets.UTF_8;

        String content = IOUtils.toString( new FileInputStream( buildFile ), charset );
        content = content.replaceAll( NAME_REGEX, "name = '" + name + "'" );
        IOUtils.write( content, new FileOutputStream( buildFile ), charset );
    }
}
