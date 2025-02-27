package com.enonic.xp.core.impl.form;

import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.enonic.xp.core.internal.json.ObjectMapperHelper;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.PropertyTreeMarshallerService;
import com.enonic.xp.schema.mixin.MixinService;

@Component(immediate = true)
public class PropertyTreeMarshallerServiceImpl
    implements PropertyTreeMarshallerService
{
    private static final ObjectMapper MAPPER = ObjectMapperHelper.create();

    private final MixinService mixinService;

    @Activate
    public PropertyTreeMarshallerServiceImpl( @Reference final MixinService mixinService )
    {
        this.mixinService = mixinService;
    }

    @Override
    public PropertyTree marshal( final Map<String, ?> values )
    {
        return marshal( values, Form.create().build(), false );
    }

    @Override
    public PropertyTree marshal( final Map<String, ?> values, final Form form, final boolean strict )
    {
        return new FormJsonToPropertyTreeTranslator( inlineMixins( form ), strict ).
            translate( MAPPER.valueToTree( values ) );
    }

    private Form inlineMixins( final Form form )
    {
        return mixinService.inlineFormItems( form );
    }
}
