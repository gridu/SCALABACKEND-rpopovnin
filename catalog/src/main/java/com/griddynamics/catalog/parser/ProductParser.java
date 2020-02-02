package com.griddynamics.catalog.parser;

import com.griddynamics.catalog.model.ProductEntity;
import com.univocity.parsers.common.processor.BeanListProcessor;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;

@Component
public class ProductParser implements FileParser<ProductEntity> {

    @Override
    public List<ProductEntity> parse(InputStream inputStream) {
        CsvParserSettings settings = new CsvParserSettings();
        settings.setAutoConfigurationEnabled(true);
        settings.setHeaderExtractionEnabled(true);
        settings.getFormat().setLineSeparator("\n");
        settings.getFormat().setDelimiter(",");
        settings.setMaxCharsPerColumn(100_000);

        BeanListProcessor<ProductEntity> processor = new BeanListProcessor<>(ProductEntity.class);
        settings.setProcessor(processor);

        CsvParser parser = new CsvParser(settings);

        parser.parse(inputStream);

        return processor.getBeans();
    }

}
