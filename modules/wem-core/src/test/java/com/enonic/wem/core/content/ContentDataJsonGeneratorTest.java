package com.enonic.wem.core.content;


import org.junit.Test;

import com.enonic.wem.core.content.config.ContentType;
import com.enonic.wem.core.content.config.field.ConfigItems;
import com.enonic.wem.core.content.config.field.Field;
import com.enonic.wem.core.content.config.field.RadioButtonsConfig;
import com.enonic.wem.core.content.config.field.type.BuiltInFieldTypes;

public class ContentDataJsonGeneratorTest
{
    @Test
    public void asdfsdf()
    {
        ContentType contentType = new ContentType();
        contentType.setName( "MyContentType" );
        ConfigItems configItems = contentType.getConfigItems();
        configItems.addField( Field.newBuilder().name( "myTextarea" ).type( BuiltInFieldTypes.textarea ).required( true ).build() );
        configItems.addField( Field.newBuilder().name( "myPhone" ).type( BuiltInFieldTypes.phone ).build() );

        ContentData contentData = new ContentData( configItems );
        contentData.setFieldValue( "myTextarea", "My test\n text." );
        contentData.setFieldValue( "myPhone", "+4712123123" );

        ContentDataJsonGenerator generator = new ContentDataJsonGenerator();
        String json = generator.toJson( contentData );

        System.out.println( json );
    }

    @Test
    public void radiobuttons()
    {
        RadioButtonsConfig radioButtonsConfig =
            RadioButtonsConfig.newBuilder().addOption( "Norway", "NO" ).addOption( "South Africa", "ZA" ).build();

        ConfigItems configItems = new ConfigItems();
        configItems.addField(
            Field.newBuilder().name( "myRadiobuttons" ).type( BuiltInFieldTypes.radioButtons ).required( true ).fieldConfig(
                radioButtonsConfig ).build() );

        ContentData contentData = new ContentData( configItems );
        contentData.setFieldValue( "myRadiobuttons", "Norway" );

        ContentDataJsonGenerator generator = new ContentDataJsonGenerator();
        String json = generator.toJson( contentData );
        System.out.println( json );

        ContentData parsedContentData = generator.toContentData( json, configItems );

        System.out.println( parsedContentData );
    }
}
