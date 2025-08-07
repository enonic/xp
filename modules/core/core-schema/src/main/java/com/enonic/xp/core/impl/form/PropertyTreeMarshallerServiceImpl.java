package com.enonic.xp.core.impl.form;

import java.util.Map;

import org.osgi.service.component.annotations.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.enonic.xp.core.internal.json.ObjectMapperHelper;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.PropertyTreeMarshallerService;

@Component(immediate = true)
public class PropertyTreeMarshallerServiceImpl
    implements PropertyTreeMarshallerService
{
    private static final ObjectMapper MAPPER = ObjectMapperHelper.create();

    @Override
    public PropertyTree marshal( final Map<String, ?> values, final Form form, final boolean strict )
    {
        return new FormJsonToPropertyTreeTranslator( form, strict ).translate( MAPPER.valueToTree( values ) );
    }
}
