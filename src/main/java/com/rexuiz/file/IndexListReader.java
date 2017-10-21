package com.rexuiz.file;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Класс специально для чтения специфического файла с атрибутами
 */
public class IndexListReader {

	public static Map<String, FileDto> read(String indexListPath) {

		Path path = Paths.get(indexListPath);
		Map<String, FileDto> fileDtoMap = new HashMap<>();
		try (BufferedReader reader = Files.newBufferedReader(path, Charset.forName("UTF-8"))) {
			String line;
			while ((line = reader.readLine()) != null) {
				String[] items = line.split("\\|");
				if (items.length == 3) {
					fileDtoMap.put(items[2], new FileDto.Builder(items[0], Integer.parseInt(items[1])).build());
				} else if (items.length == 8) {
					FileDto fileDto = new FileDto.Builder(items[0], Long.parseLong(items[1]))
							.zipSource(items[3])
							.zipSourceName(items[4])
							.zipFilePath(items[5])
							.zipHash(items[6])
							.zipSize(Long.parseLong(items[7]))
							.build();

					fileDtoMap.put(items[2], fileDto);
				}
				System.out.println(line);
			}
		} catch (IOException ex) {
			System.out.println(ex.getLocalizedMessage());
		}
		
		return fileDtoMap.isEmpty() ? Collections.<String, FileDto>emptyMap() : fileDtoMap;
	}
}
