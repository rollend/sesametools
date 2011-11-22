/**
 * 
 */
package net.fortytwo.sesametools;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.model.BNode;
import org.openrdf.model.Graph;
import org.openrdf.model.Literal;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.GraphImpl;
import org.openrdf.model.vocabulary.RDF;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 * 
 */
public class RdfListUtilTest
{
    private Graph testGraph;
    private ValueFactory vf;
    
    private Resource testSubjectUri1;
    private URI testPredicateUri1;
    private URI testObjectUri1;
    private URI testObjectUri2;
    private BNode testObjectBNode1;
    private Literal testObjectLiteral1;

    private List<Value> testValuesEmpty;
    private List<Value> testValuesSingleUri;
    private List<Value> testValuesMultipleElements;
    
    private URI testListHeadUri1;
    private URI testListHeadUri2;
    private BNode testListHeadBNode1;
    private BNode testListHeadBNode2;
    
    @Before
    public void setUp()
    {
        this.testGraph = new GraphImpl();
        this.vf = this.testGraph.getValueFactory();
        
        this.testSubjectUri1 = this.vf.createURI("http://examples.net/testsubject/1");
        this.testPredicateUri1 = this.vf.createURI("http://more.example.org/testpredicate/1");
        
        this.testListHeadUri1 = this.vf.createURI("http://examples.net/testlisthead/1");
        this.testListHeadUri2 = this.vf.createURI("http://examples.net/testlisthead/2");
        this.testListHeadBNode1 = this.vf.createBNode();
        this.testListHeadBNode2 = this.vf.createBNode();
        
        this.testObjectUri1 = this.vf.createURI("http://example.org/testobject/1");
        this.testObjectUri2 = this.vf.createURI("http://example.org/testobject/2");
        this.testObjectBNode1 = this.vf.createBNode();
        this.testObjectLiteral1 = this.vf.createLiteral("testobjectliteral1");
        
        this.testValuesEmpty = Collections.emptyList();
        
        this.testValuesSingleUri = new ArrayList<Value>(1);
        this.testValuesSingleUri.add(this.testObjectUri1);
        
        this.testValuesMultipleElements = new ArrayList<Value>(3);
        this.testValuesMultipleElements.add(this.testObjectBNode1);
        this.testValuesMultipleElements.add(this.testObjectLiteral1);
        this.testValuesMultipleElements.add(this.testObjectUri1);
        
    }
    
    @After
    public void tearDown()
    {
        this.testGraph = null;
        this.vf = null;
        
        this.testSubjectUri1 = null;
        this.testPredicateUri1 = null;
        
        this.testListHeadUri1 = null;
        this.testListHeadUri2 = null;
        this.testListHeadBNode1 = null;
        this.testListHeadBNode2 = null;
        
        this.testObjectUri1 = null;
        this.testObjectUri2 = null;
        this.testObjectBNode1 = null;
        this.testObjectLiteral1 = null;
        
        this.testValuesEmpty = null;
        this.testValuesSingleUri = null;
        this.testValuesMultipleElements = null;
        
    }
    
    /**
     * Test method for
     * {@link net.fortytwo.sesametools.RdfListUtil#addListAtNode(org.openrdf.model.Resource, org.openrdf.model.URI, java.util.List, org.openrdf.model.Graph, org.openrdf.model.Resource[])}
     * .
     */
    @Test
    public void testAddListAtNodeEmptyNoContext()
    {
        RdfListUtil.addListAtNode(this.testSubjectUri1, this.testPredicateUri1, this.testValuesEmpty, this.testGraph);
        
        Assert.assertEquals(0, this.testGraph.size());
    }
    
