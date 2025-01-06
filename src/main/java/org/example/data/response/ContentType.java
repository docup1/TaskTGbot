package org.example.data.response;

public enum ContentType {
    // Текстовые форматы
    TEXT_PLAIN("text/plain"),
    TEXT_HTML("text/html"),
    TEXT_CSS("text/css"),
    TEXT_JAVASCRIPT("text/javascript"),

    // Форматы данных JSON и XML
    APPLICATION_JSON("application/json"),
    APPLICATION_XML("application/xml"),
    APPLICATION_JAVASCRIPT("application/javascript"),

    // Форматы для изображений
    IMAGE_PNG("image/png"),
    IMAGE_JPEG("image/jpeg"),
    IMAGE_GIF("image/gif"),
    IMAGE_WEBP("image/webp"),
    IMAGE_SVG_XML("image/svg+xml"),

    // Форматы для аудио и видео
    AUDIO_MPEG("audio/mpeg"),
    AUDIO_OGG("audio/ogg"),
    VIDEO_MP4("video/mp4"),
    VIDEO_WEBM("video/webm"),
    VIDEO_OGG("video/ogg"),

    // Форматы для форм и данных
    APPLICATION_FORM_URLENCODED("application/x-www-form-urlencoded"),
    MULTIPART_FORM_DATA("multipart/form-data"),

    // Документы и другие форматы
    APPLICATION_PDF("application/pdf"),
    APPLICATION_ZIP("application/zip"),
    APPLICATION_OCTET_STREAM("application/octet-stream");

    private final String type;

    ContentType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}