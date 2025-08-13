package com.enonic.xp.lib.schema;

import org.junit.jupiter.api.Test;

import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ContentTypeParserTest
{
    @Test
    void testParse()
        throws Exception
    {
        String yaml = """
            name: "myapp:article"
            superType: "base:structured"
            isAbstract: false
            isFinal: false
            allowChildContent: true
            isBuiltIn: false
            displayName: "Article"
            displayNameI18nKey: "i18n.article.displayName"
            description: "Article Description"
            descriptionI18nKey: "i18n.article.description"
            displayNameLabel: "Article heading"
            displayNameLabelI18nKey: "article.title"
            displayNameExpression: "${expression}"
            form:
              - type: "Input"
                name: "field1"
                label: "Field 1"
                inputType: "TextLine"
                labelI18nKey: "i18n.label"
                immutable: true
                occurrences:      
                  minimum: 0
                  maximum: 1
                indexed: true
                customText: "Custom Text"
                validationRegexp: "myRegexp"
                helpText: "helpText"
                helpTextI18nKey: "helpTextI18nKey" 
                maximizeUIInputWidth: false
              
              - type: "FieldSet"
                label: "FieldSet Label"
                labelI18nKey: "i18n.fieldset.label"
                items:
                  - type: "Input"
                    name: "description"
                    label: "Description"
                    inputType: "TextArea"
              
              - type: "InlineMixin"
                mixin: "myapp:mymixin"
              
              - type: "ItemSet"
                name: "Panel"
                label: "My Panel"
                items:
                  - type: "Input"
                    name: "description"
                    label: "Description"
                    inputType: "TextArea"
              
              - type: "OptionSet"
                name: "radioOptionSet"
                label: "Single selection"
                helpText: "Help Text"
                expanded: false
                occurrences:
                  minimum: 1
                  maximum: 0
                multiselection:
                  minimum: 1
                  maximum: 1
                options:
                  - type: "OptionSetOption"
                    name: "option_1"
                    label: "Option 1"
                    defaultOption: false
                    items:
                      - type: "Input"
                        inputType: "TextLine"
                        name: "text-input"
                        label: "Name"
                        helpText: "Text input"
//                        defaultValue:
//                          property:
//                            name: default
//                            value: something
                        occurrences: 
                          minimum: 1
                          maximum: 1
                      - type: "ItemSet"
                        name: "minimum3"
                        label: "Minimum 3"
                        occurrences:
                          minimum: 3
                          maximum: 0
                        items:
                         - type: "Input"
                           name: "label"
                           inputType: "TextLine"
                           label: "Label"
                           occurrences:
                             minimum: 0
                             maximum: 1
                         - type: "Input"
                           name: "value"
                           inputType: "TextLine"
                           label: "Value"
                           occurrences:
                             minimum: 0
                             maximum: 1
                  - type: "OptionSetOption"
                    name: "option_2"
                    label: "Option 2"   
                    defaultOption: false     
            """;

        ContentTypeParser parser = new ContentTypeParser();
        ContentType contentType = parser.parse( yaml );

        assertEquals( ContentTypeName.from( "myapp:article" ), contentType.getName() );
        assertEquals( ContentTypeName.from( "base:structured" ), contentType.getSuperType() );
        assertFalse( contentType.isAbstract() );
        assertFalse( contentType.isFinal() );
        assertTrue( contentType.allowChildContent() );
        assertFalse( contentType.isBuiltIn() );
        assertEquals( "Article heading", contentType.getDisplayNameLabel() );
        assertEquals( "article.title", contentType.getDisplayNameLabelI18nKey() );
        assertEquals( "${expression}", contentType.getDisplayNameExpression() );
    }
}