    /**
     * Test method for
     * {@link net.fortytwo.sesametools.RdfListUtil#addListAtNode(org.openrdf.model.Resource, org.openrdf.model.URI, java.util.List, org.openrdf.model.Graph, org.openrdf.model.Resource[])}
     * .
     */
    @Test
    public void testAddListAtNodeMultipleElementsNoContext()
    {
        RdfListUtil.addListAtNode(this.testSubjectUri1, this.testPredicateUri1, this.testValuesMultipleElements,
                this.testGraph);
        
        Assert.assertEquals(7, this.testGraph.size());
        
        // Match the head
        final Iterator<Statement> headMatch = this.testGraph.match(this.testSubjectUri1, this.testPredicateUri1, null);
        
        Assert.assertTrue(headMatch.hasNext());
        
        final Statement headMatchedStatement = headMatch.next();
        
        Assert.assertNotNull(headMatchedStatement);
        
        Assert.assertFalse(headMatch.hasNext());
        
        Assert.assertTrue(headMatchedStatement.getObject() instanceof Resource);
        
        // match the first element, which should be a bnode
        final Iterator<Statement> matchFirst1 =
                this.testGraph.match((BNode)headMatchedStatement.getObject(), RDF.FIRST, null);
        
        Assert.assertTrue(matchFirst1.hasNext());
        
        final Statement firstListMatchedStatement1 = matchFirst1.next();
        
        Assert.assertNotNull(firstListMatchedStatement1);
        
        Assert.assertFalse(matchFirst1.hasNext());
        
        Assert.assertTrue(firstListMatchedStatement1.getObject() instanceof Resource);
        // TODO: is this check consistent with BlankNode theory?
        Assert.assertEquals(this.testObjectBNode1, firstListMatchedStatement1.getObject());
        
        // match the rest link, which should be a BNode
        final Iterator<Statement> matchRest1 =
                this.testGraph.match((BNode)headMatchedStatement.getObject(), RDF.REST, null);
        
        Assert.assertTrue(matchRest1.hasNext());
        
        final Statement restListMatchedStatement1 = matchRest1.next();
        
        Assert.assertNotNull(restListMatchedStatement1);
        
        Assert.assertFalse(matchRest1.hasNext());
        
        Assert.assertTrue(restListMatchedStatement1.getObject() instanceof Resource);
        
        // match the next first node, which should be a literal
        final Iterator<Statement> matchFirst2 =
                this.testGraph.match((BNode)restListMatchedStatement1.getObject(), RDF.FIRST, null);
        
        Assert.assertTrue(matchFirst2.hasNext());
        
        final Statement firstListMatchedStatement2 = matchFirst2.next();
        
        Assert.assertNotNull(firstListMatchedStatement2);
        
        Assert.assertFalse(matchFirst2.hasNext());
        
        Assert.assertTrue(firstListMatchedStatement2.getObject() instanceof Literal);
        
        Assert.assertEquals(this.testObjectLiteral1, firstListMatchedStatement2.getObject());
        
        // match the rest link, which should be a BNode
        final Iterator<Statement> matchRest2 =
                this.testGraph.match((BNode)restListMatchedStatement1.getObject(), RDF.REST, null);
        
        Assert.assertTrue(matchRest2.hasNext());
        
        final Statement restListMatchedStatement2 = matchRest2.next();
        
        Assert.assertNotNull(restListMatchedStatement2);
        
        Assert.assertFalse(matchRest2.hasNext());
        
        Assert.assertTrue(restListMatchedStatement2.getObject() instanceof Resource);
        
        // match the next first node, which should be a URI
        final Iterator<Statement> matchFirst3 =
                this.testGraph.match((BNode)restListMatchedStatement2.getObject(), RDF.FIRST, null);
        
        Assert.assertTrue(matchFirst3.hasNext());
        
        final Statement firstListMatchedStatement3 = matchFirst3.next();
        
        Assert.assertNotNull(firstListMatchedStatement3);
        
        Assert.assertFalse(matchFirst3.hasNext());
        
        Assert.assertTrue(firstListMatchedStatement3.getObject() instanceof URI);
        
        Assert.assertEquals(this.testObjectUri1, firstListMatchedStatement3.getObject());
        
        // match the rest link, which should be the URI rdf:nil
        final Iterator<Statement> matchRest3 =
                this.testGraph.match((BNode)restListMatchedStatement2.getObject(), RDF.REST, null);
        
        Assert.assertTrue(matchRest3.hasNext());
        
        final Statement restListMatchedStatement3 = matchRest3.next();
        
        Assert.assertNotNull(restListMatchedStatement3);
        
        Assert.assertFalse(matchRest3.hasNext());
        
        Assert.assertTrue(restListMatchedStatement3.getObject() instanceof URI);
        
        Assert.assertEquals(RDF.NIL, restListMatchedStatement3.getObject());
        
    }
    
    /**
     * Test method for
     * {@link net.fortytwo.sesametools.RdfListUtil#addListAtNode(org.openrdf.model.Resource, org.openrdf.model.URI, java.util.List, org.openrdf.model.Graph, org.openrdf.model.Resource[])}
     * .
     */
    @Test
    public void testAddListAtNodeSingleElementNoContext()
    {
        RdfListUtil.addListAtNode(this.testSubjectUri1, this.testPredicateUri1, this.testValuesSingleUri,
                this.testGraph);
        
        Assert.assertEquals(3, this.testGraph.size());
        
        // Match the head
        final Iterator<Statement> match = this.testGraph.match(this.testSubjectUri1, this.testPredicateUri1, null);
        
        Assert.assertTrue(match.hasNext());
        
        final Statement matchedStatement = match.next();
        
        Assert.assertNotNull(matchedStatement);
        
        Assert.assertFalse(match.hasNext());
        
        Assert.assertTrue(matchedStatement.getObject() instanceof Resource);
        
        // match the first element
        final Iterator<Statement> matchFirstOthers =
                this.testGraph.match((BNode)matchedStatement.getObject(), RDF.FIRST, null);
        
        Assert.assertTrue(matchFirstOthers.hasNext());
        
        final Statement firstListMatchedStatement = matchFirstOthers.next();
        
        Assert.assertNotNull(firstListMatchedStatement);
        
        Assert.assertFalse(matchFirstOthers.hasNext());
        
        Assert.assertTrue(firstListMatchedStatement.getObject() instanceof URI);
        
        Assert.assertEquals(this.testObjectUri1, firstListMatchedStatement.getObject());
        
        // match the rest link, which should be rdf:nil for a single value list
        final Iterator<Statement> matchRestNil =
                this.testGraph.match((BNode)matchedStatement.getObject(), RDF.REST, null);
        
        Assert.assertTrue(matchRestNil.hasNext());
        
        final Statement restListMatchedStatement = matchRestNil.next();
        
        Assert.assertNotNull(restListMatchedStatement);
        
        Assert.assertFalse(matchRestNil.hasNext());
        
        Assert.assertTrue(restListMatchedStatement.getObject() instanceof URI);
        
        Assert.assertEquals(RDF.NIL, restListMatchedStatement.getObject());
        
    }
    
