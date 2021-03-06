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

package cl.ucn.disc.dsm.smurquio.newsapi.model;

import lombok.Getter;
import net.openhft.hashing.LongHashFunction;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * The News Class.
 *
 * @author Sebastián Murquio Castillo
 */
@Entity
public final class News {

  /**
   * Primary Key.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Getter
  private Long key;

  //@Column(unique = true)
  @Getter
  private Long id;

  @Getter
  private String title;

  @Getter
  private String source;

  @Getter
  private String author;

  @Getter
  private String url;

  @Getter
  private String urlImage;

  @Getter
  private String description;

  @Getter
  private String content;

  @Getter
  private String publishedAt;

  public News( ){
    //Nothing here
  }

  /**
   * The Constructor of News.
   * @param title can't be null.
   * @param source can't be null.
   * @param author can't be null.
   * @param url can be null.
   * @param urlImage can be null.
   * @param description can't be null.
   * @param content can't be null.
   * @param publishedAt can't be null.
   */
  public News(final String title,
              final String source,
              final String author,
              final String url,
              final String urlImage,
              final String description,
              final String content,
              final String publishedAt) {

    // Title
    if(title == null || title.length() < 2) {
      throw new IllegalArgumentException("Title Required.");
    }
    this.title = title;

    // Source
    if(source == null || source.length() < 2) {
      throw new IllegalArgumentException("Source Required.");
    }
    this.source = source;

    // Author
    if(author == null || author.length() < 3) {
      throw new IllegalArgumentException("Author Required.");
    }
    this.author = author;

    // ID: Hashing(title + | + source + | + author)
    this.id = LongHashFunction.xx().hashChars(title + "|" + source +  "|"+ author);

    // Url
    this.url = url;

    // Image Url
    this.urlImage = urlImage;

    // Description
    if(description == null || description.length() < 4) {
      throw new IllegalArgumentException("Description Required.");
    }
    this.description = description;

    // Content
    if(content == null || content.length() < 2) {
      throw new IllegalArgumentException("Content Required.");
    }
    this.content = content;

    //Published At
    if(publishedAt == null){
      throw new IllegalArgumentException("Published At Required");
    }
    this.publishedAt = publishedAt;
  }
}
