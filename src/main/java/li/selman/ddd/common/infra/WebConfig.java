package li.selman.ddd.common.infra;

import java.util.List;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.GenericConverter;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.Printer;
import org.springframework.format.datetime.standard.DateTimeFormatterRegistrar;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
class WebConfig implements WebMvcConfigurer {

    private final List<Printer<?>> printers;
    private final List<GenericConverter> converters;

    WebConfig(List<Printer<?>> printers, List<GenericConverter> converters) {
        this.printers = printers;
        this.converters = converters;
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        DateTimeFormatterRegistrar registrar = new DateTimeFormatterRegistrar();
        registrar.setUseIsoFormat(true);
        registrar.registerFormatters(registry);

        printers.forEach(registry::addPrinter);
        converters.forEach(registry::addConverter);
    }
}
