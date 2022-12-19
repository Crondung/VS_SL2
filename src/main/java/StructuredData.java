import java.util.ArrayList;
import java.util.List;


/**
 * helper class for RFC 5424 (https://datatracker.ietf.org/doc/html/rfc5424)
 * compliant log messages as immutable Java objects - structured data (set of
 * key/value-pairs) with some predefined sets according to the standard
 * 
 * @author Sandro Leuchter
 *
 */
public class StructuredData {
    static public class Element {
        private final String name;
        private List<Param> parameters;

        public static Element newTimeQuality(boolean tzKnown,
                boolean isSynced) {
            return newTimeQuality(tzKnown, isSynced, null);
        }

        public static Element newTimeQuality(boolean tzKnown, boolean isSynced,
                Integer syncAccuracy) {
            var e = new Element("timeQuality");
            e.add(new Param("tzKnown", (tzKnown) ? "1" : "0"));
            e.add(new Param("isSynced", (isSynced) ? "1" : "0"));
            if (syncAccuracy != null && !isSynced) {
                e.add(new Param("syncAccuracy", String.valueOf(syncAccuracy)));
            }
            return e;
        }

        public static Element newOrigin(String enterpriseId, String software,
                String swVersion) {
            return newOrigin(new String[] {}, enterpriseId, software,
                    swVersion);
        }

        public static Element newOrigin(String ip, String enterpriseId,
                String software, String swVersion) {
            return newOrigin(new String[] { ip }, enterpriseId, software,
                    swVersion);
        }

        public static Element newOrigin(String[] ip, String enterpriseId,
                String software, String swVersion) {
            var e = new Element("origin");
            for (var p : ip) {
                e = e.add(new Param("ip", p));
            }
            if (enterpriseId != null && !enterpriseId.equals("")) {
                e = e.add(new Param("enterpriseId", enterpriseId));
            }
            if (software != null && !software.equals("")) {
                e = e.add(new Param("software", software));
            }
            if (swVersion != null && !swVersion.equals("")) {
                e = e.add(new Param("swVersion", swVersion));
            }
            return e;
        }

        public static Element newMeta(Integer sequenceId, Integer sysUpTime,
                String language) {
            var e = new Element("meta");
            if (sequenceId != null && sequenceId > 0) {
                e = e.add(new Param("sequenceId",
                        String.valueOf(sequenceId % 2147483647)));
            }
            if (sysUpTime != null && sysUpTime >= 0) {
                e = e.add(new Param("sysUpTime", String.valueOf(sysUpTime)));
            }
            if (language != null && !language.equals("")) {
                e = e.add(new Param("language", language));
            }
            return e;
        }

        public Element(String name) {
            this.name = name;
            this.parameters = new ArrayList<>();
        }

        public Element add(Param parameter) {
            var e = new Element(this.name);
            e.parameters = this.parameters;
            e.parameters.add(parameter);
            return e;
        }

        @Override
        public String toString() {
            var str = "[" + this.name;
            for (var p : this.parameters) {
                str = str + " " + p.toString();
            }
            return str + "]";
        }
    }

    static public class Param {
        private final String name;
        // name: printable US-ASCII string ^[@=\]\"\s]+
        // "@" + private enterpise number "@\d+(\.\d+)*"
        private final String value;

        public Param(String name, String value) {
            this.name = name;   // 7-Bit ASCII
            this.value = value; // UTF-8
        }

        @Override
        public String toString() {
            return this.name + "=\"" + this.value + "\"";
        }
    }

    private List<Element> params;

    public StructuredData() {
        this.params = new ArrayList<>();
    }

    public StructuredData(List<Element> params) {
        this.params = params;
    }

    public String toString() {
        if (this.params.size() == 0) {
            return "-";
        }
        var str = "";
        for (var p : this.params) {
            str = str + p.toString();
        }
        return str;
    }

    public StructuredData add(Element e) {
        var p = this.params;
        p.add(e);
        return new StructuredData(p);
    }
}
