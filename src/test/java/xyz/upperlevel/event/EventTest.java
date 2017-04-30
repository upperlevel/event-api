package xyz.upperlevel.event;

import lombok.Data;
import xyz.upperlevel.event.impl.def.EventManager;

public class EventTest {

    public static void main(String... args) {
        EventManager manager = new EventManager();

        manager.register(new IdkListener());

        manager.call(new MailReadyEvent("The pen is on the table"));
    }

    @Data
    public static class MailEvent implements Event {
        public final String text;
    }

    @Data
    public static class MailReadyEvent extends MailEvent {
        public MailReadyEvent(String text) {
            super(text);
        }
    }


    public static class IdkListener implements Listener {

        @EventHandler(priority = EventPriority.LOW)
        public void onMailReady(MailEvent event) {
            System.out.println("ready! -> " + event.getText());
        }

        @EventHandler(priority = EventPriority.HIGH)
        protected void onHiddenMailReady(MailReadyEvent event) {
            System.out.println("shhh! mail ready! -> " + event.getText());
        }
    }
}
