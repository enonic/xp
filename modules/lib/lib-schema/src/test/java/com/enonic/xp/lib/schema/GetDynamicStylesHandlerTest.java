package com.enonic.xp.lib.schema;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.resource.DynamicSchemaResult;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.schema.xdata.MixinName;
import com.enonic.xp.site.MixinMapping;
import com.enonic.xp.style.ImageStyle;
import com.enonic.xp.style.StyleDescriptor;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GetDynamicStylesHandlerTest
    extends BaseSchemaHandlerTest
{
    @Test
    void testStyles()
    {
        when( dynamicSchemaService.getStyles( isA( ApplicationKey.class ) ) ).thenAnswer( params -> {
            final ApplicationKey applicationKey = params.getArgument( 0, ApplicationKey.class );

            final List<MixinMapping> xDataMappingList = new ArrayList<>();
            xDataMappingList.add( MixinMapping.create().mixinName( MixinName.from( "myapplication:my" ) ).build() );

            StyleDescriptor styleDescriptor = StyleDescriptor.create()
                .application( applicationKey )
                .addStyleElement( ImageStyle.create()
                                      .displayName( "Style display name" )
                                      .name( "mystyle" )
                                      .displayNameI18nKey( "style.display" )
                                      .build() )
                .cssPath( "assets/styles.css" )
                .modifiedTime( Instant.parse( "2021-02-25T10:44:33.170079900Z" ) )
                .build();

            final Resource resource = mock( Resource.class );
            when( resource.readString() ).thenReturn( "<styles><some-data></some-data></styles>" );

            return new DynamicSchemaResult<>( styleDescriptor, resource );
        } );

        runScript( "/lib/xp/examples/schema/getStyles.js" );
    }


    @Test
    void testNull()
    {
        runFunction( "/test/GetDynamicStylesHandlerTest.js", "getNull" );
    }

}
