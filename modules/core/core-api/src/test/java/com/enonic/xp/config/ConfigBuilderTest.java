package com.enonic.xp.config;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.junit.jupiter.api.Test;

import com.enonic.xp.core.internal.Dictionaries;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConfigBuilderTest
{

    @Test
    public void testBuild()
    {
        int n = 7;
        int[][] edges = new int[][]{{0, 1}, {0, 2}, {1, 4}, {1, 5}, {2, 3}, {2, 6}};
        List<Boolean> hasApple = List.of( false, false, true, false, false, true, false );

        System.out.println( minTime( n, edges, hasApple ) );

    }

    public int minTime( int n, int[][] edges, List<Boolean> hasApple )
    {
        final TreeNode[] nodes = new TreeNode[n];

        for ( int[] edge : edges )
        {
            if ( nodes[edge[0]] == null )
            {
                nodes[edge[0]] = new TreeNode( edge[0], edge[1] );
            }
            else
            {
                nodes[edge[0]].addConnection( edge[1] );
            }
            if ( nodes[edge[1]] == null )
            {
                nodes[edge[1]] = new TreeNode( edge[1], edge[0] );
            }
            else
            {
                nodes[edge[1]].addConnection( edge[0] );
            }
        }

        return Math.max( 0, findApples( nodes[0], nodes, hasApple ) - 1 );
    }

    private int findApples( TreeNode node, TreeNode[] nodes, List<Boolean> hasApple )
    {
        if ( node == null )
        {
            return 0;
        }

        int result = 0;

        for ( Integer child : node.connections )
        {
            int childTime = findApples( nodes[child], nodes, hasApple );
            result += childTime == 0 ? 0 : childTime + 1;
        }

        if ( hasApple.get( node.val ) || result > 0 )
        {
            result++;
        }

        return result;
    }

    @Test
    void testAdd()
    {
        final Configuration config =
            ConfigBuilder.create().add( "key1", "value1" ).add( "  key2  ", "  value2  " ).add( "key3", 11 ).add( "key4", null ).build();

        assertNotNull( config );
        assertEquals( "value1", config.get( "key1" ) );
        assertEquals( "value2", config.get( "key2" ) );
        assertEquals( "11", config.get( "key3" ) );
        assertEquals( 3, config.asMap().size() );
    }

    @Test
    void testEmpty()
    {
        final Configuration config = ConfigBuilder.create().build();

        assertNotNull( config );
        assertTrue( config.asMap().isEmpty() );
    }

    @Test
    void testAddAll_map()
    {
        final Map<String, String> source = new HashMap<>();
        source.put( "key1", "value1" );
        source.put( "key2", "value2" );

        final Configuration config = ConfigBuilder.create().addAll( source ).add( "key3", "value3" ).build();

        assertNotNull( config );
        assertEquals( true, config.exists( "key1" ) );
        assertEquals( true, config.exists( "key2" ) );
        assertEquals( true, config.exists( "key3" ) );

        final Map<String, String> map = config.asMap();
        assertEquals( 3, map.size() );
    }

    @Test
    void testAddAll_properties()
    {
        final Properties properties = new Properties();
        properties.put( "key1", "value1" );
        properties.put( "key2", "value2" );

        final Configuration config = ConfigBuilder.create().addAll( properties ).add( "key3", "value3" ).build();

        assertNotNull( config );
        assertEquals( true, config.exists( "key1" ) );
        assertEquals( true, config.exists( "key2" ) );
        assertEquals( true, config.exists( "key3" ) );

        final Map<String, String> map = config.asMap();
        assertEquals( 3, map.size() );
    }

    @Test
    void testAddAll_dictionary()
    {
        Map<String, String> dictionary = Map.of( "key1", "value1", "key2", "value2" );

        final Configuration config = ConfigBuilder.create().addAll( Dictionaries.copyOf( dictionary ) ).add( "key3", "value3" ).build();

        assertNotNull( config );
        assertEquals( true, config.exists( "key1" ) );
        assertEquals( true, config.exists( "key2" ) );
        assertEquals( true, config.exists( "key3" ) );

        final Map<String, String> map = config.asMap();
        assertEquals( 3, map.size() );
    }

    @Test
    void testAddConfig()
    {
        final Configuration config1 = ConfigBuilder.create().add( "key1", "value1" ).add( "key2", "value2" ).build();

        final Configuration config2 = ConfigBuilder.create().addAll( config1 ).add( "key3", "value3" ).build();

        assertNotNull( config2 );
        assertEquals( true, config2.exists( "key1" ) );
        assertEquals( true, config2.exists( "key2" ) );
        assertEquals( true, config2.exists( "key3" ) );

        final Map<String, String> map = config2.asMap();
        assertEquals( 3, map.size() );
    }

    @Test
    void testLoad()
    {
        final Configuration config1 =
            ConfigBuilder.create().load( getClass(), "ConfigLoaderTest.properties" ).add( "key3", "value3" ).build();

        final Configuration config2 = ConfigBuilder.create().addAll( config1 ).add( "key3", "value3" ).build();

        assertNotNull( config2 );
        assertEquals( true, config2.exists( "key1" ) );
        assertEquals( true, config2.exists( "key2" ) );
        assertEquals( true, config2.exists( "key3" ) );

        final Map<String, String> map = config2.asMap();
        assertEquals( 3, map.size() );
    }

    class TreeNode
    {
        int val;

        Set<Integer> connections;

        TreeNode( int val, int connection )
        {
            this.val = val;
            this.connections = new HashSet<>();
            addConnection( connection );
        }

        final Set<Integer> addConnection( int node )
        {
            if ( this.val < node )
            {
                this.connections.add( node );
            }
            return this.connections;
        }
    }
}
