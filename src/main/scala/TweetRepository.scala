package com.tbrown.twitterStream

import doobie.imports._
import fs2.Task
import fs2.util.Monad
import org.joda.time.DateTime

import scalaz._
import Scalaz._

//trait TweetRepositoryComponent {
//  def tweetRepository: TweetRepository
//
//  trait TweetRepository {
//    def store(tweet: Tweet): Unit
//    def search(size: Int, from: Int, searchTerms: List[String]): Task[List[Tweet]]
//  }
//}
//
//
//trait MemoryBasedTweetRepositoryComponent extends TweetRepositoryComponent {
//  val tweetRepository = new MemoryBasedTweetRepository{}
//
//  trait MemoryBasedTweetRepository extends TweetRepository {//needs to manage itself
//    var storage: List[Tweet] = List.empty[Tweet]
//    def store(tweet: Tweet): Unit = storage = storage :+ tweet
//    def search(size: Int, from: Int, searchTerms: List[String]): Task[List[Tweet]] = {
//      Task(storage.filter(t => searchTerms.map(t.text.contains(_)).reduce(_ || _)))
//    }
//  }
//}


abstract class TweetRepository[M[_]: Monad] {
  def create(t: Tweet): M[Tweet]
}

class InMemoryRepository extends TweetRepository[Task] {
  import DAO._

  def create(t: Tweet): Task[Tweet] = Task.delay(Tweet)
}

object DAO {

  implicit val DateTimeMeta: Meta[DateTime] =
    Meta[java.sql.Timestamp].nxmap(
      ts => new DateTime(ts.getTime()),
      dt => new java.sql.Timestamp(dt.getMillis())
    )

  def withInit[A](a: ConnectionIO[A]): ConnectionIO[A] =
    a.exceptSomeSqlState {
      case SqlState("42S02") => init.run *> a
    }

  def insertTweet(tweet: Tweet): Update0 =
    sql"""
          INSERT INTO tweets (retweeted, lang, id, timestamp, created_at, favorite_count, text, source, retweet_count, id_str)
          VALUES (${tweet.retweeted}, ${tweet.lang}, ${tweet.id}, ${tweet.timestamp}, ${tweet.createdAt},
      ${tweet.favoriteCount}, ${tweet.text}, ${tweet.source}, ${tweet.retweetCount}, ${tweet.idStr})
    """.update

  def init: Update0 =
    sql"""
      CREATE TABLE tweets (
        retweeted      BOOLEAN       NOT NULL,
        lang           VARCHAR       NOT NULL,
        id             BIGINT        NOT NULL,
        timestamp      VARCHAR       NOT NULL,
        created_at     TIMESTAMP     NOT NULL,
        favorite_count INTEGER       NOT NULL,
        text           VARCHAR       NOT NULL,
        source         VARCHAR       NOT NULL,
        retweet_count  INTEGER       NOT NULL,
        id_str         VARCHAR       NOT NULL
      )
    """.update
}
