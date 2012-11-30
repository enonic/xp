package com.enonic.wem.core.content.type;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.content.type.ContentType;
import com.enonic.wem.api.content.type.QualifiedContentTypeNames;
import com.enonic.wem.api.content.type.form.FormItemSet;
import com.enonic.wem.api.content.type.form.Input;
import com.enonic.wem.api.module.Module;
import com.enonic.wem.api.module.ModuleName;

import static com.enonic.wem.api.command.Commands.contentType;
import static com.enonic.wem.api.content.type.ContentType.newContentType;
import static com.enonic.wem.api.content.type.form.FormItemSet.newFormItemSet;
import static com.enonic.wem.api.content.type.form.Input.newInput;
import static com.enonic.wem.api.content.type.form.inputtype.InputTypes.TEXT_AREA;
import static com.enonic.wem.api.content.type.form.inputtype.InputTypes.TEXT_LINE;

@Component
public class ContentTypesInitializer
{
    private static final ContentType CONTENT = createSystemType( "Content", "content", false, true );

    private static final ContentType SPACE = createSystemType( "Space", "space", true, false );

    private static final ContentType STRUCTURED = createSystemType( "Structured", "structured", false, true );

    private static final ContentType UNSTRUCTURED = createSystemType( "Unstructured", "unstructured", false, false );

    private static final ContentType FOLDER = createSystemType( "Folder", "folder", false, false );

    private static final ContentType PAGE = createSystemType( "Page", "page", true, false );

    private static final ContentType SHORTCUT = createSystemType( "Shortcut", "shortcut", true, false );

    private static final ContentType FILE = createSystemType( "File", "file", true, false );

    private static final ContentType[] SYSTEM_TYPES = {CONTENT, SPACE, STRUCTURED, UNSTRUCTURED, FOLDER, PAGE, SHORTCUT, FILE};

    private Client client;

    public void createSystemTypes()
    {
        for ( final ContentType contentType : SYSTEM_TYPES )
        {
            addContentType( contentType );
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
            module( ModuleName.from( "News" ) ).
            name( "Article" ).
            superType( CONTENT.getQualifiedName() ).
            addFormItem( title ).
            addFormItem( category ).
            addFormItem( body ).
            build();
        addContentType( articleContentType );

        final FormItemSet formItemSet = newFormItemSet().name( "related" ).build();
        formItemSet.add( newInput().name( "author" ).label( "Author" ).type( TEXT_LINE ).build() );
        formItemSet.add( newInput().name( "category" ).label( "Category" ).type( TEXT_LINE ).build() );
        final ContentType article2ContentType = newContentType().
            module( ModuleName.from( "News" ) ).
            name( "Article2" ).
            superType( CONTENT.getQualifiedName() ).
            addFormItem( title.copy() ).
            addFormItem( category.copy() ).
            addFormItem( body.copy() ).
            addFormItem( formItemSet ).
            build();
        addContentType( article2ContentType );
    }

    private void addContentType( final ContentType contentType )
    {
        final QualifiedContentTypeNames qualifiedNames = QualifiedContentTypeNames.from( contentType.getQualifiedName() );
        final boolean contentTypeExists = !client.execute( contentType().get().names( qualifiedNames ) ).isEmpty();
        if ( !contentTypeExists )
        {
            client.execute( contentType().create().contentType( contentType ) );
        }
    }

    private static ContentType createSystemType( final String displayName, final String name, final boolean isFinal,
                                                 final boolean isAbstract )
    {
        return newContentType().
            module( Module.SYSTEM.getName() ).
            name( name ).
            displayName( displayName ).
            setFinal( isFinal ).
            setAbstract( isAbstract ).
            build();
    }

    @Autowired
    public void setClient( final Client client )
    {
        this.client = client;
    }
}