    /**
     * Test method for
     * {@link net.fortytwo.sesametools.RdfListUtil#addListAtNode(org.openrdf.model.Resource, org.openrdf.model.URI, java.util.List, org.openrdf.model.Graph, org.openrdf.model.Resource[])}
     * .
     */
    @Test
    public void testAddListBNodeHeadEmptyNoContext()
    {
        RdfListUtil.addList(this.testListHeadBNode1, this.testValuesEmpty, this.testGraph);
        
        Assert.assertEquals(0, this.testGraph.size());
    }
    
    /**
     * Test method for
     * {@link net.fortytwo.sesametools.RdfListUtil#addListAtNode(org.openrdf.model.Resource, org.openrdf.model.URI, java.util.List, org.openrdf.model.Graph, org.openrdf.model.Resource[])}
     * .
     */
    @Test
    public void testAddListBNodeHeadMultipleElementsNoContext()
    {
        RdfListUtil.addList(this.testListHeadBNode1, this.testValuesMultipleElements, this.testGraph);
        
        Assert.assertEquals(6, this.testGraph.size());
        
        // match the first element, which should be a bnode
        final Iterator<Statement> matchFirst1 = this.testGraph.match(this.testListHeadBNode1, RDF.FIRST, null);
        
        Assert.assertTrue(matchFirst1.hasNext());
        
        final Statement firstListMatchedStatement1 = matchFirst1.next();
        
        Assert.assertNotNull(firstListMatchedStatement1);
        
        Assert.assertFalse(matchFirst1.hasNext());
        
        Assert.assertTrue(firstListMatchedStatement1.getObject() instanceof Resource);
        
        // TODO: is this check consistent with BlankNode theory?
        Assert.assertEquals(this.testObjectBNode1, firstListMatchedStatement1.getObject());
        
        // match the rest link, which should be a BNode
        final Iterator<Statement> matchRest1 = this.testGraph.match(this.testListHeadBNode1, RDF.REST, null);
        
        Assert.assertTrue(matchRest1.hasNext());
        
        final Statement restListMatchedStatement1 = matchRest1.next();
        
        Assert.assertNotNull(restListMatchedStatement1);
        
        Assert.assertFalse(matchRest1.hasNext());
        
        Assert.assertTrue(restListMatchedStatement1.getObject() instanceof Resource);
        
        // match the next first node, which should be a literal
        final Iterator<Statement> matchFirst2 =
                this.testGraph.match((BNode)restListMatchedStatement1.getObject(), RDF.FIRST, null);
        
        Assert.assertTrue(matchFirst2.hasNext());
        
        final Statement firstListMatchedStatement2 = matchFirst2.next();
        
        Assert.assertNotNull(firstListMatchedStatement2);
        
        Assert.assertFalse(matchFirst2.hasNext());
        
        Assert.assertTrue(firstListMatchedStatement2.getObject() instanceof Literal);
        
        Assert.assertEquals(this.testObjectLiteral1, firstListMatchedStatement2.getObject());
        
        // match the rest link, which should be a BNode
        final Iterator<Statement> matchRest2 =
                this.testGraph.match((BNode)restListMatchedStatement1.getObject(), RDF.REST, null);
        
        Assert.assertTrue(matchRest2.hasNext());
        
        final Statement restListMatchedStatement2 = matchRest2.next();
        
        Assert.assertNotNull(restListMatchedStatement2);
        
        Assert.assertFalse(matchRest2.hasNext());
        
        Assert.assertTrue(restListMatchedStatement2.getObject() instanceof Resource);
        
        // match the next first node, which should be a URI
        final Iterator<Statement> matchFirst3 =
                this.testGraph.match((BNode)restListMatchedStatement2.getObject(), RDF.FIRST, null);
        
        Assert.assertTrue(matchFirst3.hasNext());
        
        final Statement firstListMatchedStatement3 = matchFirst3.next();
        
        Assert.assertNotNull(firstListMatchedStatement3);
        
        Assert.assertFalse(matchFirst3.hasNext());
        
        Assert.assertTrue(firstListMatchedStatement3.getObject() instanceof URI);
        
        Assert.assertEquals(this.testObjectUri1, firstListMatchedStatement3.getObject());
        
        // match the rest link, which should be the URI rdf:nil
        final Iterator<Statement> matchRest3 =
                this.testGraph.match((BNode)restListMatchedStatement2.getObject(), RDF.REST, null);
        
        Assert.assertTrue(matchRest3.hasNext());
        
        final Statement restListMatchedStatement3 = matchRest3.next();
        
        Assert.assertNotNull(restListMatchedStatement3);
        
        Assert.assertFalse(matchRest3.hasNext());
        
        Assert.assertTrue(restListMatchedStatement3.getObject() instanceof URI);
        
        Assert.assertEquals(RDF.NIL, restListMatchedStatement3.getObject());
        
    }
    
