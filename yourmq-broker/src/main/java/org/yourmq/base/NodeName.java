package org.yourmq.base;

import java.lang.annotation.*;

/**
 * @deprecated
 */
@Deprecated
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NodeName {
    String value();
}
