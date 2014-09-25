package com.enonic.wem.core.schema.mixin;


import java.util.List;

import org.junit.Test;

import com.enonic.wem.api.form.Form;
import com.enonic.wem.api.form.MixinReference;
import com.enonic.wem.api.form.inputtype.InputTypes;
import com.enonic.wem.api.schema.mixin.Mixin;
import com.enonic.wem.api.schema.mixin.MixinName;

import static com.enonic.wem.api.form.Input.newInput;
import static com.enonic.wem.api.schema.mixin.Mixin.newMixin;
import static junit.framework.Assert.assertEquals;

public class MixinUsageResolverTest
{
    @Test
    public void resolveUsingMixinReferences_given_one_FormItem_being_matching_MixinReference_then_List_with_size_one_is_returned()
    {
        Mixin mixin = newMixin().name( "mymodule:my-mixin" ).addFormItem(
            newInput().inputType( InputTypes.TEXT_LINE ).name( "my-input-to-mixin" ).build() ).build();

        MixinUsageResolver mixinUsageResolver = new MixinUsageResolver( mixin );

        Form form = Form.newForm().
            addFormItem( MixinReference.newMixinReference().name( "my-mixin-reference" ).mixin( mixin ).build() ).
            build();

        // exercise
        List<MixinReference> resolvedReferences = mixinUsageResolver.resolveUsingMixinReferences( form );

        // verify
        assertEquals( 1, resolvedReferences.size() );
    }

    @Test
    public void resolveUsingMixinReferences_given_two_FormItems_and_one_matching_MixinReference_then_List_with_size_one_is_returned()
    {
        Mixin mixin = newMixin().name( "mymodule:my-mixin" ).addFormItem(
            newInput().inputType( InputTypes.TEXT_LINE ).name( "my-input-to-mixin" ).build() ).build();

        MixinUsageResolver mixinUsageResolver = new MixinUsageResolver( mixin );

        Form form = Form.newForm().
            addFormItem( MixinReference.newMixinReference().name( "my-mixin-reference" ).mixin( mixin ).build() ).
            addFormItem( newInput().inputType( InputTypes.TEXT_LINE ).name( "my-text-line" ).build() ).
            build();

        // exercise
        List<MixinReference> resolvedReferences = mixinUsageResolver.resolveUsingMixinReferences( form );

        // verify
        assertEquals( 1, resolvedReferences.size() );
    }

    @Test
    public void resolveUsingMixinReferences_given_two_MixinReferences_and_one_is_matching_MixinReferences_then_List_with_size_one_is_returned()
    {
        Mixin mixin = newMixin().name( "mymodule:my-mixin" ).addFormItem(
            newInput().inputType( InputTypes.TEXT_LINE ).name( "my-input-to-mixin" ).build() ).build();

        Mixin otherMixin = newMixin().name( "mymodule:my-other-mixin" ).addFormItem(
            newInput().inputType( InputTypes.TEXT_LINE ).name( "my-input-to-other-mixin" ).build() ).build();

        MixinUsageResolver mixinUsageResolver = new MixinUsageResolver( mixin );

        Form form = Form.newForm().
            addFormItem( MixinReference.newMixinReference().name( "my-mixin-reference" ).mixin( mixin ).build() ).
            addFormItem( MixinReference.newMixinReference().name( "my-other-reference" ).mixin( otherMixin ).build() ).
            build();

        // exercise
        List<MixinReference> resolvedReferences = mixinUsageResolver.resolveUsingMixinReferences( form );

        // verify
        assertEquals( 1, resolvedReferences.size() );
        assertEquals( MixinName.from( "mymodule:my-mixin" ), resolvedReferences.get( 0 ).getMixinName() );
    }
}
