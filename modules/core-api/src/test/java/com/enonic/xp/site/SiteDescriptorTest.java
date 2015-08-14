package com.enonic.xp.site;

import java.util.List;

import org.junit.Test;

import com.google.common.collect.Lists;

import com.enonic.xp.form.Form;
import com.enonic.xp.form.FormItem;
import com.enonic.xp.form.Input;
import com.enonic.xp.inputtype.InputTypeName;
import com.enonic.xp.schema.mixin.MixinName;
import com.enonic.xp.schema.mixin.MixinNames;

import static org.junit.Assert.*;

public class SiteDescriptorTest
{
    @Test
    public void create_empty_site_descriptor()
    {
        //Builds an empty SiteDescriptor
        SiteDescriptor siteDescriptor = SiteDescriptor.create().build();
        assertEquals( null, siteDescriptor.getForm() );
        assertEquals( null, siteDescriptor.getMetaSteps() );
    }

    @Test
    public void create_site_descriptor()
    {
        //Builds a Form
        final FormItem formItem = Input.create().
            name( "input" ).
            label( "Input" ).
            inputType( InputTypeName.DOUBLE ).
            build();

        final Form form = Form.create().
            addFormItem( formItem ).
            build();

        //Builds MixinNames
        List<MixinName> mixinNameList = Lists.newArrayList();
        mixinNameList.add( MixinName.from( "myapplication:my" ) );
        MixinNames metaSteps = MixinNames.from( mixinNameList );

        //Builds a SiteDescriptor
        SiteDescriptor siteDescriptor = SiteDescriptor.create().
            form( form ).
            metaSteps( metaSteps ).
            build();
        assertEquals( form, siteDescriptor.getForm() );
        assertEquals( metaSteps, siteDescriptor.getMetaSteps() );
    }

}
