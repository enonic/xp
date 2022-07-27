package com.enonic.xp.lib.schema;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.resource.DynamicSchemaResult;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.schema.xdata.XDataName;
import com.enonic.xp.site.XDataMapping;
import com.enonic.xp.style.GenericStyle;
import com.enonic.xp.style.StyleDescriptor;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GetDynamicStylesHandlerTest
    extends BaseSchemaHandlerTest
{
    @Test
    public void testStyles()
    {
        when( dynamicSchemaService.getStyles( isA( ApplicationKey.class ) ) ).thenAnswer( params -> {
            final ApplicationKey applicationKey = params.getArgument( 0, ApplicationKey.class );

            final List<XDataMapping> xDataMappingList = new ArrayList<>();
            xDataMappingList.add( XDataMapping.create().xDataName( XDataName.from( "myapplication:my" ) ).build() );

            StyleDescriptor styleDescriptor = StyleDescriptor.create()
                .application( applicationKey )
                .addStyleElement( GenericStyle.create()
                                      .displayName( "Style display name" )
                                      .name( "mystyle" )
                                      .displayNameI18nKey( "style.display" )
                                      .build() )
                .cssPath( "assets/styles.css" )
                .modifiedTime( Instant.parse( "2021-02-25T10:44:33.170079900Z" ) )
                .build();

            final Resource resource = mock( Resource.class );
            when( resource.readString() ).thenReturn( "<styles><some-data></some-data></styles>" );

            return new DynamicSchemaResult<StyleDescriptor>( styleDescriptor, resource );
        } );

        runScript( "/lib/xp/examples/schema/getStyles.js" );
    }


    @Test
    public void testNull()
    {
        runFunction( "/test/GetDynamicStylesHandlerTest.js", "getNull" );
    }

}
