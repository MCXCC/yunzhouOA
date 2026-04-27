package com.yunzhou.console.handler;

import com.meilisearch.sdk.Client;
import com.meilisearch.sdk.Index;
import com.meilisearch.sdk.model.SearchResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class MeilisearchHandler {

    private final Client meilisearchClient;

    public void createIndex(String indexUid) throws Exception {
        meilisearchClient.index(indexUid);
        log.info("创建索引: {}", indexUid);
    }

    public void addDocuments(String indexUid, List<Map<String, Object>> documents) throws Exception {
        Index index = meilisearchClient.index(indexUid);
        String json = new com.google.gson.Gson().toJson(documents);
        index.addDocuments(json);
        log.info("添加文档到索引: {}, 数量: {}", indexUid, documents.size());
    }

    public void updateDocument(String indexUid, Map<String, Object> document) throws Exception {
        Index index = meilisearchClient.index(indexUid);
        String json = new com.google.gson.Gson().toJson(List.of(document));
        index.addDocuments(json);
        log.info("更新文档到索引: {}", indexUid);
    }

    public void deleteDocument(String indexUid, String documentId) throws Exception {
        Index index = meilisearchClient.index(indexUid);
        index.deleteDocument(documentId);
        log.info("删除文档: {} from 索引: {}", documentId, indexUid);
    }

    public SearchResult search(String indexUid, String query) throws Exception {
        Index index = meilisearchClient.index(indexUid);
        return index.search(query);
    }

    public void deleteAllDocuments(String indexUid) throws Exception {
        Index index = meilisearchClient.index(indexUid);
        index.deleteAllDocuments();
        log.info("清空索引所有文档: {}", indexUid);
    }
}
