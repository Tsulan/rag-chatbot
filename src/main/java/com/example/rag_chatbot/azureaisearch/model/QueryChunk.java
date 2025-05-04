package com.example.rag_chatbot.azureaisearch.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
public class QueryChunk {

    private String id;

    private String pageLink;

    private String subChunk;

    private String masterChunk;

    public static List<QueryChunk> toQueryChunks(ChunkedDocument chunkedDocument) {
        List<QueryChunk> queryChunks = new ArrayList<>();
        String pageLink = chunkedDocument.getPageLink();

        for (MasterChunk master : chunkedDocument.getMasterChunks()) {
            String masterText = master.getText();
            for (SubChunk sub : master.getSubChunks()) {
                QueryChunk qc = new QueryChunk();
                qc.setId(sub.getId());
                qc.setPageLink(pageLink);
                qc.setSubChunk(sub.getText());
                qc.setMasterChunk(masterText);
                queryChunks.add(qc);
            }
        }

        return queryChunks;
    }
}
