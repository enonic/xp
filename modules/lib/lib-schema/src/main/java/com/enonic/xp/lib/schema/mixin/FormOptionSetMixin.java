package com.enonic.xp.lib.schema.mixin;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import com.enonic.xp.form.FormOptionSet;

@JsonDeserialize(builder = FormOptionSet.Builder.class)
public abstract class FormOptionSetMixin
{
}
