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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * The Controller of News
 * @author Sebastián Murquio Castillo
 */
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
  public List<News> getNews(){
    // Equals to SELECT * FROM News;
    final List<News> theNews = this.newsRepository.findAll();
    //TODO: show the news in console.
    return theNews;
  }

  /**
   *
   * @param id of News to retrieve.
   * @return the News.
   */
  @GetMapping("/v1/news/{id}")
  public News one(@PathVariable final Long id){
    //FIXME: Change the RuntimeException to 404
    return this.newsRepository.findById(id)
        .orElseThrow(()-> new RuntimeException("News Not Found :("));
  }
}