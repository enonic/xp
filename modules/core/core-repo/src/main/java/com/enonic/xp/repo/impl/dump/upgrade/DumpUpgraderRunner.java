package com.enonic.xp.repo.impl.dump.upgrade;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

import com.enonic.xp.dump.DumpUpgradeResult;
import com.enonic.xp.repo.impl.dump.RepoDumpException;
import com.enonic.xp.repo.impl.dump.RepoLoadException;
import com.enonic.xp.repo.impl.dump.reader.DumpReaderV7;
import com.enonic.xp.repo.impl.dump.reader.ZipDumpReaderV7;
import com.enonic.xp.repo.impl.dump.upgrade.model8to9.DumpUpgrader8to9;
import com.enonic.xp.repo.impl.dump.writer.DumpWriter;
import com.enonic.xp.repo.impl.dump.writer.ZipDumpWriterV8;
import com.enonic.xp.upgrade.UpgradeListener;
import com.enonic.xp.util.Version;

public class DumpUpgraderRunner
{
    private static final Logger LOG = LoggerFactory.getLogger( DumpUpgraderRunner.class );

    private static final Version SUPPORTED_MODEL_VERSION = new Version( 8, 0, 0 );

    public DumpUpgradeResult upgrade( final Path basePath, final String dumpName, final UpgradeListener upgradeListener )
    {
        final DumpUpgradeResult.Builder result = DumpUpgradeResult.create().dumpName( dumpName );

        try (DumpReaderV7 dumpReader = ZipDumpReaderV7.create( basePath, dumpName ))
        {
            final Version modelVersion = Objects.requireNonNullElse( dumpReader.getDumpMeta().getModelVersion(), Version.emptyVersion );

            result.initialVersion( modelVersion );

            if ( !SUPPORTED_MODEL_VERSION.equals( modelVersion ) )
            {
                throw new RepoLoadException(
                    "Cannot upgrade dump; model version [" + modelVersion + "] is not supported, expected [" + SUPPORTED_MODEL_VERSION +
                        "]" );
            }

            DumpUpgrader dumpUpgrader = new DumpUpgrader8to9( dumpReader );

            if ( upgradeListener != null )
            {
                upgradeListener.total( 1 );
            }

            final Version targetModelVersion = dumpUpgrader.getModelVersion();
            final String targetDumpName = dumpName + "-upgraded-to-" + targetModelVersion.toShortestString();

            try (DumpWriter dumpWriter = ZipDumpWriterV8.create( basePath, targetDumpName ))
            {
                LOG.info( "Running upgrade step [{}]...", dumpUpgrader.getName() );
                dumpUpgrader.upgrade( dumpWriter );
                LOG.info( "Finished upgrade step [{}]", dumpUpgrader.getName() );
            }

            if ( upgradeListener != null )
            {
                upgradeListener.upgraded();
            }

            result.upgradedVersion( targetModelVersion );
            result.dumpName( targetDumpName );
            return result.build();
        }

        catch ( IOException e )
        {
            throw new RepoDumpException( "Failed to close dump reader/writer", e );
        }
    }

    static void main( final String[] args )
    {
        final Path zipPath = Path.of( args[0] );
        final String fileName = zipPath.getFileName().toString();
        Preconditions.checkArgument( fileName.endsWith( ".zip" ), "Argument must end with .zip" );
        new DumpUpgraderRunner().upgrade( zipPath.getParent(), fileName.substring( 0, fileName.length() - 4 ), null );
    }
}
