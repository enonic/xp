package com.enonic.xp.core.impl.schema.mixin;

import com.enonic.xp.form.Form;
import com.enonic.xp.media.MediaInfo;
import com.enonic.xp.schema.mixin.Mixin;

class ExtractedTextMixin
    extends BuiltinMixinsTypes
{
    static final Mixin EXTRACTED_TEXT_INFO_MIXIN = Mixin.create().
        name( MediaInfo.EXTRACTED_TEXT_MIXIN_NAME ).
        displayName( "Text Content" ).
        form( createExtractedTextMixinForm() ).
        build();

    private static Form createExtractedTextMixinForm()
    {
        final Form.Builder form = Form.create();
        form.addFormItem( createTextArea( MediaInfo.EXTRACTED_TEXT_CONTENT, "Content" ).occurrences( 0, 1 ).immutable( true ).build() );
        return form.build();
    }

}