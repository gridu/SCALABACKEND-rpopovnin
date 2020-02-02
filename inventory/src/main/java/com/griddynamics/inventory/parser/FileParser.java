package com.griddynamics.inventory.parser;

import java.io.InputStream;
import java.util.List;

public interface FileParser<T> {

    List<T> parse(InputStream inputStream);
}
