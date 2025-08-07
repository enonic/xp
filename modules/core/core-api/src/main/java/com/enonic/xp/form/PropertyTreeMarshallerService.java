package com.enonic.xp.form;

import java.util.Map;

import com.enonic.xp.data.PropertyTree;

public interface PropertyTreeMarshallerService
{
    PropertyTree marshal( Map<String, ?> values, Form form, boolean strict );
}
