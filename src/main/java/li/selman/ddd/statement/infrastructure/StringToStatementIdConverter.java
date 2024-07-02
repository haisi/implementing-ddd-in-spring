package li.selman.ddd.statement.infrastructure;

import li.selman.ddd.statement.Statement;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.ConditionalGenericConverter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
class StringToStatementIdConverter implements ConditionalGenericConverter {
    @Override
    public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
        return (sourceType.getType().equals(String.class)
                && targetType.getType().equals(Statement.StatementId.class));
    }

    @Override
    public Set<ConvertiblePair> getConvertibleTypes() {
        return Set.of(new ConvertiblePair(String.class, Statement.StatementId.class));
    }

    @Override
    public @Nullable Object convert(@Nullable Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
        if (source == null) {
            return null;
        }

        String rawStatementId = (String) source;

        return Statement.StatementId.fromString(rawStatementId);
    }
}
