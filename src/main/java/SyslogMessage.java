import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * RFC 5424 (https://datatracker.ietf.org/doc/html/rfc5424) compliant log
 * messages as immutable Java objects
 * 
 * @author Sandro Leuchter
 *
 */
public class SyslogMessage implements Serializable {
    private static final long serialVersionUID = -5895573029109990861L;
    private final Facility fac;
    private final Severity sev;
    private final AsciiChars.L255 host;
    private final AsciiChars.L048 appName;
    private final AsciiChars.L128 procId;
    private final AsciiChars.L032 msgId;
    private final StructuredData data;
    private final Message message;

    public SyslogMessage(Facility fac, Severity sev, AsciiChars.L255 host,
            AsciiChars.L048 appName, AsciiChars.L128 procId,
            AsciiChars.L032 msgId, StructuredData data, Message message) {
        this.fac = fac;
        this.sev = sev;
        this.host = host;
        this.appName = appName;
        this.procId = procId;
        this.msgId = msgId;
        this.data = data;
        this.message = message;
    }

    public Facility fac() {
        return this.fac;
    }


    public Severity sev() {
        return sev;
    }

    public AsciiChars.L255 host() {
        return host;
    }

    public AsciiChars.L048 appName() {
        return appName;
    }

    public AsciiChars.L128 procId() {
        return procId;
    }

    public AsciiChars.L032 msgId() {
        return msgId;
    }

    public StructuredData data() {
        return data;
    }

    public Message message() {
        return message;
    }

    public static int version() {
        return VERSION;
    }


    public static enum Facility {
        KERNEL, USER, MAIL_SYSTEM, SYS_DAEMON, SECURITY1, INTERNAL, PRINTER,
        NEWS, UUCP, CLOCK1, SECURITY2, FTP, NTP, AUDIT, ALERT, CLOCK2, LOCAL0,
        LOCAL1, LOCAL2, LOCAL3, LOCAL4, LOCAL5, LOCAL6, LOCAL7;
    }

    public static enum Severity {
        EMERGENCY, ALERT, CRITICAL, ERROR, WARNING, NOTICE, INFORMATIONAL,
        DEBUG;
    }

    public static interface Message {
        public Object message();

        public int length();
    }

    public static class BinaryMessage implements Message {
        private Byte[] message;

        public BinaryMessage(Byte[] message) {
            this.message = message;
        }

        @Override
        public String toString() {
            return message.toString();
        }

        @Override
        public Object message() {
            return this.message;
        }

        @Override
        public int length() {
            return this.message.length;
        }
    }

    public static class TextMessage implements Message {
        private String message; // UTF8

        public TextMessage(String message) {
            this.message = message;
        }

        @Override
        public String toString() {
            return "\u00EF\u00BB\u00BF" + message.toString();
        }

        @Override
        public Object message() {
            return this.message;
        }

        @Override
        public int length() {
            return this.message.length();
        }
    }

    static final int VERSION = 1; // RFC 5424, Mar 2009

    @Override
    public String toString() {
        var prival = String.valueOf(fac().ordinal() * 8 + sev().ordinal());
        var d = "";
        if (data() != null) {
            d = " " + data();
        }
        var m = "";
        if (message() != null && message().message() != null
                && message().length() > 0) {
            m = " " + message();
        }
        var timestamp = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX")
                .format(new Date());
        return "<" + prival + ">" + VERSION + " " + timestamp + " "
                + host().toString() + " " + appName().toString() + " "
                + procId().toString() + " " + msgId().toString() + d + m;
    }
}
