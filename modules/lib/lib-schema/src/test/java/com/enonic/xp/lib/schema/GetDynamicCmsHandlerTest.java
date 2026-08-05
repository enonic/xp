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
import com.enonic.xp.schema.mixin.MixinName;
import com.enonic.xp.site.CmsDescriptor;
import com.enonic.xp.site.MixinMapping;
import com.enonic.xp.site.MixinMappings;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GetDynamicCmsHandlerTest
    extends BaseSchemaHandlerTest
{
    @Test
    void testCms()
    {
        when( schemaService.getCmsDescriptor( isA( ApplicationKey.class ) ) ).thenAnswer( params -> {
            final ApplicationKey applicationKey = params.getArgument( 0, ApplicationKey.class );

            final FormItem formItem = Input.create().name( "input" ).label( "Input" ).inputType( InputTypeName.DOUBLE ).build();

            final Form form = Form.create().addFormItem( formItem ).build();

            List<MixinMapping> mixinMappings = new ArrayList<>();
            mixinMappings.add( MixinMapping.create().mixinName( MixinName.from( "myapplication:my" ) ).build() );
            MixinMappings mappings = MixinMappings.from( mixinMappings );

            CmsDescriptor cmsDescriptor = CmsDescriptor.create()
                .applicationKey( applicationKey )
                .modifiedTime( Instant.parse( "2021-02-25T10:44:33.170079900Z" ) )
                .form( form )
                .mixinMappings( mappings )
                .build();

            final Resource resource = mock( Resource.class );
            when( resource.readString() ).thenReturn( "kind: \"CMS\"\n" + "mixins:\n" + "- name: \"myapplication:my\"\n" +
                                                          "  optional: false\n" + "form:\n" + "- type: \"Double\"\n" +
                                                          "  name: \"input\"\n" + "  label: \"Input\"\n" + "  occurrences:\n" +
                                                          "    min: 0\n" + "    max: 1" );

            return new DynamicSchemaResult<>( cmsDescriptor, resource );
        } );

        runScript( "/lib/xp/examples/schema/getCms.js" );
    }


    @Test
    void testNull()
    {
        runFunction( "/test/GetDynamicCmsHandlerTest.js", "getNull" );
    }

}
