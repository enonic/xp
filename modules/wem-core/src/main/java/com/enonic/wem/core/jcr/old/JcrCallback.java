package com.enonic.wem.core.jcr.old;


import java.io.IOException;

import javax.jcr.RepositoryException;

public interface JcrCallback
{

    public Object doInJcr( JcrSession session )
            throws IOException, RepositoryException;

}
