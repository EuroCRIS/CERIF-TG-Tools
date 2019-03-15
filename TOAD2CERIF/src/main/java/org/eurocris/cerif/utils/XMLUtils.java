/**
 * This file comes from the DSpace software, see
 * https://github.com/DSpace/DSpace/blob/master/dspace-api/src/main/java/org/dspace/app/util/XMLUtils.java
 */
package org.eurocris.cerif.utils;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.RandomAccess;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Simple class to read information from small XML using DOM manipulation
 * 
 * @author Andrea Bollini
 * 
 */
public class XMLUtils
{
    /**
     * 
     * @param rootElement
     *            the starting node
     * @param subElementName
     *            the name of the subelement to find
     * @return the list of all DOM Element with the provided name direct child
     *         of the starting node
     */
    public static List<Element> getElementList(Element dataRoot, String name)
    {
        NodeList list = dataRoot.getElementsByTagName(name);
        List<Element> listElements = new ArrayList<Element>();
        for (int i = 0; i < list.getLength(); i++)
        {
            Element item = (Element) list.item(i);
            if (item.getParentNode().equals(dataRoot))
            {
                listElements.add(item);
            }
        }
        return listElements;
    }

    /**
     * 
     * @param dataRoot
     *            the starting node
     * @param name
     *            the name of the sub element
     * @param attr
     *            the attribute to get
     * @return the value of the attribute for the sub element with the specified
     *         name in the starting node
     */
    public static String getElementAttribute(Element dataRoot, String name,
            String attr)
    {
        Element element = getSingleElement(dataRoot, name);
        String attrValue = null;
        if (element != null)
        {
            attrValue = element.getAttribute(attr);
            if (StringUtils.isNotBlank(attrValue))
            {
                attrValue = attrValue.trim();
            }
            else
                attrValue = null;
        }
        return attrValue;
    }

    /**
     * 
     * @param dataRoot
     *            the starting node
     * @param name
     *            the name of the sub element
     * @return the text content of the sub element with the specified name in
     *         the starting node
     */
    public static String getElementValue(Element dataRoot, String name)
    {
        Element element = getSingleElement(dataRoot, name);
        String elementValue = null;
        if (element != null)
        {
            elementValue = element.getTextContent();
            if (StringUtils.isNotBlank(elementValue))
            {
                elementValue = elementValue.trim();
            }
            else
                elementValue = null;
        }
        return elementValue;
    }

    /**
     * Return the first element child with the specified name
     * 
     * @param dataRoot
     *            the starting node
     * @param name
     *            the name of sub element to look for
     * @return the first child element or null if no present
     */
    public static Element getSingleElement(Element dataRoot, String name)
    {
        List<Element> nodeList = getElementList(dataRoot, name);
        Element element = null;
        if (nodeList != null && nodeList.size() > 0)
        {
            element = (Element) nodeList.get(0);
        }
        return element;
    }

    /**
     * 
     * @param rootElement
     *            the starting node
     * @param subElementName
     *            the name of the subelement to find
     * @return a list of string including all the text contents of the sub
     *         element with the specified name. If there are not sub element
     *         with the supplied name the method will return null
     */
    public static List<String> getElementValueList(Element rootElement,
            String subElementName)
    {
        if (rootElement == null)
            return null;

        List<Element> subElements = getElementList(rootElement, subElementName);
        if (subElements == null)
            return null;

        List<String> result = new LinkedList<String>();
        for (Element el : subElements)
        {
            if (StringUtils.isNotBlank(el.getTextContent()))
            {
                result.add(el.getTextContent().trim());
            }
        }
        return result;
    }

    /**
     * root/subElement[]/field1, field2, fieldN
     * 
     * @param rootElement
     *            the starting node
     * @param subElementName
     *            the name of the sub element to work on
     * @param fieldsName
     *            the names of the sub-sub-elements from which get the text
     *            content
     * @return a list of array strings. The length of the array is equals to the
     *         number of fields required. For any fields the first textual value
     *         found in the sub element is used, null if no value is present
     */
    public static List<String[]> getElementValueArrayList(Element rootElement,
            String subElementName, String... fieldsName)
    {
        if (rootElement == null)
            return null;

        List<Element> subElements = getElementList(rootElement, subElementName);
        if (subElements == null)
            return null;

        List<String[]> result = new LinkedList<String[]>();
        for (Element el : subElements)
        {
            String[] tmp = new String[fieldsName.length];
            for (int idx = 0; idx < fieldsName.length; idx++)
            {
                tmp[idx] = XMLUtils.getElementValue(el, fieldsName[idx]);
            }
            result.add(tmp);
        }
        return result;
    }

	public static Element cloneElementAs( Element srcEl, Document dstDoc, String elName ) {
		if ( srcEl.getNodeName().equals( elName ) ) {
			if ( srcEl.getOwnerDocument() == dstDoc ) {
				return (Element) srcEl.cloneNode( true );
			} else {
				return (Element) dstDoc.importNode( srcEl, true );
			}
		} else {
			final Element dstEl = dstDoc.createElement( elName );
			final NodeList srcChildren = srcEl.getChildNodes();
			final int n = srcChildren.getLength();
			for ( int i = 0; i < n; ++i ) {
				final Node srcChild = srcChildren.item( i );
				final Node dstChild = dstDoc.importNode( srcChild, true );
				dstEl.appendChild( dstChild );
			}
			return dstEl;
		}
	}
    
    /**
     * root/subElement[]/fieldName
     * 
     * @param rootElement
     *            the starting node
     * @param subElementName
     *            the name of the sub element to work on
     * @param fieldsName
     *            the names of the sub-sub-element from which get the text
     *            content
     * @return a list of strings. The first textual value
     *         found in the sub element is used, null if no value is present
     */
    public static List<String> getElementValueList(Element rootElement,
            String subElementName, String fieldName) {
    	List<String[]> tmp = getElementValueArrayList(rootElement, subElementName, fieldName);
    	List<String> result = new ArrayList<String>(tmp.size());
    	for (String[] t : tmp) {
    		result.add(t[0]);
    	}
    	return result;
    }
    
    public static List<Element> getChildElements( final Element parent ) {
    	final NodeList nl = parent.getChildNodes();
    	final int n = nl.getLength();
		final List<Element> list = new ArrayList<>( n );
    	for ( int i = 0; i < n; ++i ) {
    		final Node node = nl.item( i );
    		if ( node instanceof Element ) {
    			list.add( (Element) node );
    		}
    	}
    	return list;
    }
    
	public static List<Node> asList( final NodeList nl ) {
		return new NodeListWrapper( nl );
	}

	private static final class NodeListWrapper extends AbstractList<Node> implements RandomAccess {
		
		private final NodeList list;

		NodeListWrapper( final NodeList l ) {
			list = l;
		}

		public Node get( final int index ) {
			return list.item( index );
		}

		public int size() {
			return list.getLength();
		}
		
	}

	public static List<Element> asElementList( final NodeList nl ) {
		return new NodeListElementWrapper( nl );
	}

	private static final class NodeListElementWrapper extends AbstractList<Element> implements RandomAccess {
		
		private final NodeList list;

		NodeListElementWrapper( final NodeList l ) {
			list = l;
		}

		public Element get( final int index ) {
			return (Element) list.item( index );
		}

		public int size() {
			return list.getLength();
		}
		
	}

}