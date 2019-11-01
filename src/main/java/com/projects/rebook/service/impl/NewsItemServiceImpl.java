package com.projects.rebook.service.impl;

import com.projects.rebook.bean.Response.CommonResponse;
import com.projects.rebook.bean.Response.CommonResponse.Fail;
import com.projects.rebook.bean.Response.NewsResponseDTO;
import com.projects.rebook.cache.CacheDataService;
import com.projects.rebook.cache.NewsItemIndex;
import com.projects.rebook.model.*;
import com.projects.rebook.repository.*;
import com.projects.rebook.service.NewsItemService;
import com.projects.rebook.utils.DateTimeUtils;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class NewsItemServiceImpl implements NewsItemService {

  private static Logger logger = LoggerFactory.getLogger(NewsItemServiceImpl.class);

  @Autowired
  private NewsItemRepository newsRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private LikeRepository likeRepository;

  @Autowired
  private CommentRepository commentRepository;

  @Autowired
  private ShareRepository shareRepository;

  @Autowired
  private PropertyAdressRepository propertyAdressRepository;

  @Autowired
  private ContactOwnerRepository contactOwnerRepository;

  @Autowired
  private PropertyProjectRepository propertyProjectRepository;

  @Autowired
  CacheDataService cacheDataService;

  private int returnCode = 1;
  private String returnMessage = "success";

  private static Integer currentPartition = DateTimeUtils.getPartition();

  @Override
  public CommonResponse getAllNewsItem() {
    try {
      Map<String, NewsItem> newsMap = NewsItemIndex.newsItemMap;
      List<NewsResponseDTO> newsResponseDTOList = new ArrayList<>();

      if (!newsMap.isEmpty() && newsMap.size() >= 20) {
        for (Map.Entry<String, NewsItem> entry : newsMap.entrySet()) {
          NewsItem newsItem = entry.getValue();
          if (newsItem != null) {
            newsResponseDTOList.add(mapNewsToNewsResponseDTO(newsItem));
          }
        }
      }
      else {
        List<NewsItem> newsItemList = newsRepository.findNewsByPartition(currentPartition);
        for (NewsItem newsItem : newsItemList) {
          newsResponseDTOList.add(mapNewsToNewsResponseDTO(newsItem));
        }
      }

      logger.info("NewsItemServiceImpl getAllNewsItem response - {}, size - {}", newsResponseDTOList, newsResponseDTOList.size());

      return new CommonResponse<>(this.returnCode, this.returnMessage, newsResponseDTOList);
    } catch (Exception ex) {
      logger.error("NewsItemServiceImpl getAllNewsItem Exception: "+ ex);
      return new CommonResponse.Fail("NewsItemServiceImpl getAllNewsItem Exception.");
    }
  }

  @Override
  public CommonResponse getAllNewsByUser(Long userID) throws IOException {
    try {
      List<NewsItem> newsItemList;
      List<NewsResponseDTO> newsResponseDTOList = new ArrayList<>();
      Optional<User> user = userRepository.findById(userID);
      if (user.isPresent()) {
        newsItemList = newsRepository.findAllByUser(user.get());
        for (NewsItem newsItem : newsItemList) {
          newsResponseDTOList.add(mapNewsToNewsResponseDTO(newsItem));
        }
      }
      return new CommonResponse<>(this.returnCode, this.returnMessage, newsResponseDTOList);
    }
    catch (Exception ex) {
      ex.printStackTrace();
      logger.error("NewsItemServiceImpl getAllNewsByUser Exception: ", ex);
      return new Fail("Lấy thông tin bài viết của user thất bại.");
    }
  }

  private NewsResponseDTO mapNewsToNewsResponseDTO(NewsItem newsItem) {
    NewsResponseDTO newsResponseDTO = new NewsResponseDTO();
    newsResponseDTO.setImageUser(newsItem.getUser().getImageUrl());
    newsResponseDTO.setUsername(newsItem.getUser().getName());
    newsResponseDTO.setTitleNews(newsItem.getTitle());

    if (newsItem.getSummary() != null) {
      newsResponseDTO.setSummaryNews(newsItem.getSummary());
    }

    if (newsItem.getDescription() != null) {
      newsResponseDTO.setDescriptionNews(newsItem.getDescription());
    }

    newsResponseDTO.setPubDate(newsItem.getPostedDate());
    newsResponseDTO.setPrice(newsItem.getPrice());
    newsResponseDTO.setArea(newsItem.getArea());

    if (newsItem.getPropertyAddressId() != null) {
      Optional<PropertyAddress> propertyAddress = propertyAdressRepository.findById(currentPartition,
          newsItem.getPropertyAddressId());
      propertyAddress.ifPresent(address -> newsResponseDTO.setAddress_prop(address.getSummary()));
    }

    newsResponseDTO.setImageUrlList(newsItem.getImages());
    newsResponseDTO.setNewsId(newsItem.getId());
    newsResponseDTO.setUserId(newsItem.getUser().getId());

    if (newsItem.getContactOwnerId() != null) {
      Optional<ContactOwner> contactOwner = contactOwnerRepository.findById(currentPartition,
          newsItem.getContactOwnerId());
      contactOwner.ifPresent(contact -> {
        newsResponseDTO.setContactEmail(contact.getEmail());
        newsResponseDTO.setContactName(contact.getContactName());
        newsResponseDTO.setContactPhone(contact.getPhoneNumber());
      });

    }

    if (newsItem.getPropertyProjectId() != null) {
      Optional<PropertyProject> propertyProject = propertyProjectRepository.findById(currentPartition,
          newsItem.getPropertyProjectId());
      propertyProject.ifPresent(project -> {
        newsResponseDTO.setProjectOwner(project.getProjectOwner());
        newsResponseDTO.setProjectSize(project.getProjectSize());
        newsResponseDTO.setProjectName(project.getProjectName());
      });
    }

    List<LikeNews> likeNewsList = likeRepository.findByNewsItemId(newsItem.getId());
    newsResponseDTO.setLikeNewsList(likeNewsList);

    List<Comment> commentList = commentRepository.findByNewItemId(newsItem.getId());
    newsResponseDTO.setCommentList(commentList);

    List<ShareNews> shareNewsList = shareRepository.findByNewItemId(newsItem.getId());
    newsResponseDTO.setShareNewsList(shareNewsList);

    return newsResponseDTO;
  }
}
