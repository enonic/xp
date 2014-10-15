package com.enonic.wem.core.schema.mixin;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.wem.api.module.Module;
import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.resource.Resource;
import com.enonic.wem.api.resource.ResourceKey;
import com.enonic.wem.api.schema.mixin.Mixin;
import com.enonic.wem.api.schema.mixin.MixinName;
import com.enonic.wem.api.schema.mixin.Mixins;
import com.enonic.wem.api.xml.mapper.XmlMixinMapper;
import com.enonic.wem.api.xml.model.XmlMixin;
import com.enonic.wem.api.xml.serializer.XmlSerializers2;
import com.enonic.wem.core.support.dao.IconDao;

import static java.util.stream.Collectors.toList;

public final class MixinLoader
{
    private final static Logger LOG = LoggerFactory.getLogger( MixinLoader.class );

    private final static Pattern MIXIN_PATTERN = Pattern.compile( "mixin/([^/]+)/mixin\\.xml" );

    private final static String MIXIN_FILE = "mixin.xml";

    private final static String MIXIN_DIRECTORY = "mixin";

    private final IconDao iconDao = new IconDao();

    public Mixins loadMixins( final Module module )
    {
        final ModuleKey moduleKey = module.getKey();
        final List<MixinName> mixinNames = findMixinNames( module );

        final List<Mixin> mixins = mixinNames.stream().
            map( ( mixinName ) -> loadMixin( moduleKey, mixinName ) ).
            filter( Objects::nonNull ).
            collect( toList() );

        return Mixins.from( mixins );
    }

    private Mixin loadMixin( final ModuleKey moduleKey, final MixinName mixinName )
    {
        final String name = mixinName.getLocalName();
        final ResourceKey folderResourceKey = ResourceKey.from( moduleKey, MIXIN_DIRECTORY + "/" + name );
        final ResourceKey mixinResourceKey = folderResourceKey.resolve( MIXIN_FILE );

        final Resource resource = Resource.from( mixinResourceKey );
        if ( resource.exists() )
        {
            try
            {
                final String serializedResource = resource.readString();

                final Mixin.Builder mixin = parseMixinXml( serializedResource );
                final Instant modifiedTime = Instant.now();
                mixin.modifiedTime( modifiedTime );
                mixin.createdTime( modifiedTime );
                mixin.icon( iconDao.readIcon( folderResourceKey ) );
                return mixin.name( MixinName.from( moduleKey, name ) ).build();
            }
            catch ( Exception e )
            {
                LOG.warn( "Could not load mixin [" + mixinResourceKey + "]", e );
            }
        }
        return null;
    }

    private List<MixinName> findMixinNames( final Module module )
    {
        return module.getResourcePaths().stream().
            map( this::getMixinNameFromResourcePath ).
            filter( Objects::nonNull ).
            map( ( localName ) -> MixinName.from( module.getKey(), localName ) ).
            collect( toList() );
    }

    private String getMixinNameFromResourcePath( final String resourcePath )
    {
        final Matcher matcher = MIXIN_PATTERN.matcher( resourcePath );
        return matcher.matches() ? matcher.group( 1 ) : null;
    }

    private Mixin.Builder parseMixinXml( final String serializedMixin )
    {
        final Mixin.Builder builder = Mixin.newMixin();
        final XmlMixin mixinXml = XmlSerializers2.mixin().parse( serializedMixin );
        XmlMixinMapper.fromXml( mixinXml, builder );
        return builder;
    }

}
