package server.exception;

public class QuizException extends Throwable {
    public QuizException(String message, Throwable cause) {
        super(message, cause);
    }
    //address already in use
    //port?
    //conn refused (extra client)
    //parsing
    //no answer - timeout
    //client left?
}
