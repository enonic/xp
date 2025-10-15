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
import com.enonic.xp.schema.xdata.XData;
import com.enonic.xp.schema.xdata.XDataName;
import com.enonic.xp.schema.xdata.XDataService;
import com.enonic.xp.site.SiteService;
import com.enonic.xp.site.XDataMappingService;
import com.enonic.xp.site.XDataMappings;
import com.enonic.xp.site.XDataOption;
import com.enonic.xp.site.XDataOptions;

import static com.google.common.base.Strings.nullToEmpty;

@Component
public final class XDataMappingServiceImpl
    implements XDataMappingService
{
    private final SiteService siteService;

    private final XDataService xDataService;

    @Activate
    public XDataMappingServiceImpl( @Reference SiteService siteService, @Reference XDataService xDataService )
    {
        this.siteService = siteService;
        this.xDataService = xDataService;
    }

    @Override
    public XDataOptions getXDataMappingOptions( final ContentTypeName type, final ApplicationKeys applicationKeys )
    {
        if ( applicationKeys.isEmpty() )
        {
            return XDataOptions.empty();
        }

        return getXDataByApps( applicationKeys, type );
    }

    private XDataOptions getXDataByApps( final ApplicationKeys applicationKeys, final ContentTypeName type )
    {
        final XDataMappings.Builder builder = XDataMappings.create();

        applicationKeys.stream()
            .map( siteService::getDescriptor )
            .filter( Objects::nonNull )
            .forEach( siteDescriptor -> builder.addAll( siteDescriptor.getXDataMappings() ) );

        return getXDatasByContentType( builder.build(), type );
    }

    private XDataOptions getXDatasByContentType( final XDataMappings xDataMappings, final ContentTypeName contentTypeName )
    {
        final Map<XDataName, XDataOption> result = xDataMappings.stream()
            .filter( xDataMapping -> {
                final String pattern = xDataMapping.getAllowContentTypes();
                final ApplicationKey applicationKey = xDataMapping.getXDataName().getApplicationKey();
                return nullToEmpty( pattern ).isBlank() || new ApplicationWildcardMatcher<>( applicationKey, ContentTypeName::toString,
                                                                                             ApplicationWildcardMatcher.Mode.MATCH ).matches(
                    pattern, contentTypeName );
            } )
            .map( xDataMapping -> {
                final XData xData = this.xDataService.getByName( xDataMapping.getXDataName() );
                if ( xData == null )
                {
                    if ( !xDataMapping.getOptional() )
                    {
                        throw new IllegalStateException( "XData '" + xDataMapping.getXDataName() + "' not found" );
                    }
                    return null;
                }
                return Map.entry( xData.getName(), new XDataOption( xData, xDataMapping.getOptional() ) );
            } )
            .filter( Objects::nonNull )
            .collect(
                Collectors.toMap( Map.Entry::getKey, Map.Entry::getValue, ( v1, v2 ) -> v1.optional() ? v2 : v1, LinkedHashMap::new ) );

        return result.values().stream().collect( XDataOptions.collector() );
    }
}
