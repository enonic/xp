package com.enonic.xp.core.impl.schema.mapper;

import com.enonic.xp.inputtype.InputTypeName;

public class AttachmentUploaderYml
    extends InputYml
{
    public static final InputTypeName INPUT_TYPE_NAME = InputTypeName.ATTACHMENT_UPLOADER;

    public AttachmentUploaderYml()
    {
        super( INPUT_TYPE_NAME );
    }
}
