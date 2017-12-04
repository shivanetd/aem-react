package com.sinnerschrader.aem.react.tsgenerator.maven;

import java.util.List;

import org.apache.maven.plugin.logging.Log;

import com.sinnerschrader.aem.react.tsgenerator.descriptor.ClassDescriptor;
import com.sinnerschrader.aem.react.tsgenerator.descriptor.EnumDescriptor;
import com.sinnerschrader.aem.react.tsgenerator.fromclass.GeneratorFromClass;
import com.sinnerschrader.aem.react.tsgenerator.generator.PathMapper;

import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner;
import io.github.lukehutch.fastclasspathscanner.scanner.ScanResult;
import lombok.Builder;

@Builder
public class Scanner {

	private String basePackage;
	private Class<?> annotationClass;
	private Log log;

	public void scan(Processor processor, EnumProcessor enumProcessor) {
		FastClasspathScanner scanner = new FastClasspathScanner(basePackage);
		ScanResult scanResult = scanner.scan();
		List<String> aClasses = scanResult.getNamesOfClassesWithAnnotation(annotationClass);
		for (String clazzName : aClasses) {
			try {
				Class<?> clazz = Class.forName(clazzName);
				PathMapper mapper = new PathMapper(clazz.getName());
				if (Enum.class.isAssignableFrom(clazz)) {
					EnumDescriptor cd = GeneratorFromClass.createEnumDescriptor((Class<Enum>)clazz);
					enumProcessor.apply(cd);
				}else {
					ClassDescriptor cd = GeneratorFromClass.createClassDescriptor(clazz, mapper);
					processor.apply(cd);
				}
			} catch (ClassNotFoundException e) {
				log.error(String.format("class not found %s.", e.getMessage()));
			}

		}

	}
}