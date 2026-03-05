package itqGroupTestApp.core.servises;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import itqGroupTestApp.core.entity.Action;
import itqGroupTestApp.core.entity.Document;
import itqGroupTestApp.core.entity.History;
import itqGroupTestApp.core.entity.User;
import itqGroupTestApp.core.ropositories.HistoryRepository;

import java.util.List;

@Slf4j
@Service
public class HistoryService {

    private final HistoryRepository historyRepository;

    @Autowired
    public HistoryService(HistoryRepository historyRepository) {
        this.historyRepository = historyRepository;
    }

    public List<History> getHistoryByDocumentId(Long id) {
        return historyRepository.findAllByDocument_Id(id);
    }

    public void createHistory(Document document, User user, Action action) {
        History history = new History();
        history.setDocument(document);
        history.setAction(action);
        history.setInitiator(user);
        historyRepository.save(history);
    }
}
