package com.enonic.xp.lib.schema;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.FormItem;
import com.enonic.xp.form.Input;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.resource.DynamicSchemaResult;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.schema.xdata.MixinName;
import com.enonic.xp.site.CmsDescriptor;
import com.enonic.xp.site.MixinMapping;
import com.enonic.xp.site.MixinMappings;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GetDynamicSiteHandlerTest
    extends BaseSchemaHandlerTest
{
    @Test
    void testSite()
    {
        when( dynamicSchemaService.getCmsDescriptor( isA( ApplicationKey.class ) ) ).thenAnswer( params -> {
            final ApplicationKey applicationKey = params.getArgument( 0, ApplicationKey.class );

            final FormItem formItem = Input.create().name( "input" ).label( "Input" ).inputType( InputTypeName.DOUBLE ).build();

            final Form form = Form.create().addFormItem( formItem ).build();

            List<MixinMapping> xDataMappingList = new ArrayList<>();
            xDataMappingList.add( MixinMapping.create().mixinName( MixinName.from( "myapplication:my" ) ).build() );
            MixinMappings xDataMappings = MixinMappings.from( xDataMappingList );

            CmsDescriptor cmsDescriptor = CmsDescriptor.create()
                .applicationKey( applicationKey )
                .modifiedTime( Instant.parse( "2021-02-25T10:44:33.170079900Z" ) )
                .form( form )
                .mixinMappings( xDataMappings )
                .build();

            final Resource resource = mock( Resource.class );
            when( resource.readString() ).thenReturn( "<site><some-data></some-data></site>" );

            return new DynamicSchemaResult<>( cmsDescriptor, resource );
        } );

        runScript( "/lib/xp/examples/schema/getSite.js" );
    }


    @Test
    void testNull()
    {
        runFunction( "/test/GetDynamicSiteHandlerTest.js", "getNull" );
    }

}
