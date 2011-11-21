/* ==========================================
 * JGraphT : a free Java graph-theory library
 * ==========================================
 *
 * Project Info:  http://jgrapht.sourceforge.net/
 * Project Creator:  Barak Naveh (http://sourceforge.net/users/barak_naveh)
 *
 * (C) Copyright 2003-2008, by Barak Naveh and Contributors.
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation,
 * Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307, USA.
 */
/* -----------------
 * HelloJGraphT.java
 * -----------------
 * (C) Copyright 2003-2008, by Barak Naveh and Contributors.
 *
 * Original Author:  Barak Naveh
 * Contributor(s):   -
 *
 * $Id: HelloJGraphT.java 645 2008-09-30 19:44:48Z perfecthash $
 *
 * Changes
 * -------
 * 27-Jul-2003 : Initial revision (BN);
 *
 */
package graph.test;

import java.net.*;

import mirroruniverse.g3player.PointPair;

import org.jgrapht.*;
import org.jgrapht.graph.*;


/**
 * A simple introduction to using JGraphT.
 *
 * @author Barak Naveh
 * @since Jul 27, 2003
 */
public final class HelloJGraphT
{
    //~ Constructors -----------------------------------------------------------

    private HelloJGraphT()
    {
    } // ensure non-instantiability.

    //~ Methods ----------------------------------------------------------------

    /**
     * The starting point for the demo.
     *
     * @param args ignored.
     */
    public static void main(String [] args)
    {
        UndirectedGraph<PointPair, DefaultEdge> stringGraph = createStringGraph();

        // note undirected edges are printed as: {<v1>,<v2>}
        System.out.println(stringGraph.toString());

        // create a graph based on URL objects
        DirectedGraph<URL, DefaultEdge> hrefGraph = createHrefGraph();

        // note directed edges are printed as: (<v1>,<v2>)
        System.out.println(hrefGraph.toString());
    }

    /**
     * Creates a toy directed graph based on URL objects that represents link
     * structure.
     *
     * @return a graph based on URL objects.
     */
    private static DirectedGraph<URL, DefaultEdge> createHrefGraph()
    {
        DirectedGraph<URL, DefaultEdge> g =
            new DefaultDirectedGraph<URL, DefaultEdge>(DefaultEdge.class);

        try {
            URL amazon = new URL("http://www.amazon.com");
            URL yahoo = new URL("http://www.yahoo.com");
            URL ebay = new URL("http://www.ebay.com");

            // add the vertices
            g.addVertex(amazon);
            g.addVertex(yahoo);
            g.addVertex(ebay);

            // add edges to create linking structure
            g.addEdge(yahoo, amazon);
            g.addEdge(yahoo, ebay);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return g;
    }

    /**
     * Craete a toy graph based on String objects.
     *
     * @return a graph based on String objects.
     */
    private static UndirectedGraph<PointPair, DefaultEdge> createStringGraph()
    {
        UndirectedGraph<PointPair, DefaultEdge> g =
            new SimpleGraph<PointPair, DefaultEdge>(DefaultEdge.class);

        PointPair pp1 = new PointPair(1, 1, 1, 1);
        PointPair pp2 = new PointPair(1, 1, 1, 1);        
        PointPair pp3 = new PointPair(1, 1, 1, 1);
        PointPair pp4 = new PointPair(1, 1, 1, 1);
        // add the vertices
        g.addVertex(pp1);
        g.addVertex(pp2);
        g.addVertex(pp3);
        g.addVertex(pp4);

        // add edges to create a circuit
        g.addEdge(pp1, pp2);
        g.addEdge(pp2, pp3);
        g.addEdge(pp3, pp4);
        g.addEdge(pp4, pp1);

        return g;
    }
}

// End HelloJGraphT.java
