package xyz.upperlevel.event;

import lombok.Data;
import org.junit.Test;
import xyz.upperlevel.event.impl.def.EventManager;

import static org.junit.Assert.assertEquals;

public class EventInheritanceTest implements Listener {

    private int mails, received, sent;

    @Data
    public static class MailEvent implements Event {
    }

    @Data
    public static class MailReceiveEvent extends MailEvent {
    }

    @Data
    public static class MailSendEvent extends MailEvent {
    }

    private void reset() {
        mails = 0;
        received = 0;
        sent = 0;
    }

    @Test
    public void test() {
        EventManager manager = new EventManager();
        manager.register(this);

        reset();
        manager.call(new MailEvent());
        assertEquals(1, mails);
        assertEquals(0, received);
        assertEquals(0, sent);

        reset();
        manager.call(new MailReceiveEvent());
        assertEquals(1, mails);
        assertEquals(1, received);
        assertEquals(0, sent);

        reset();
        manager.call(new MailSendEvent());
        assertEquals(1, mails);
        assertEquals(0, received);
        assertEquals(1, sent);
    }

    @EventHandler
    public void onMail(MailEvent event) {
        mails++;
    }

    @EventHandler
    public void onMailReceive(MailReceiveEvent event) {
        received++;
    }

    @EventHandler
    public void onMailSend(MailSendEvent event) {
        sent++;
    }
}
