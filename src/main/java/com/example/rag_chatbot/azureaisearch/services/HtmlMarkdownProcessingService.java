package com.example.rag_chatbot.azureaisearch.services;

import com.vladsch.flexmark.html2md.converter.FlexmarkHtmlConverter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Safelist;
import org.springframework.stereotype.Service;

@Service
public class HtmlMarkdownProcessingService {

    public String cleanHtml(String html) {
        Document dirty = Jsoup.parse(html);

        return Jsoup.clean(dirty.html(), Safelist.relaxed());
    }

    public String convertToMarkdown(String html) {
        return FlexmarkHtmlConverter.builder().build().convert(html);
    }
}
