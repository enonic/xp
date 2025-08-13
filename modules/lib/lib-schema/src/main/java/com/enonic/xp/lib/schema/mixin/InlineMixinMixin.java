package com.enonic.xp.lib.schema.mixin;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import com.enonic.xp.form.InlineMixin;

@JsonDeserialize(builder = InlineMixin.Builder.class)
public abstract class InlineMixinMixin
{
}
