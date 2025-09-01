package com.enonic.xp.core.impl.schema.mapper;

import com.enonic.xp.form.Input;
import com.enonic.xp.inputtype.InputTypeName;

public class AttachmentUploaderYml
    extends InputYml
{
    @Override
    public InputTypeName getInputTypeName()
    {
        return InputTypeName.ATTACHMENT_UPLOADER;
    }

    @Override
    public void customizeInputType( final Input.Builder builder )
    {
        // do nothing
    }
}
