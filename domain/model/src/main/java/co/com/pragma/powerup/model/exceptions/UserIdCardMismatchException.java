package co.com.pragma.powerup.model.exceptions;

import co.com.pragma.powerup.model.utils.Constants;

public class UserIdCardMismatchException extends RuntimeException {
    public UserIdCardMismatchException(String providedIdCard, String expectedIdCard) {
        super(String.format(Constants.USER_ID_CARD_MISMATCH,
                providedIdCard, expectedIdCard));
    }
}
