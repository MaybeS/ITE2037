package imf.data;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class DataParser
{	
	public String rootName;
	public List<DataObject> nodes = new ArrayList<DataObject>();
	
	public DataParser(String name, int options)
	{
		try {
			File file = new File(name);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder;
			dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(file);
			
			rootName = doc.getDocumentElement().getNodeName();
			NodeList nList =  doc.getElementsByTagName("object");
			
			for(int i  =0 ; i < nList.getLength(); ++i)
			{
				Node nNode = nList.item(i);
				if(nNode.getNodeType() == Node.ELEMENT_NODE)
				{
					Element eElement = (Element) nNode;
					NamedNodeMap attrs = eElement.getAttributes();
					DataObject oNode = new DataObject(eElement.getNodeName());
					
					for(int j = 0; j < attrs.getLength(); ++j)
						oNode.insert(attrs.item(j).getNodeName(), attrs.item(j).getNodeValue());
					
					nodes.add(oNode);
				}
			}
		} catch (ParserConfigurationException e) {
			return;
		} catch (SAXException | IOException e) {
			return;
		}
	}

	public void loop(Consumer<DataObject> func)
	{
		for(DataObject oi : nodes)
			func.accept(oi);;
	}
}