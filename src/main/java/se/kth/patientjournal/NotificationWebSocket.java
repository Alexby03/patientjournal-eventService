package se.kth.patientjournal;

import io.smallrye.jwt.auth.principal.JWTParser;
import io.smallrye.jwt.auth.principal.ParseException;
import jakarta.inject.Inject;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint("/ws/messages/{userId}")
@ApplicationScoped
public class NotificationWebSocket {

    private final Map<String, Session> sessions = new ConcurrentHashMap<>();

    @Inject
    JWTParser jwtParser;

    @OnOpen
    public void onOpen(Session session, @PathParam("userId") String userId) {

        try {
            String token = getTokenFromSession(session);

            if (token == null) {
                closeSession(session, "No token provided");
                return;
            }

            JsonWebToken jwt = jwtParser.parse(token);System.out.println("DEBUG: JWT Parsed successfully. Subject: " + jwt.getSubject()); // 3. Lyckades parsning?

            String subject = jwt.getSubject();
            if (!subject.equals(userId)) {
                closeSession(session, "Token ID does not match URL ID");
                return;
            }

            sessions.put(userId, session);

        } catch (Exception e) {
            e.printStackTrace();
            closeSession(session, "Internal Error");
        }
    }


    @OnClose
    public void onClose(Session session, @PathParam("userId") String userId) {
        sessions.remove(userId);
    }

    @OnError
    public void onError(Session session, @PathParam("userId") String userId, Throwable throwable) {
        sessions.remove(userId);
    }

    public void sendToUser(String userId, String messageJson) {
        Session session = sessions.get(userId);
        if (session != null && session.isOpen()) {
            session.getAsyncRemote().sendText(messageJson);
        }
    }

    private String getTokenFromSession(Session session) {
        Map<String, List<String>> params = session.getRequestParameterMap();
        if (params.containsKey("access_token")) {
            return params.get("access_token").get(0);
        }
        return null;
    }

    private void closeSession(Session session, String reason) {
        try {
            session.close(new CloseReason(CloseReason.CloseCodes.VIOLATED_POLICY, reason));
        } catch (Exception e) {

        }
    }
}
