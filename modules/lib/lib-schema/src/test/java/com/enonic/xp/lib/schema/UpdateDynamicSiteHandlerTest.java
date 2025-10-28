package com.enonic.xp.lib.schema;

import java.time.Instant;

import org.junit.jupiter.api.Test;

import com.enonic.xp.resource.DynamicSchemaResult;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.UpdateDynamicSiteParams;
import com.enonic.xp.site.SiteDescriptor;
import com.enonic.xp.xml.parser.XmlSiteParser;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UpdateDynamicSiteHandlerTest
    extends BaseSchemaHandlerTest
{
    @Test
    void testSite()
    {
        when( dynamicSchemaService.updateSite( isA( UpdateDynamicSiteParams.class ) ) ).thenAnswer( params -> {
            final UpdateDynamicSiteParams siteParams = params.getArgument( 0, UpdateDynamicSiteParams.class );

            final XmlSiteParser parser = new XmlSiteParser();

            SiteDescriptor.Builder builder = SiteDescriptor.create();

            final Instant modifiedTime = Instant.parse( "2021-09-25T10:00:00.00Z" );
            builder.modifiedTime( modifiedTime );

            parser.siteDescriptorBuilder( builder );
            builder.applicationKey( siteParams.getKey() );

            parser.source( siteParams.getResource() );
            parser.currentApplication( siteParams.getKey() );

            parser.parse();

            final Resource resource = mock( Resource.class );
            when( resource.readString() ).thenReturn( siteParams.getResource() );

            return new DynamicSchemaResult<SiteDescriptor>( builder.build(), resource );
        } );

        runScript( "/lib/xp/examples/schema/updateSite.js" );
    }

    @Test
    void testInvalidSite()
    {
        runFunction( "/test/UpdateDynamicSiteHandlerTest.js", "updateInvalidSite" );
    }
}
