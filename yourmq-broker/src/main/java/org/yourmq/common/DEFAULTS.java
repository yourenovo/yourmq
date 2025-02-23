
package org.yourmq.common;

import org.yourmq.base.*;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Locale;
import java.util.TimeZone;

public class DEFAULTS {
    public static final int DEF_FEATURES;
    public static final String DEF_TYPE_PROPERTY_NAME = "@type";
    public static final TimeZone DEF_TIME_ZONE;
    public static final ZoneOffset DEF_OFFSET;
    public static final Locale DEF_LOCALE;
    public static String DEF_DATETIME_FORMAT;
    public static final Fromer DEF_OBJECT_FROMER;
    public static final Toer DEF_OBJECT_TOER;
    public static final Fromer DEF_STRING_FROMER;
    public static final Toer DEF_STRING_TOER;
    public static final Toer DEF_JSON_TOER;

    public DEFAULTS() {
    }

    static {
        DEF_FEATURES = Feature.QuoteFieldNames.code;
        DEF_TIME_ZONE = TimeZone.getDefault();
        DEF_OFFSET = OffsetDateTime.now().getOffset();
        DEF_LOCALE = Locale.getDefault();
        DEF_DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
        DEF_OBJECT_FROMER = new ObjectFromer();
        DEF_OBJECT_TOER = new ObjectToer();
        DEF_STRING_FROMER = new JsonFromer();
        DEF_STRING_TOER = new JsonToer();
        DEF_JSON_TOER = new JsonToer();
    }
}
