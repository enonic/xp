package com.enonic.xp.lib.schema.mixin;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import com.enonic.xp.form.FieldSet;

@JsonDeserialize(builder = FieldSet.Builder.class)
public abstract class FieldSetMixin
{
}
