package stellaris.archeology;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TypeChecks {
    TypeCheck[] value();
}