    /**
     * Test method for
     * {@link net.fortytwo.sesametools.RdfListUtil#addListAtNode(org.openrdf.model.Resource, org.openrdf.model.URI, java.util.List, org.openrdf.model.Graph, org.openrdf.model.Resource[])}
     * .
     */
    @Test
    public void testAddListBNodeHeadSingleElementNoContext()
    {
        RdfListUtil.addList(this.testListHeadBNode1, this.testValuesSingleUri, this.testGraph);
        
        Assert.assertEquals(2, this.testGraph.size());
        
        // match the first element
        final Iterator<Statement> matchFirstOthers = this.testGraph.match(this.testListHeadBNode1, RDF.FIRST, null);
        
        Assert.assertTrue(matchFirstOthers.hasNext());
        
        final Statement firstListMatchedStatement = matchFirstOthers.next();
        
        Assert.assertNotNull(firstListMatchedStatement);
        
        Assert.assertFalse(matchFirstOthers.hasNext());
        
        Assert.assertTrue(firstListMatchedStatement.getObject() instanceof URI);
        
        Assert.assertEquals(this.testObjectUri1, firstListMatchedStatement.getObject());
        
        // match the rest link, which should be rdf:nil for a single value list
        final Iterator<Statement> matchRestNil = this.testGraph.match(this.testListHeadBNode1, RDF.REST, null);
        
        Assert.assertTrue(matchRestNil.hasNext());
        
        final Statement restListMatchedStatement = matchRestNil.next();
        
        Assert.assertNotNull(restListMatchedStatement);
        
        Assert.assertFalse(matchRestNil.hasNext());
        
        Assert.assertTrue(restListMatchedStatement.getObject() instanceof URI);
        
        Assert.assertEquals(RDF.NIL, restListMatchedStatement.getObject());
        
    }
    
    /**
     * Test method for
     * {@link net.fortytwo.sesametools.RdfListUtil#addListAtNode(org.openrdf.model.Resource, org.openrdf.model.URI, java.util.List, org.openrdf.model.Graph, org.openrdf.model.Resource[])}
     * .
     */
    @Test
    public void testAddListUriHeadEmptyNoContext()
    {
        RdfListUtil.addList(this.testListHeadUri1, this.testValuesEmpty, this.testGraph);
        
        Assert.assertEquals(0, this.testGraph.size());
    }
    
    /**
     * Test method for
     * {@link net.fortytwo.sesametools.RdfListUtil#addListAtNode(org.openrdf.model.Resource, org.openrdf.model.URI, java.util.List, org.openrdf.model.Graph, org.openrdf.model.Resource[])}
     * .
     */
    @Test
    public void testAddListURIHeadMultipleElementsNoContext()
    {
        RdfListUtil.addList(this.testListHeadUri1, this.testValuesMultipleElements, this.testGraph);
        
        Assert.assertEquals(6, this.testGraph.size());
        
        // match the first element, which should be a bnode
        final Iterator<Statement> matchFirst1 = this.testGraph.match(this.testListHeadUri1, RDF.FIRST, null);
        
        Assert.assertTrue(matchFirst1.hasNext());
        
        final Statement firstListMatchedStatement1 = matchFirst1.next();
        
        Assert.assertNotNull(firstListMatchedStatement1);
        
        Assert.assertFalse(matchFirst1.hasNext());
        
        Assert.assertTrue(firstListMatchedStatement1.getObject() instanceof Resource);
        
        // TODO: is this check consistent with BlankNode theory?
        Assert.assertEquals(this.testObjectBNode1, firstListMatchedStatement1.getObject());
        
        // match the rest link, which should be a BNode
        final Iterator<Statement> matchRest1 = this.testGraph.match(this.testListHeadUri1, RDF.REST, null);
        
        Assert.assertTrue(matchRest1.hasNext());
        
        final Statement restListMatchedStatement1 = matchRest1.next();
        
        Assert.assertNotNull(restListMatchedStatement1);
        
        Assert.assertFalse(matchRest1.hasNext());
        
        Assert.assertTrue(restListMatchedStatement1.getObject() instanceof Resource);
        
        // match the next first node, which should be a literal
        final Iterator<Statement> matchFirst2 =
                this.testGraph.match((BNode)restListMatchedStatement1.getObject(), RDF.FIRST, null);
        
        Assert.assertTrue(matchFirst2.hasNext());
        
        final Statement firstListMatchedStatement2 = matchFirst2.next();
        
        Assert.assertNotNull(firstListMatchedStatement2);
        
        Assert.assertFalse(matchFirst2.hasNext());
        
        Assert.assertTrue(firstListMatchedStatement2.getObject() instanceof Literal);
        
        Assert.assertEquals(this.testObjectLiteral1, firstListMatchedStatement2.getObject());
        
        // match the rest link, which should be a BNode
        final Iterator<Statement> matchRest2 =
                this.testGraph.match((BNode)restListMatchedStatement1.getObject(), RDF.REST, null);
        
        Assert.assertTrue(matchRest2.hasNext());
        
        final Statement restListMatchedStatement2 = matchRest2.next();
        
        Assert.assertNotNull(restListMatchedStatement2);
        
        Assert.assertFalse(matchRest2.hasNext());
        
        Assert.assertTrue(restListMatchedStatement2.getObject() instanceof Resource);
        
        // match the next first node, which should be a URI
        final Iterator<Statement> matchFirst3 =
                this.testGraph.match((BNode)restListMatchedStatement2.getObject(), RDF.FIRST, null);
        
        Assert.assertTrue(matchFirst3.hasNext());
        
        final Statement firstListMatchedStatement3 = matchFirst3.next();
        
        Assert.assertNotNull(firstListMatchedStatement3);
        
        Assert.assertFalse(matchFirst3.hasNext());
        
        Assert.assertTrue(firstListMatchedStatement3.getObject() instanceof URI);
        
        Assert.assertEquals(this.testObjectUri1, firstListMatchedStatement3.getObject());
        
        // match the rest link, which should be the URI rdf:nil
        final Iterator<Statement> matchRest3 =
                this.testGraph.match((BNode)restListMatchedStatement2.getObject(), RDF.REST, null);
        
        Assert.assertTrue(matchRest3.hasNext());
        
        final Statement restListMatchedStatement3 = matchRest3.next();
        
        Assert.assertNotNull(restListMatchedStatement3);
        
        Assert.assertFalse(matchRest3.hasNext());
        
        Assert.assertTrue(restListMatchedStatement3.getObject() instanceof URI);
        
        Assert.assertEquals(RDF.NIL, restListMatchedStatement3.getObject());
        
    }
    
