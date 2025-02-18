//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.yourmq.base;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ONodeAttr {
    String name() default "";

    String format() default "";

    String timezone() default "";

    boolean asString() default false;

    boolean ignore() default false;

    boolean serialize() default true;

    boolean deserialize() default true;

    boolean incNull() default true;

    boolean flat() default false;
}
