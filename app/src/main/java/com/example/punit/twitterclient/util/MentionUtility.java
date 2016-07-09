package com.example.punit.twitterclient.util;

public class MentionUtility {

    /**
     * Checks if the current word being edited is a valid token (e.g. starts with @ and has no spaces)
     * @param text - all text being edited in input
     * @param cursor - current position of text change
     * @return is valid
     */
    public static boolean isValidToken(CharSequence text, int cursor){
        for (int i=cursor; i>=0; i--){
            if (text.charAt(i) == '@') return true;
            if (text.charAt(i) == ' ') return false;
        }
        return false;
    }

    /**
     * Fetches the current token being edited - assumes valid token (use isValidToken to confirm)
     * @param text
     * @param cursor
     * @return
     */
    public static String getToken(CharSequence text, int cursor){
        int start=findTokenStart(text, cursor);
        int end=findTokenEnd(text, cursor);
        return text.subSequence(start, end).toString();
    }

    /**
     * In the current input text, finds the start position of the current token (iterates backwards from current position
     * until finds the token prefix "@")
     * @param text - all text being edited in input
     * @param cursor - current position of text change
     * @return position of token start
     */
    private static int findTokenStart(CharSequence text, int cursor) {
        int i = cursor;
        while (i > 0 && text.charAt(i - 1) != '@') { i--; }
        return i;
    }

    /**
     * In the current input text, finds the position of the end of the current token (iterates forwards from current
     * position
     * until finds the the end, e.g. a space or end of all input)
     * @param text - all text being edited in input
     * @param cursor - current position of text change
     * @return position of token end
     */
    private static int findTokenEnd(CharSequence text, int cursor) {
        int i = cursor;
        int len = text.length();
        while (i < len && text.charAt(i) != ' ' && text.charAt(i) != ',' && text.charAt(i) != '.'  ) {
            i++;
        }
        return i;
    }
}