    /**
     * Test method for
     * {@link net.fortytwo.sesametools.RdfListUtil#addListAtNode(org.openrdf.model.Resource, org.openrdf.model.URI, java.util.List, org.openrdf.model.Graph, org.openrdf.model.Resource[])}
     * .
     */
    @Test
    public void testAddListURIHeadSingleElementNoContext()
    {
        RdfListUtil.addList(this.testListHeadUri1, this.testValuesSingleUri, this.testGraph);
        
        Assert.assertEquals(2, this.testGraph.size());
        
        // match the first element
        final Iterator<Statement> matchFirstOthers = this.testGraph.match(this.testListHeadUri1, RDF.FIRST, null);
        
        Assert.assertTrue(matchFirstOthers.hasNext());
        
        final Statement firstListMatchedStatement = matchFirstOthers.next();
        
        Assert.assertNotNull(firstListMatchedStatement);
        
        Assert.assertFalse(matchFirstOthers.hasNext());
        
        Assert.assertTrue(firstListMatchedStatement.getObject() instanceof URI);
        
        Assert.assertEquals(this.testObjectUri1, firstListMatchedStatement.getObject());
        
        // match the rest link, which should be rdf:nil for a single value list
        final Iterator<Statement> matchRestNil = this.testGraph.match(this.testListHeadUri1, RDF.REST, null);
        
        Assert.assertTrue(matchRestNil.hasNext());
        
        final Statement restListMatchedStatement = matchRestNil.next();
        
        Assert.assertNotNull(restListMatchedStatement);
        
        Assert.assertFalse(matchRestNil.hasNext());
        
        Assert.assertTrue(restListMatchedStatement.getObject() instanceof URI);
        
        Assert.assertEquals(RDF.NIL, restListMatchedStatement.getObject());
        
    }
    
    /**
     * Test method for
     * {@link net.fortytwo.sesametools.RdfListUtil#getListAtNode(org.openrdf.model.Resource, org.openrdf.model.URI, org.openrdf.model.Graph, org.openrdf.model.Resource)}
     * .
     */
    @Test
    public void testGetListAfterAddListAtNodeMultipleElementsNullContext()
    {
        RdfListUtil.addListAtNode(this.testSubjectUri1, this.testPredicateUri1, this.testValuesMultipleElements,
                this.testGraph);
        
        Assert.assertEquals(7, this.testGraph.size());
        
        // verify that the head statement was inserted and find the first pointer to use with
        // getList
        final Iterator<Statement> match = this.testGraph.match(this.testSubjectUri1, this.testPredicateUri1, null);
        
        Assert.assertTrue(match.hasNext());
        
        final Statement matchedStatement = match.next();
        
        Assert.assertNotNull(matchedStatement);
        
        Assert.assertFalse(match.hasNext());
        
        Assert.assertTrue(matchedStatement.getObject() instanceof Resource);
        
        final List<Value> results =
                RdfListUtil.getList((BNode)matchedStatement.getObject(), this.testGraph, (Resource)null);
        
        Assert.assertEquals(3, results.size());
        
        Assert.assertTrue(results.contains(this.testObjectBNode1));
        Assert.assertTrue(results.contains(this.testObjectLiteral1));
        Assert.assertTrue(results.contains(this.testObjectUri1));
        
    }
    
    /**
     * Test method for
     * {@link net.fortytwo.sesametools.RdfListUtil#getListAtNode(org.openrdf.model.Resource, org.openrdf.model.URI, org.openrdf.model.Graph, org.openrdf.model.Resource)}
     * .
     */
    @Test
    public void testGetListAtNodeAfterInvalidGraphOperation()
    {
        RdfListUtil.addListAtNode(this.testSubjectUri1, this.testPredicateUri1, this.testValuesMultipleElements,
                this.testGraph);
        
        Assert.assertEquals(7, this.testGraph.size());
        
        // Modify the graph in an invalid way to test getList
        final Iterator<Statement> matches = this.testGraph.match(null, RDF.REST, RDF.NIL);
        
        Assert.assertTrue(matches.hasNext());
        
        final Statement matchedStatement = matches.next();
        
        Assert.assertFalse(matches.hasNext());
        
        Assert.assertTrue(this.testGraph.remove(matchedStatement));
        
        Assert.assertFalse(this.testGraph.contains(matchedStatement));
        
        try
        {
            @SuppressWarnings("unused")
            final List<Value> results =
                    RdfListUtil.getListAtNode(this.testSubjectUri1, this.testPredicateUri1, this.testGraph,
                            (Resource)null);
            Assert.fail("Did not find expected exception");
        }
        catch(final RuntimeException rex)
        {
            Assert.assertEquals("List structure was not complete", rex.getMessage());
        }
    }
    
