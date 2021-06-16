package com.enonic.xp.portal.impl.processor;

import java.util.HashSet;
import java.util.Set;

import com.enonic.xp.form.FormItemPath;
import com.enonic.xp.form.Input;
import com.enonic.xp.form.InputVisitor;
import com.enonic.xp.inputtype.InputTypeName;

public class HtmlAreaVisitor
    extends InputVisitor
{

    private final Set<FormItemPath> paths = new HashSet<>();

    @Override
    public void visit( final Input input )
    {
        if ( InputTypeName.HTML_AREA.equals( input.getInputType() ) )
        {
            paths.add( input.getPath() );
        }

    }

    public Set<FormItemPath> getPaths()
    {
        return paths;
    }
}
