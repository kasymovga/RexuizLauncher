package com.rexuiz.file;

/**
 * Сущность для хранения записи о файле и его атрибутов
 */
public class FileDto {
	private final String hash;
	private final long size;
	private String zipSource;
	private String zipSourceName;
	private String zipFilePath;
	private String zipHash;
	private long zipSize;

	private FileDto(Builder builder) {
		this.hash = builder.hash;
		this.size = builder.size;
		this.zipSource = builder.zipSource;
		this.zipSourceName = builder.zipSourceName;
		this.zipFilePath = builder.zipFilePath;
		this.zipHash = builder.zipHash;
		this.zipSize = builder.zipSize;
	}


	public static class Builder {
		private final String hash;
		private final long size;
		private String zipSource;
		private String zipSourceName;
		private String zipFilePath;
		private String zipHash;
		private long zipSize;

		public Builder(String hash, long size) {
			this.hash = hash;
			this.size = size;
		}

		public Builder zipSource(String zipSource) {
			this.zipSource = zipSource;
			return this;
		}

		public Builder zipSourceName(String zipSourceName) {
			this.zipSourceName = zipSourceName;
			return this;
		}

		public Builder zipFilePath(String zipFilePath) {
			this.zipFilePath = zipFilePath;
			return this;
		}

		public Builder zipHash(String zipHash) {
			this.zipHash = zipHash;
			return this;
		}

		public Builder zipSize(long zipSize) {
			this.zipSize = zipSize;
			return this;
		}

		public FileDto build() {
			return new FileDto(this);
		}
	}

	@Override
	public String toString() {
		return "FileDto{" +
				"hash='" + hash + '\'' +
				", size=" + size +
				", zipSource='" + zipSource + '\'' +
				", zipSourceName='" + zipSourceName + '\'' +
				", zipFilePath='" + zipFilePath + '\'' +
				", zipHash='" + zipHash + '\'' +
				", zipSize=" + zipSize +
				'}';
	}
}