    /**
     * Test method for
     * {@link net.fortytwo.sesametools.RdfListUtil#getListAtNode(org.openrdf.model.Resource, org.openrdf.model.URI, org.openrdf.model.Graph, org.openrdf.model.Resource)}
     * .
     */
    @Test
    public void testGetListAtNodeMultipleElementsNullContext()
    {
        RdfListUtil.addListAtNode(this.testSubjectUri1, this.testPredicateUri1, this.testValuesMultipleElements,
                this.testGraph);
        
        Assert.assertEquals(7, this.testGraph.size());
        
        final List<Value> results =
                RdfListUtil.getListAtNode(this.testSubjectUri1, this.testPredicateUri1, this.testGraph, (Resource)null);
        
        Assert.assertEquals(3, results.size());
        
        Assert.assertTrue(results.contains(this.testObjectBNode1));
        Assert.assertTrue(results.contains(this.testObjectLiteral1));
        Assert.assertTrue(results.contains(this.testObjectUri1));
        
    }
    
    /**
     * Test method for
     * {@link net.fortytwo.sesametools.RdfListUtil#getListAtNode(org.openrdf.model.Resource, org.openrdf.model.URI, org.openrdf.model.Graph, org.openrdf.model.Resource)}
     * .
     */
    @Test
    public void testGetListBNodeHeadAfterInvalidGraphOperation()
    {
        RdfListUtil.addList(this.testListHeadBNode1, this.testValuesMultipleElements, this.testGraph);
        
        Assert.assertEquals(6, this.testGraph.size());
        
        // Modify the graph in an invalid way to test getList
        final Iterator<Statement> matches = this.testGraph.match(null, RDF.REST, RDF.NIL);
        
        Assert.assertTrue(matches.hasNext());
        
        final Statement matchedStatement = matches.next();
        
        Assert.assertFalse(matches.hasNext());
        
        Assert.assertTrue(this.testGraph.remove(matchedStatement));
        
        Assert.assertFalse(this.testGraph.contains(matchedStatement));
        
        try
        {
            @SuppressWarnings("unused")
            final List<Value> results = RdfListUtil.getList(this.testListHeadBNode1, this.testGraph, (Resource)null);
            Assert.fail("Did not find expected exception");
        }
        catch(final RuntimeException rex)
        {
            Assert.assertEquals("List structure was not complete", rex.getMessage());
        }
    }
    
    /**
     * Test method for
     * {@link net.fortytwo.sesametools.RdfListUtil#getListAtNode(org.openrdf.model.Resource, org.openrdf.model.URI, org.openrdf.model.Graph, org.openrdf.model.Resource)}
     * .
     */
    @Test
    public void testGetListBNodeHeadMultipleElementsNullContext()
    {
        RdfListUtil.addList(this.testListHeadBNode1, this.testValuesMultipleElements, this.testGraph);
        
        Assert.assertEquals(6, this.testGraph.size());
        
        final List<Value> results = RdfListUtil.getList(this.testListHeadBNode1, this.testGraph, (Resource)null);
        
        Assert.assertEquals(3, results.size());
        
        Assert.assertTrue(results.contains(this.testObjectBNode1));
        Assert.assertTrue(results.contains(this.testObjectLiteral1));
        Assert.assertTrue(results.contains(this.testObjectUri1));
        
    }
    
    @Test
    public void testGetListsAtNodeSingleNullContext()
    {
        RdfListUtil.addListAtNode(this.testSubjectUri1, this.testPredicateUri1, this.testValuesSingleUri,
                this.testGraph);
        
        Assert.assertEquals(3, this.testGraph.size());
        
        // verify that the head statement was inserted
        final Iterator<Statement> match = this.testGraph.match(this.testSubjectUri1, this.testPredicateUri1, null);
        
        Assert.assertTrue(match.hasNext());
        
        final Statement matchedStatement = match.next();
        
        Assert.assertNotNull(matchedStatement);
        
        Assert.assertFalse(match.hasNext());
        
        Assert.assertTrue(matchedStatement.getObject() instanceof Resource);
        
        final Collection<List<Value>> lists =
                RdfListUtil
                        .getListsAtNode(this.testSubjectUri1, this.testPredicateUri1, this.testGraph, (Resource)null);
        
        Assert.assertEquals(1, lists.size());
        
        Iterator<List<Value>> listIterator = lists.iterator();
        
        Assert.assertTrue(listIterator.hasNext());
        
        List<Value> nextList = listIterator.next();
        
        Assert.assertEquals(1, nextList.size());
    }
    
