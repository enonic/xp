package com.enonic.wem.core.content.type;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.content.type.ContentType;
import com.enonic.wem.api.content.type.form.FormItemSet;
import com.enonic.wem.api.content.type.form.Input;
import com.enonic.wem.api.module.Module;

import static com.enonic.wem.api.command.Commands.contentType;
import static com.enonic.wem.api.content.type.ContentType.newContentType;
import static com.enonic.wem.api.content.type.form.FormItemSet.newFormItemSet;
import static com.enonic.wem.api.content.type.form.Input.newInput;
import static com.enonic.wem.api.content.type.form.inputtype.InputTypes.TEXT_AREA;
import static com.enonic.wem.api.content.type.form.inputtype.InputTypes.TEXT_LINE;

@Component
public class ContentTypesInitializer
{
    private static final ContentType CONTENT = createSystemType( "Content", "content", false );

    private static final ContentType SPACE = createSystemType( "Space", "space", true );

    private static final ContentType STRUCTURED = createSystemType( "Structured", "structured", false );

    private static final ContentType UNSTRUCTURED = createSystemType( "Unstructured", "unstructured", false );

    private static final ContentType FOLDER = createSystemType( "Folder", "folder", false );

    private static final ContentType PAGE = createSystemType( "Page", "page", true );

    private static final ContentType SHORTCUT = createSystemType( "Shortcut", "shortcut", true );

    private static final ContentType FILE = createSystemType( "File", "file", true );

    private static final ContentType[] SYSTEM_TYPES = {CONTENT, SPACE, STRUCTURED, UNSTRUCTURED, FOLDER, PAGE, SHORTCUT, FILE};

    private Client client;

    public void createSystemTypes()
    {
        for ( final ContentType contentType : SYSTEM_TYPES )
        {
            client.execute( contentType().create().contentType( contentType ) );
        }

        addTestContentTypes();
    }

    private void addTestContentTypes()
    {
        final Input title =
            newInput().name( "title" ).type( TEXT_LINE ).label( "Title" ).required( true ).helpText( "Article title" ).build();
        final Input category =
            newInput().name( "preface" ).type( TEXT_LINE ).label( "Preface" ).helpText( "Preface of the article" ).build();
        final Input body =
            newInput().name( "body" ).type( TEXT_AREA ).label( "Body" ).required( true ).helpText( "Body of the article" ).build();
        final ContentType articleContentType = newContentType().
            module( Module.newModule().name( "News" ).build() ).
            name( "Article" ).
            addFormItem( title ).
            addFormItem( category ).
            addFormItem( body ).
            build();
        client.execute( contentType().create().contentType( articleContentType ) );

        final FormItemSet formItemSet = newFormItemSet().name( "related" ).build();
        formItemSet.add( newInput().name( "author" ).label( "Author" ).type( TEXT_LINE ).build() );
        formItemSet.add( newInput().name( "category" ).label( "Category" ).type( TEXT_LINE ).build() );
        final ContentType article2ContentType = newContentType().
            module( Module.newModule().name( "News" ).build() ).
            name( "Article2" ).
            addFormItem( title.copy() ).
            addFormItem( category.copy() ).
            addFormItem( body.copy() ).
            addFormItem( formItemSet ).
            build();
        client.execute( contentType().create().contentType( article2ContentType ) );
    }

    private static ContentType createSystemType( final String displayName, final String name, final boolean isFinal )
    {
        final ContentType contentType = newContentType().
            module( Module.SYSTEM ).
            name( name ).
            displayName( displayName ).
            setFinal( isFinal ).
            build();
        return contentType;
    }

    @Autowired
    public void setClient( final Client client )
    {
        this.client = client;
    }
}
