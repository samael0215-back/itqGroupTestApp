package itqGroupTestApp.core.config;

import itqGroupTestApp.core.servises.BackgroundService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StartupRunner implements ApplicationRunner {

    private final BackgroundService backgroundService;

    @Override
    public void run(ApplicationArguments args) {
        backgroundService.startSubmitWorker();
        backgroundService.startApproveWorker();
    }
}