    @Test
    public void testGetListsAfterAddListAtNodeSingleNullContext()
    {
        RdfListUtil.addListAtNode(this.testSubjectUri1, this.testPredicateUri1, this.testValuesSingleUri,
                this.testGraph);
        
        Assert.assertEquals(3, this.testGraph.size());
        
        // verify that the head statement was inserted
        final Iterator<Statement> match = this.testGraph.match(this.testSubjectUri1, this.testPredicateUri1, null);
        
        Assert.assertTrue(match.hasNext());
        
        final Statement matchedStatement = match.next();
        
        Assert.assertNotNull(matchedStatement);
        
        Assert.assertFalse(match.hasNext());
        
        Assert.assertTrue(matchedStatement.getObject() instanceof Resource);
        
        Set<Resource> heads = new HashSet<Resource>(1);
        heads.add((BNode)matchedStatement.getObject());
        
        final Collection<List<Value>> lists =
                RdfListUtil
                        .getLists(heads, this.testGraph, (Resource)null);
        
        Assert.assertEquals(1, lists.size());

        Iterator<List<Value>> listIterator = lists.iterator();
        
        Assert.assertTrue(listIterator.hasNext());
        
        List<Value> nextList = listIterator.next();
        
        Assert.assertEquals(1, nextList.size());
    }
    
    @Test
    public void testGetListsAfterAddListBNodeHeadSingleNullContext()
    {
        RdfListUtil.addList(this.testListHeadBNode1, this.testValuesSingleUri,
                this.testGraph);
        
        Assert.assertEquals(2, this.testGraph.size());
        
        // verify that the head statement was inserted
        final Iterator<Statement> match = this.testGraph.match(this.testListHeadBNode1, null, null);
        
        Assert.assertTrue(match.hasNext());
        
        final Statement matchedStatement = match.next();
        
        Assert.assertNotNull(matchedStatement);
        
        Assert.assertEquals(this.testListHeadBNode1, matchedStatement.getSubject());
        
        Set<Resource> heads = new HashSet<Resource>(1);
        heads.add((BNode)matchedStatement.getSubject());
        
        final Collection<List<Value>> lists =
                RdfListUtil
                        .getLists(heads, this.testGraph);
        
        Assert.assertEquals(1, lists.size());

        Iterator<List<Value>> listIterator = lists.iterator();
        
        Assert.assertTrue(listIterator.hasNext());
        
        List<Value> nextList = listIterator.next();
        
        Assert.assertEquals(1, nextList.size());
    }
    
    @Test
    public void testGetListsSingleNullContext()
    {
        RdfListUtil.addListAtNode(this.testSubjectUri1, this.testPredicateUri1, this.testValuesSingleUri,
                this.testGraph);
        
        Assert.assertEquals(3, this.testGraph.size());
        
        // Find the head node that was generated by this method
        final Iterator<Statement> match = this.testGraph.match(this.testSubjectUri1, this.testPredicateUri1, null);
        
        Assert.assertTrue(match.hasNext());
        
        final Statement matchedStatement = match.next();
        
        Assert.assertNotNull(matchedStatement);
        
        Assert.assertFalse(match.hasNext());
        
        Assert.assertTrue(matchedStatement.getObject() instanceof Resource);
        
        final Resource headNode = (Resource)matchedStatement.getObject();
        
        final Set<Resource> heads = new HashSet<Resource>();
        
        heads.add(headNode);
        
        final Collection<List<Value>> lists = RdfListUtil.getLists(heads, this.testGraph);
        
        Assert.assertEquals(1, lists.size());
        
        Iterator<List<Value>> listIterator = lists.iterator();
        
        Assert.assertTrue(listIterator.hasNext());
        
        List<Value> nextList = listIterator.next();
        
        Assert.assertEquals(1, nextList.size());
    }
    
    /**
     * Test method for
     * {@link net.fortytwo.sesametools.RdfListUtil#getListAtNode(org.openrdf.model.Resource, org.openrdf.model.URI, org.openrdf.model.Graph, org.openrdf.model.Resource)}
     * .
     */
    @Test
    public void testGetListURIHeadAfterInvalidGraphOperation()
    {
        RdfListUtil.addList(this.testListHeadUri1, this.testValuesMultipleElements, this.testGraph);
        
        Assert.assertEquals(6, this.testGraph.size());
        
        // Modify the graph in an invalid way to test getList
        final Iterator<Statement> matches = this.testGraph.match(null, RDF.REST, RDF.NIL);
        
        Assert.assertTrue(matches.hasNext());
        
        final Statement matchedStatement = matches.next();
        
        Assert.assertFalse(matches.hasNext());
        
        Assert.assertTrue(this.testGraph.remove(matchedStatement));
        
        Assert.assertFalse(this.testGraph.contains(matchedStatement));
        
        try
        {
            @SuppressWarnings("unused")
            final List<Value> results = RdfListUtil.getList(this.testListHeadUri1, this.testGraph, (Resource)null);
            Assert.fail("Did not find expected exception");
        }
        catch(final RuntimeException rex)
        {
            Assert.assertEquals("List structure was not complete", rex.getMessage());
        }
    }
    
