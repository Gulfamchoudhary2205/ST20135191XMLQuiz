package server.data;

import server.exception.QuizException;

import javax.xml.parsers.ParserConfigurationException;

/**
 * Interface for parsing functionality
 */
public interface Parser {
    /**
     * Parses the file on the given path to create a QuestionBank
     * @param path location of the file to parse
     * @return QuestionBank with all the parameters
     * @throws QuizException if any unexpected situation happens
     */
    QuestionBank parseQuestionaire(String path) throws QuizException;
}
