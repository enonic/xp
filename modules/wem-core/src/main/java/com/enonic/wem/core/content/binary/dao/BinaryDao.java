package com.enonic.wem.core.content.binary.dao;


import javax.jcr.Session;

import com.enonic.wem.api.content.binary.Binary;
import com.enonic.wem.api.content.binary.BinaryId;

public interface BinaryDao
{
    BinaryId createBinary( Binary binary, Session session );

    Binary getBinary( BinaryId binaryId, Session session );

    boolean deleteBinary( BinaryId binaryId, Session session );
}
