package io.github.buzzxu.spuddy.util;

import com.google.common.collect.Maps;
import org.springframework.beans.factory.config.YamlProcessor;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.util.ClassUtils;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public abstract class Yml {


    public static Optional<Map<String,Object>> load(Resource resource)throws IOException {
        if (ClassUtils.isPresent("org.yaml.snakeyaml.Yaml", null)) {
            Processor processor = new Processor(resource);
            Map<String, Object> source = processor.process();
            return !source.isEmpty() ? Optional.of(source) : Optional.empty();
        }
       return Optional.empty();
    }

    public static PropertySource<Map<String,Object>> load(String name, Resource resource)
            throws IOException {
        var source = load(resource);
        if(source.isPresent()){
            return new MapPropertySource(name, source.get());
        }
        return null;
    }



    private static class Processor extends YamlProcessor {
        Processor(Resource resource) {
            setResources(resource);
        }

        public Map<String, Object> process() {
            final Map<String, Object> result = Maps.newLinkedHashMapWithExpectedSize(3);
            process((properties, map) -> result.putAll(getFlattenedMap(map)));
//            process((properties, map) -> merge(result, map));
            return result;
        }

        @SuppressWarnings("unchecked")
        private void merge(Map<String, Object> output, Map<String, Object> map) {
            map.forEach((key, value) -> {
                Object existing = output.get(key);
                if (value instanceof Map && existing instanceof Map) {
                    Map<String, Object> result = new LinkedHashMap<>((Map<String, Object>) existing);
                    merge(result, (Map) value);
                    output.put(key, result);
                }
                else {
                    output.put(key, value);
                }
            });
        }
    }
}
