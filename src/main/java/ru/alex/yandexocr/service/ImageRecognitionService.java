package ru.alex.yandexocr.service;

import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.MetadataException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class ImageRecognitionService {

    private final RestTemplateService restTemplateService;

    public ImageRecognitionService(RestTemplateService restTemplateService) {
        this.restTemplateService = restTemplateService;
    }

    public String recognize(MultipartFile file) throws IOException, ImageProcessingException, MetadataException {
        byte[] fileData = ImageUtils.correctOrientation(file);
        byte[] base64FileData = Base64.encodeBase64(fileData);
        String body = createRequestBody(file, base64FileData);
        String responseBody = restTemplateService.postForEntity(body);
        return buildHtmlContent(responseBody);
    }

    private String createRequestBody(MultipartFile file, byte[] base64FileData) {
        return String.format("""
        {
          "mimeType": "%s",
          "languageCodes": ["*"],
          "model": "page",
          "content": "%s"
        }""", file.getContentType(), new String(base64FileData));
    }

    private String buildHtmlContent(String responseBody) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(responseBody);
        JsonNode blocks = jsonNode.path("result").path("textAnnotation").path("blocks");
        double totalConfidence = 0.0;
        int lineCount = 0;
        StringBuilder htmlContent = new StringBuilder();

        // Рассчитываем общую точность и количество строк
        for (JsonNode block : blocks) {
            for (JsonNode line : block.path("lines")) {
                double lineConfidence = line.path("confidence").asDouble(1.0);
                totalConfidence += lineConfidence;
                lineCount++;
            }
        }

        // Вычисляем среднюю точность для всего документа
        double averageConfidence = calculateAverageConfidence(totalConfidence, lineCount);

        // Добавляем информацию о точности вверху документа
        htmlContent.append(String.format("<div class='ocr-confidence'>Средняя точность распознавания: %.2f%%</div>", averageConfidence * 100));

        // Добавляем остальной HTML-контент после информации о точности
        htmlContent.append("<div class='ocr-container'>");
        for (JsonNode block : blocks) {
            for (JsonNode line : block.path("lines")) {
                htmlContent.append(buildLineHtml(line));
            }
        }
        htmlContent.append("</div>");

        return htmlContent.toString();
    }


    private String buildLineHtml(JsonNode line) {
        StringBuilder htmlContent = new StringBuilder("<div class='ocr-line'>");
        for (JsonNode word : line.path("words")) {
            htmlContent.append(buildWordHtml(word));
        }
        htmlContent.append("</div>");
        return htmlContent.toString();
    }

    private String buildWordHtml(JsonNode word) {
        String text = word.path("text").asText();
        int fontSize = word.path("fontSize").asInt(12);
        boolean isBold = word.path("bold").asBoolean(false);
        boolean isItalic = word.path("italic").asBoolean(false);
        JsonNode boundingBox = word.path("boundingBox");
        JsonNode topLeft = boundingBox.path("vertices").get(0);
        JsonNode bottomRight = boundingBox.path("vertices").get(2);
        int x = topLeft.path("x").asInt();
        int y = topLeft.path("y").asInt();
        int width = bottomRight.path("x").asInt() - x;
        int height = bottomRight.path("y").asInt() - y;
        String style = String.format("font-size: %dpx; left: %dpx; top: %dpx; width: %dpx; height: %dpx;", fontSize, x, y, width, height);
        if (isBold) {
            style += " font-weight: bold;";
        }
        if (isItalic) {
            style += " font-style: italic;";
        }
        return String.format("<span class='ocr-word' style='%s'>%s</span> ", style, text);
    }

    private double calculateAverageConfidence(double totalConfidence, int lineCount) {
        return lineCount > 0 ? totalConfidence / lineCount : 1.0;
    }
}
