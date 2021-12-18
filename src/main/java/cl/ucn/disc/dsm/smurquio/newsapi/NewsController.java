/*
 * Copyright (c) 2021 Sebastián Murquio Castillo
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package cl.ucn.disc.dsm.smurquio.newsapi;

import cl.ucn.disc.dsm.smurquio.newsapi.model.News;
import com.kwabenaberko.newsapilib.models.Article;
import com.kwabenaberko.newsapilib.models.response.ArticleResponse;
import com.kwabenaberko.newsapilib.network.APIClient;
import com.kwabenaberko.newsapilib.network.APIService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.threeten.bp.ZoneId;
import org.threeten.bp.ZonedDateTime;
import retrofit2.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The Controller of News
 * @author Sebastián Murquio Castillo
 */
@Slf4j
@RestController
public class NewsController {

  /**
   * The Repo of News
   */
  private final NewsRepository newsRepository;

  /**
   * The Constructor of NewsController
   * @param newsRepository to use.
   */
  public NewsController(NewsRepository newsRepository){
    this.newsRepository = newsRepository;
  }

  /**
   *
   * @return all the News in the backend.
   */
  @GetMapping("/v1/news")
  public List<News> all(@RequestParam(required = false, defaultValue = "false") Boolean reload){

    // is reload -> get news from NewsApi.org
    if(reload){

      this.reloadNewsFromNewsApi();
    }

    // Equals to SELECT * FROM News;
    return this.newsRepository.findAll();
  }

  /**
   * Get the News from NewsAPI and save into the database.
   */
  private void reloadNewsFromNewsApi() {
    //WARNING: Just for test
    final String API_KEY = "74609a6b455346e38cf11b8ad39e4d61";
    final int pageSize = 100;

    //1. Create the APIService from APIClient
    final APIService apiService = APIClient.getAPIService();

    //2. Build the request params
    final Map<String,String> reqParams = new HashMap<>();
    //The API key
    reqParams.put("apikey", API_KEY);

    //Filter by category
    reqParams.put("category", "technology");

    //The number of results to return per page (request). 20 default, max 100
    reqParams.put("pageSize", String.valueOf(pageSize));

    //3. Call the request
    try {
      Response<ArticleResponse> articlesResponse =
          apiService.getTopHeadlines(reqParams).execute();

      //Exito!
      if(articlesResponse.isSuccessful()){
        assert articlesResponse.body() != null;
        List<Article> articles = articlesResponse.body().getArticles();

        // List<Article> to List<News>
        List<News> news = new ArrayList<>();

        if(articles != null){
          for(Article article : articles){

            News _new = toNews(article);

            news.add(_new);

            //Call the findNewsByIdEquals method to search if the current News is already in the database.
            List<News> repoNews = this.newsRepository.findNewsByIdEquals(_new.getId());

            //if the list isn´t empty, then remove the News in news list.
            if(!repoNews.isEmpty()){
              news.remove(_new);
            }
          }

          //4. Save into the local database.
          this.newsRepository.saveAll(news);
        }
      }

    } catch (IOException e) {
      log.error("Getting the Articles from NewsAPI", e);
    }

  }

  /**
   * Convert Article to News.
   *
   * @param article to convert.
   * @return the News
   */
  private static News toNews(Article article){

    // Protection Author
    if(article.getAuthor() == null || article.getAuthor().length() < 3){
      article.setAuthor("No author*");
    }

    // Protection Title
    if(article.getTitle() == null || article.getTitle().length() < 3){
      article.setAuthor("No Title*");
    }

    // Protection Description
    if(article.getDescription() == null || article.getDescription().length() < 4){
      article.setDescription("No Description*");
    }

    //Parse the date and fix the Zone
    ZonedDateTime publishedAt = ZonedDateTime
        .parse(article.getPublishedAt())
        // Correct from UTC to LocalTime (Chile)
        .withZoneSameInstant(ZoneId.of("-3"));

    return new News(
        article.getTitle(),
        article.getSource().getName(),
        article.getAuthor(),
        article.getUrl(),
        article.getUrlToImage(),
        article.getDescription(),
        article.getDescription(),
        publishedAt
    );
  }

  /**
   *
   * @param id of News to retrieve.
   * @return the News.
   */
  @GetMapping("/v1/news/{id}")
  public News one(@PathVariable final Long id){
    return this.newsRepository.findById(id)
        .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "News not found :("));
  }
}
