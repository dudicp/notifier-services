package com.patimer.notifier.service.engine.worker;

import com.patimer.notifier.model.AccountEntity;
import com.patimer.notifier.model.SearcherEntity;
import com.patimer.notifier.service.engine.SearcherService;
import com.patimer.notifier.service.notification.NotificationService;
import org.jsoup.helper.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SearcherWorkerFactory
{
    private SearcherService searcherService;
    private NotificationService notificationService;

    @Autowired
    public SearcherWorkerFactory(SearcherService searcherService, NotificationService notificationService)
    {
        Validate.notNull(searcherService);
        Validate.notNull(notificationService);

        this.searcherService = searcherService;
        this.notificationService = notificationService;
    }

    public Runnable createSearcherWorker(AccountEntity accountEntity, List<SearcherEntity> searcherEntities)
    {
        return new RunnableSearcherWorker(accountEntity, searcherEntities, searcherService, notificationService);
    }
}
