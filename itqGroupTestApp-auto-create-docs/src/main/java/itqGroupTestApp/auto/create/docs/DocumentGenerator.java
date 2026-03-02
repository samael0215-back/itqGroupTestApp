package itqGroupTestApp.auto.create.docs;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@Slf4j
@SpringBootApplication
public class DocumentGenerator implements CommandLineRunner {

    @Value("${document.count}")
    private int documentCount;

    @Autowired
    private RestTemplate restTemplate;

    private final String apiUrl = "http://localhost:8081/documents/new";

    public static void main(String[] args) {
        SpringApplication.run(DocumentGenerator.class, args);
    }

    @Override
    public void run(String... args) {
        log.info("Starting document generation for {} documents", documentCount);

        for (int i = 0; i < documentCount; i++) {
            createDocument(i);
            logProgress(i + 1);
        }
    }

    private void createDocument(int index) {
        String title = "Document " + (index + 1);
        Long authorId = 1L;

        String requestUrl = String.format("%s?title=%s&author=%d", apiUrl, title, authorId);

        ResponseEntity<String> response = restTemplate.postForEntity(requestUrl, null, String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            log.info("Successfully created document: {}", title);
        } else {
            log.error("Error while creating document '{}': Status code: {}", title, response.getStatusCode());
        }
    }

    private void logProgress(int completed) {
        log.info("Progress: {} of {} documents created", completed, documentCount);
    }
}
