package ca.powercool.powercoolhub.utilities;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/**
 * SessionUtility provides utility functions to help
 * debugging session-related issues.
 */
public class SessionUtility {
    /**
     * Print out session info.
     * 
     * @param request
     */
    public static void dumpSession(HttpServletRequest request) {
        HttpSession session = request.getSession();
        System.out.println("Session ID: " + session.getId());
        System.out.println("Creation Time: " + session.getCreationTime());
        System.out.println("Last Accessed Time: " + session.getLastAccessedTime());

        System.out.println("Session Attributes:");
        session.getAttributeNames().asIterator().forEachRemaining(attributeName -> {
            Object attributeValue = session.getAttribute(attributeName);
            System.out.println(attributeName + " : " + attributeValue);
        });
    }
}
