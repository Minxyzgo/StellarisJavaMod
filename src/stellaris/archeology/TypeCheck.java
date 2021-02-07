package stellaris.archeology;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(TypeChecks.class)
public @interface TypeCheck {
    ArcheologyType type() default ArcheologyType.intermediate;
}