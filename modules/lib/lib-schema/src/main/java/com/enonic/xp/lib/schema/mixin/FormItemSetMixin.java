package com.enonic.xp.lib.schema.mixin;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import com.enonic.xp.form.FormItemSet;

@JsonDeserialize(builder = FormItemSet.Builder.class)
public abstract class FormItemSetMixin
{
}
