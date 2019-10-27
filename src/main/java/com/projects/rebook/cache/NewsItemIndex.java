package com.projects.rebook.cache;

import com.projects.rebook.model.NewsItem;
import com.projects.rebook.repository.ImagesRepository;
import com.projects.rebook.repository.NewsItemRepository;
import com.projects.rebook.repository.UserRepository;
import com.projects.rebook.service.impl.NewsItemServiceImpl;
import com.projects.rebook.utils.DateTimeUtils;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.List;

@Component
@Transactional
public class NewsItemIndex {

  private Logger logger = LoggerFactory.getLogger(NewsItemServiceImpl.class);

  public static Map<String, NewsItem> newsItemMap = new HashMap<>();

  public static NewsItem newsItem = new NewsItem();

  private static Integer partition = DateTimeUtils.getPartition();

  @Autowired
  NewsItemRepository newsItemRepository;

  @Autowired
  UserRepository userRepository;

  @Autowired
  ImagesRepository imagesRepository;

  @Autowired
  CacheDataService cacheDataService;

  @PostConstruct
  public void mapToIndexNewsItem() {
    indexNewsItem(partition);
    mapToIndexNews(partition);
  }

  public void mapToIndexNews(Integer partition) {
    List<NewsItem> newsItemList = cacheDataService.findNewsByPartition(partition);
    if (newsItemList != null && !newsItemList.isEmpty()) {
      logger.info("NewsItemIndex newsItemMap - {}", NewsItemIndex.newsItemMap);
      NewsItemIndex.newsItemMap.clear();
      for (int i = 0; i < newsItemList.size(); i++) {
        NewsItemIndex.newsItemMap.put(newsItemList.get(i).getUrl(), newsItemList.get(i));
      }
    }
  }

  public void indexNewsItem(Integer partition) {
    NewsItemIndex.newsItem = cacheDataService.findLastRow(partition);
  }

}
