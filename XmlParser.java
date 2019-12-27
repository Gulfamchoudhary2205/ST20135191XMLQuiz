package server.data;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import server.exception.QuizException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * XmlParser is the implementation of the Parser interface that parses the xml files
 */
public class XmlParser implements Parser {

    @Override
    public QuestionBank parseQuestionaire(String path) throws QuizException {
        //load the file from the path
        File fXmlFile = new File(path);
        //instantiate the factory
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        //crate the builder
        DocumentBuilder dBuilder;
        try {
            dBuilder = dbFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new QuizException("DocumentBuilder cannot be created", e);
        }
        //parse the file to the doc
        Document doc;
        try {
            doc = dBuilder.parse(fXmlFile);
        } catch (SAXException e) {
            throw new QuizException("Error while parsing the xml", e);
        } catch (IOException e) {
            throw new QuizException("IO error while parsing the xml", e);
        }
        //get the root element
        Element rootElement = doc.getDocumentElement();
        //normalization
        rootElement.normalize();
        //read the number of players
        int players = Integer.parseInt(rootElement.getAttribute(XmlNames.PLAYERS.getName()));
        //placeholder for the questions that are going to be read
        List<Question> questions = new ArrayList<>();
        //get Question elements from the doc
        NodeList qList = doc.getElementsByTagName(XmlNames.QUESTION.getName());
        //for each
        for (int i = 0; i < qList.getLength(); i++) {
            //load the node
            Node qNode = qList.item(i);
            //if it's element
            if (qNode.getNodeType() == Node.ELEMENT_NODE) {
                //cast node to element
                Element qElement = (Element) qNode;
                //read the id
                int id = Integer.parseInt(qElement.getAttribute(XmlNames.ID.getName()));
                //read the id of an answer
                int answerId = Integer.parseInt(qElement.getAttribute(XmlNames.ANSWER_ID.getName()));
                //read the text of the question
                String text = qElement.getElementsByTagName(XmlNames.TEXT.getName()).item(0).getTextContent();
                //placeholder for given answers
                List<Answer> answers = new ArrayList<>();
                //get the list of answer nodes from the doc
                NodeList aList = qElement.getElementsByTagName(XmlNames.ANSWERS.getName()).item(0).getChildNodes();
                //for each node
                for (int j = 0; j < aList.getLength(); j++) {
                    //load it
                    Node aNode = aList.item(j);
                    //if it's element
                    if (aNode.getNodeType() == Node.ELEMENT_NODE) {
                        //cast node to element
                        Element aElement = (Element) aNode;
                        //read the id of an answer
                        int ansId = Integer.parseInt(aElement.getAttribute(XmlNames.ID.getName()));
                        //read the text of an answer
                        String ansTxt = aElement.getTextContent();
                        //create a new Answer and put it to the list
                        answers.add(new Answer(ansId, ansTxt));
                    }
                }
                //create a new Question and put it to the list
                questions.add(new Question(id, answerId, text, answers));
            }
        }
        //return the QuestionBank
        return new QuestionBank(players, questions);
    }
}
