package com.enonic.xp.form;

import com.enonic.xp.data.PropertyTree;

public interface FormDefaultValuesProcessor
{
    void setDefaultValues( Form form, PropertyTree data );
}
