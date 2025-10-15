package com.enonic.xp.core.impl.content;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.content.ExtraData;
import com.enonic.xp.content.ExtraDatas;
import com.enonic.xp.content.XDataDefaultValuesProcessor;
import com.enonic.xp.form.Form;
import com.enonic.xp.form.FormDefaultValuesProcessor;
import com.enonic.xp.schema.xdata.XData;
import com.enonic.xp.schema.xdata.XDataName;
import com.enonic.xp.schema.xdata.XDataService;

@Component
public final class XDataDefaultValuesProcessorImpl
    implements XDataDefaultValuesProcessor
{
    private final XDataService xDataService;

    private final FormDefaultValuesProcessor formDefaultValuesProcessor;

    @Activate
    public XDataDefaultValuesProcessorImpl( @Reference final XDataService xDataService,
                                            @Reference final FormDefaultValuesProcessor formDefaultValuesProcessor )
    {
        this.xDataService = xDataService;
        this.formDefaultValuesProcessor = formDefaultValuesProcessor;
    }

    public void applyDefaultValues( final ExtraDatas extraDatas )
    {
        if ( extraDatas == null )
        {
            return;
        }

        for ( XDataName xDataName : extraDatas.getNames() )
        {
            final XData xData = xDataService.getByName( xDataName );

            if ( xData != null && xData.getForm() != null )
            {
                final Form form = xData.getForm();
                final ExtraData extraData = extraDatas.getMetadata( xDataName );

                formDefaultValuesProcessor.setDefaultValues( form, extraData.getData() );
            }
        }
    }


}