    @Test
    public void testGetListsForkedValid()
    {
        Statement testStatement1 = vf.createStatement(testListHeadBNode1, RDF.FIRST, testObjectLiteral1);
        this.testGraph.add(testStatement1);

        Statement testStatement2 = vf.createStatement(testListHeadBNode1, RDF.REST, testListHeadUri1);
        this.testGraph.add(testStatement2);
        
        Statement testStatement3 = vf.createStatement(testListHeadUri1, RDF.FIRST, testObjectUri1);
        this.testGraph.add(testStatement3);
        
        Statement testStatement4 = vf.createStatement(testListHeadUri1, RDF.REST, testListHeadBNode2);
        this.testGraph.add(testStatement4);
        
        Statement testStatement5 = vf.createStatement(testListHeadUri1, RDF.REST, testListHeadUri2);
        this.testGraph.add(testStatement5);
        
        Statement testStatement6 = vf.createStatement(testListHeadBNode2, RDF.FIRST, testObjectBNode1);
        this.testGraph.add(testStatement6);
        
        Statement testStatement7 = vf.createStatement(testListHeadBNode2, RDF.REST, RDF.NIL);
        this.testGraph.add(testStatement7);
        
        Statement testStatement8 = vf.createStatement(testListHeadUri2, RDF.FIRST, testObjectUri2);
        this.testGraph.add(testStatement8);
        
        Statement testStatement9 = vf.createStatement(testListHeadUri2, RDF.REST, RDF.NIL);
        this.testGraph.add(testStatement9);
        
        Set<Resource> heads = new HashSet<Resource>(1);
        heads.add(this.testListHeadBNode1);
        
        final Collection<List<Value>> results = RdfListUtil.getLists(heads, this.testGraph);

        Assert.assertEquals(2, results.size());
        
        boolean foundFirstList = false;
        boolean foundSecondList = false;
        
        // Test that both of the returned lists contain three elements
        for(List<Value> resultList : results)
        {
            Assert.assertEquals(3, resultList.size());
            
            Assert.assertTrue(resultList.contains(testObjectLiteral1));
            
            Assert.assertTrue(resultList.contains(testObjectUri1));
            
            if(resultList.contains(testObjectBNode1))
            {
                foundFirstList = true;
            }
            else if(resultList.contains(testObjectUri2))
            {
                foundSecondList = true;
            }
        }
        
        Assert.assertTrue("Did not find first list", foundFirstList);
        Assert.assertTrue("Did not find second list", foundSecondList);
    }

    @Test
    public void testGetListForkedValid()
    {
        Statement testStatement1 = vf.createStatement(testListHeadBNode1, RDF.FIRST, testObjectLiteral1);
        this.testGraph.add(testStatement1);

        Statement testStatement2 = vf.createStatement(testListHeadBNode1, RDF.REST, testListHeadUri1);
        this.testGraph.add(testStatement2);
        
        Statement testStatement3 = vf.createStatement(testListHeadUri1, RDF.FIRST, testObjectUri1);
        this.testGraph.add(testStatement3);
        
        Statement testStatement4 = vf.createStatement(testListHeadUri1, RDF.REST, testListHeadBNode2);
        this.testGraph.add(testStatement4);
        
        Statement testStatement5 = vf.createStatement(testListHeadUri1, RDF.REST, testListHeadUri2);
        this.testGraph.add(testStatement5);
        
        Statement testStatement6 = vf.createStatement(testListHeadBNode2, RDF.FIRST, testObjectBNode1);
        this.testGraph.add(testStatement6);
        
        Statement testStatement7 = vf.createStatement(testListHeadBNode2, RDF.REST, RDF.NIL);
        this.testGraph.add(testStatement7);
        
        Statement testStatement8 = vf.createStatement(testListHeadUri2, RDF.FIRST, testObjectUri2);
        this.testGraph.add(testStatement8);
        
        Statement testStatement9 = vf.createStatement(testListHeadUri2, RDF.REST, RDF.NIL);
        this.testGraph.add(testStatement9);
        
        try
        {
            @SuppressWarnings("unused")
            final List<Value> results = RdfListUtil.getList(this.testListHeadBNode1, this.testGraph);
            Assert.fail("Did not find expected exception");
        }
        catch(final RuntimeException rex)
        {
            Assert.assertEquals("Found more than one list, possibly due to forking", rex.getMessage());
        }
        
    }

    @Test
    public void testGetListForkedInvalid()
    {
        Statement testStatement1 = vf.createStatement(testListHeadBNode1, RDF.FIRST, testObjectLiteral1);
        this.testGraph.add(testStatement1);

        Statement testStatement2 = vf.createStatement(testListHeadBNode1, RDF.REST, testListHeadUri1);
        this.testGraph.add(testStatement2);
        
        Statement testStatement3 = vf.createStatement(testListHeadUri1, RDF.FIRST, testObjectUri1);
        this.testGraph.add(testStatement3);
        
        Statement testStatement4 = vf.createStatement(testListHeadUri1, RDF.REST, testListHeadBNode2);
        this.testGraph.add(testStatement4);
        
        Statement testStatement5 = vf.createStatement(testListHeadUri1, RDF.REST, testListHeadUri2);
        this.testGraph.add(testStatement5);
        
        try
        {
            @SuppressWarnings("unused")
            final List<Value> results = RdfListUtil.getList(this.testListHeadBNode1, this.testGraph);
            Assert.fail("Did not find expected exception");
        }
        catch(final RuntimeException rex)
        {
            Assert.assertEquals("List structure was not complete", rex.getMessage());
        }
        
    }
}
