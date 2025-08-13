package com.enonic.xp.lib.schema.mixin;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import com.enonic.xp.schema.content.ContentType;

@JsonDeserialize(builder = ContentType.Builder.class)
public abstract class ContentTypeMixin
{
}
