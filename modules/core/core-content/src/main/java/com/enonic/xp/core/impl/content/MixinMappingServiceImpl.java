package com.enonic.xp.core.impl.content;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationKeys;
import com.enonic.xp.app.ApplicationWildcardMatcher;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.mixin.MixinDescriptor;
import com.enonic.xp.schema.mixin.MixinName;
import com.enonic.xp.schema.mixin.MixinService;
import com.enonic.xp.site.CmsService;
import com.enonic.xp.site.MixinMappingService;
import com.enonic.xp.site.MixinMappings;
import com.enonic.xp.site.MixinOption;
import com.enonic.xp.site.MixinOptions;

import static com.google.common.base.Strings.nullToEmpty;

@Component
public final class MixinMappingServiceImpl
    implements MixinMappingService
{
    private final CmsService cmsService;

    private final MixinService mixinService;

    @Activate
    public MixinMappingServiceImpl( @Reference CmsService cmsService, @Reference MixinService mixinService )
    {
        this.cmsService = cmsService;
        this.mixinService = mixinService;
    }

    @Override
    public MixinOptions getMixinMappingOptions( final ContentTypeName type, final ApplicationKeys applicationKeys )
    {
        if ( applicationKeys.isEmpty() )
        {
            return MixinOptions.empty();
        }

        return getMixinOptionsByApps( applicationKeys, type );
    }

    private MixinOptions getMixinOptionsByApps( final ApplicationKeys applicationKeys, final ContentTypeName type )
    {
        final MixinMappings.Builder builder = MixinMappings.create();

        applicationKeys.stream()
            .map( cmsService::getDescriptor )
            .filter( Objects::nonNull )
            .forEach( siteDescriptor -> builder.addAll( siteDescriptor.getMixinMappings() ) );

        return getMixinOptionsByContentType( builder.build(), type );
    }

    private MixinOptions getMixinOptionsByContentType( final MixinMappings mixinMappings, final ContentTypeName contentTypeName )
    {
        final Map<MixinName, MixinOption> result = mixinMappings.stream()
            .filter( mixinMapping -> {
                final String pattern = mixinMapping.getAllowContentTypes();
                final ApplicationKey applicationKey = mixinMapping.getMixinName().getApplicationKey();
                return nullToEmpty( pattern ).isBlank() || new ApplicationWildcardMatcher<>( applicationKey, ContentTypeName::toString,
                                                                                             ApplicationWildcardMatcher.Mode.MATCH ).matches(
                    pattern, contentTypeName );
            } )
            .map( mixinMapping -> {
                final MixinDescriptor mixinDescriptor = this.mixinService.getByName( mixinMapping.getMixinName() );
                if ( mixinDescriptor == null )
                {
                    if ( !mixinMapping.isOptional() )
                    {
                        throw new IllegalStateException( "Mixin '" + mixinMapping.getMixinName() + "' not found" );
                    }
                    return null;
                }
                return Map.entry( mixinDescriptor.getName(), new MixinOption( mixinDescriptor, mixinMapping.isOptional() ) );
            } )
            .filter( Objects::nonNull )
            .collect(
                Collectors.toMap( Map.Entry::getKey, Map.Entry::getValue, ( v1, v2 ) -> v1.optional() ? v2 : v1, LinkedHashMap::new ) );

        return result.values().stream().collect( MixinOptions.collector() );
    }
}
