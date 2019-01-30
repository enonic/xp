package com.enonic.xp.admin.impl.json.form;

import com.google.common.annotations.Beta;

import com.enonic.xp.form.FormItem;

@Beta
public abstract class NamedFormItemJson<T extends FormItem>
    extends FormItemJson<T>
{
    public abstract String getName();
}
