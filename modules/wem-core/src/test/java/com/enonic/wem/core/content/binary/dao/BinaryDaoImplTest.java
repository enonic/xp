package com.enonic.wem.core.content.binary.dao;

import javax.jcr.Node;

import org.junit.Test;

import com.enonic.wem.api.content.binary.Binary;
import com.enonic.wem.api.content.binary.BinaryId;
import com.enonic.wem.core.AbstractJcrTest;
import com.enonic.wem.core.jcr.JcrHelper;

import static org.junit.Assert.*;

public class BinaryDaoImplTest
    extends AbstractJcrTest
{
    private BinaryDao binaryDao;

    public void setupDao()
        throws Exception
    {
        binaryDao = new BinaryDaoImpl();
    }

    @Test
    public void createBinary()
        throws Exception
    {
        // setup
        final Binary binary = Binary.from( "some data".getBytes() );

        // exercise
        final BinaryId binaryId = binaryDao.createBinary( binary, session );
        commit();

        // verify
        assertNotNull( binaryId );
        Node binariesNode = session.getNode( "/" + AbstractBinaryDaoHandler.BINARIES_PATH );
        Node createdBinaryNode = session.getNodeByIdentifier( binaryId.toString() );
        assertNotNull( createdBinaryNode );
        assertTrue( createdBinaryNode.hasProperty( AbstractBinaryDaoHandler.DATA_PROPERTY ) );
        assertEquals( binariesNode.getPath(), createdBinaryNode.getParent().getPath() );
    }

    @Test
    public void getBinary()
        throws Exception
    {
        // setup
        final BinaryId binaryId = binaryDao.createBinary( Binary.from( "some data".getBytes() ), session );
        commit();

        // exercise
        final Binary retrievedBinary = binaryDao.getBinary( binaryId, session );

        // verify
        assertNotNull( retrievedBinary );
        assertArrayEquals( "some data".getBytes(), retrievedBinary.toByteArray() );
    }

    @Test
    public void getBinaryNotFound()
        throws Exception
    {
        // setup
        final BinaryId binaryId = binaryDao.createBinary( Binary.from( "some data".getBytes() ), session );
        commit();

        // exercise
        final Binary retrievedBinary = binaryDao.getBinary( BinaryId.from( "dummy-id" ), session );

        // verify
        assertNull( retrievedBinary );
    }

    @Test
    public void deleteBinary()
        throws Exception
    {
        // setup
        final BinaryId binaryId = binaryDao.createBinary( Binary.from( "some data".getBytes() ), session );
        final BinaryId binaryId2 = binaryDao.createBinary( Binary.from( "some other data".getBytes() ), session );
        final BinaryId binaryId3 = binaryDao.createBinary( Binary.from( "more data".getBytes() ), session );
        commit();

        // exercise
        boolean deleted = binaryDao.deleteBinary( binaryId, session );
        commit();

        // verify
        assertTrue( deleted );
        Node binariesNode = session.getNode( "/" + AbstractBinaryDaoHandler.BINARIES_PATH );
        assertEquals( 2, binariesNode.getNodes().getSize() );

        assertNull( JcrHelper.getNodeById( session, binaryId.toString() ) );
        assertNotNull( JcrHelper.getNodeById( session, binaryId2.toString() ) );
        assertNotNull( JcrHelper.getNodeById( session, binaryId3.toString() ) );
    }

    @Test
    public void deleteBinaryMissing()
        throws Exception
    {
        // setup
        final BinaryId binaryId = binaryDao.createBinary( Binary.from( "some data".getBytes() ), session );
        final BinaryId binaryId2 = binaryDao.createBinary( Binary.from( "some other data".getBytes() ), session );
        final BinaryId binaryId3 = binaryDao.createBinary( Binary.from( "more data".getBytes() ), session );
        commit();

        // exercise
        boolean deleted = binaryDao.deleteBinary( BinaryId.from( "dummy-id" ), session );
        commit();

        // verify
        assertFalse( deleted );
        Node binariesNode = session.getNode( "/" + AbstractBinaryDaoHandler.BINARIES_PATH );
        assertEquals( 3, binariesNode.getNodes().getSize() );

        assertNotNull( JcrHelper.getNodeById( session, binaryId.toString() ) );
        assertNotNull( JcrHelper.getNodeById( session, binaryId2.toString() ) );
        assertNotNull( JcrHelper.getNodeById( session, binaryId3.toString() ) );
    }
}
