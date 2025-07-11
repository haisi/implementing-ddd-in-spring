package li.selman.ddd;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

public class AbstractPropertyTest {
    protected Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    protected YAMLMapper mapper = YAMLMapper.builder()
            .propertyNamingStrategy(PropertyNamingStrategy.KEBAB_CASE)
            .addModule(new ParameterNamesModule())
            .build();
}
