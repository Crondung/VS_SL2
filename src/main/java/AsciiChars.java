/**
 * helper class for RFC 5424 (https://datatracker.ietf.org/doc/html/rfc5424)
 * compliant log messages as immutable Java objects - representation of a subset
 * of printable strings of specific length
 * 
 * @author Sandro Leuchter
 *
 */
public abstract class AsciiChars {
    private final String value;

    public String value() {
        return this.value;
    }

    protected AsciiChars(int length, String value) {
        if (value != null) {
            if (value.length() > length) {
                throw new IllegalArgumentException(
                        "Stringlänge = " + value.length() + " > " + length);
            }
            for (int c : value.getBytes()) {
                if (c < 33 || c > 126) {
                    throw new IllegalArgumentException(
                            "Stringinhalt nicht printable US-ASCII ohne Space");
                }
            }
        }
        this.value = value;
    }

    @Override
    public String toString() {
        if (value() == null || value().length() == 0) {
            return "-";
        } else {
            return value();
        }
    }

    static public final class L004 extends AsciiChars {
        public L004(String value) {
            super(4, value);
        }
    }

    static public final class L012 extends AsciiChars {
        public L012(String value) {
            super(12, value);
        }
    }

    static public final class L032 extends AsciiChars {
        public L032(String value) {
            super(32, value);
        }
    }

    static public final class L048 extends AsciiChars {
        public L048(String value) {
            super(48, value);
        }
    }

    static public final class L128 extends AsciiChars {
        public L128(String value) {
            super(128, value);
        }
    }

    static public final class L255 extends AsciiChars {
        public L255(String value) {
            super(255, value);
        }
    }
}
