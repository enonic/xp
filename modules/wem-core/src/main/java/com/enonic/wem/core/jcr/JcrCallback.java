package com.enonic.wem.core.jcr;


import java.io.IOException;

import javax.jcr.RepositoryException;

public interface JcrCallback<R>
{

    public R doInJcr( JcrSession session )
            throws IOException, RepositoryException;

}
