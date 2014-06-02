package com.enonic.wem.core.schema.mixin.dao;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

import javax.inject.Inject;

import org.apache.commons.io.FileUtils;
import org.joda.time.Instant;

import com.google.common.base.Charsets;

import com.enonic.wem.api.Icon;
import com.enonic.wem.api.exception.SystemException;
import com.enonic.wem.api.schema.mixin.Mixin;
import com.enonic.wem.api.schema.mixin.MixinName;
import com.enonic.wem.api.schema.mixin.Mixins;
import com.enonic.wem.core.config.SystemConfig;
import com.enonic.wem.core.support.dao.IconDao;
import com.enonic.wem.core.schema.mixin.MixinXmlSerializer;

import static com.enonic.wem.api.schema.mixin.Mixins.newMixins;
import static java.nio.file.Files.getLastModifiedTime;
import static java.nio.file.Files.isDirectory;
import static java.nio.file.Files.isRegularFile;
import static java.nio.file.Files.readAttributes;

public final class MixinDaoImpl
    implements MixinDao
{
    private static final String MIXIN_XML = "mixin.xml";

    private Path basePath;

    @Override
    public Mixin createMixin( final Mixin mixin )
    {
        final Path mixinPath = pathForMixin( mixin.getName() );

        writeMixinXml( mixin, mixinPath );
        new IconDao().writeIcon( mixin.getIcon(), mixinPath );

        return mixin;
    }

    @Override
    public void updateMixin( final Mixin mixin )
    {
        final Path mixinPath = pathForMixin( mixin.getName() );

        writeMixinXml( mixin, mixinPath );
        new IconDao().writeIcon( mixin.getIcon(), mixinPath );
    }

    @Override
    public Mixins getAllMixins()
    {
        final Mixins.Builder mixins = newMixins();

        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream( this.basePath ))
        {
            for ( Path schemaDir : directoryStream )
            {
                final Mixin.Builder mixinBuilder = readMixin( schemaDir );
                final Mixin mixin = mixinBuilder != null ? mixinBuilder.build() : null;
                if ( mixin != null )
                {
                    mixins.add( mixin );
                }
            }
        }
        catch ( IOException e )
        {
            throw new SystemException( e, "Could not retrieve mixins" );
        }
        return mixins.build();
    }

    @Override
    public Mixin.Builder getMixin( final MixinName mixinName )
    {
        final Path mixinPath = pathForMixin( mixinName );
        return readMixin( mixinPath );
    }

    @Override
    public boolean deleteMixin( final MixinName mixinName )
    {
        final Path mixinPath = pathForMixin( mixinName );
        final boolean mixinDirExists = isDirectory( mixinPath ) && isRegularFile( mixinPath.resolve( MIXIN_XML ) );
        if ( mixinDirExists )
        {
            try
            {
                FileUtils.deleteDirectory( mixinPath.toFile() );
                return true;
            }
            catch ( IOException e )
            {
                throw new SystemException( e, "Could not delete mixin [{0}]", mixinName );
            }
        }
        else
        {
            return false;
        }
    }

    private Mixin.Builder readMixin( final Path mixinPath )
    {
        final boolean isMixinDir = isDirectory( mixinPath ) && isRegularFile( mixinPath.resolve( MIXIN_XML ) );
        if ( isMixinDir )
        {
            final Mixin.Builder mixin = readMixinXml( mixinPath );
            final Icon icon = new IconDao().readIcon( mixinPath );
            mixin.icon( icon );
            return mixin;
        }
        return null;
    }

    private void writeMixinXml( final Mixin mixin, final Path mixinPath )
    {
        final MixinXmlSerializer xmlSerializer = new MixinXmlSerializer().prettyPrint( true ).generateName( false );
        final String serializedMixin = xmlSerializer.toString( mixin );
        final Path xmlFile = mixinPath.resolve( MIXIN_XML );

        try
        {
            Files.createDirectories( mixinPath );
            Files.write( xmlFile, serializedMixin.getBytes( Charsets.UTF_8 ) );
        }
        catch ( IOException e )
        {
            throw new SystemException( e, "Could not store mixin [{0}]", mixin.getName() );
        }
    }

    private Mixin.Builder readMixinXml( final Path mixinPath )
    {
        final Path xmlFile = mixinPath.resolve( MIXIN_XML );
        try
        {
            final String serializedMixin = new String( Files.readAllBytes( xmlFile ), Charsets.UTF_8 );
            final String mixinName = mixinPath.getFileName().toString();
            final MixinXmlSerializer xmlSerializer = new MixinXmlSerializer().overrideName( mixinName );
            final Mixin mixin = xmlSerializer.toMixin( serializedMixin );
            // TODO make mixin xml parser return Mixin.Builder
            final Mixin.Builder builder = Mixin.newMixin( mixin );

            // TODO remove conversion to JODA Instant and use Java 8 Instant, call toInstant() instead of toMillis()
            final Instant modifiedTime = new Instant( getLastModifiedTime( xmlFile ).toMillis() );
            builder.modifiedTime( modifiedTime );

            final BasicFileAttributes basicAttr = readAttributes( xmlFile, BasicFileAttributes.class );
            final Instant createdTime = new Instant( basicAttr.creationTime().toMillis() );
            builder.createdTime( createdTime );

            return builder;
        }
        catch ( IOException e )
        {
            throw new SystemException( e, "Could not read mixin [{0}]", mixinPath.getFileName() );
        }
    }

    private Path pathForMixin( final MixinName mixinName )
    {
        return this.basePath.resolve( mixinName.toString() );
    }

    @Inject
    public void setSystemConfig( final SystemConfig systemConfig )
        throws IOException
    {
        this.basePath = systemConfig.getMixinsDir();
        Files.createDirectories( this.basePath );
    }
}
