package com.tbrown.twitterStream
import scala.concurrent.duration._
import scalaz.concurrent.Task

trait TweetRepositoryComponent {
  def tweetRepository: TweetRepository
}

trait TweetRepository {
  def store(tweet: Tweet): Unit
  def search(size: Int, from: Int, searchTerms: List[String]): Task[List[Tweet]]
}

trait MemoryBasedTweetRepositoryComponent extends TweetRepositoryComponent {
  val tweetRepository = new MemoryBasedTweetRepository{}

  trait MemoryBasedTweetRepository extends TweetRepository {
    var storage: List[Tweet] = List.empty[Tweet]
    def store(tweet: Tweet): Unit = storage = storage :+ tweet
    def search(size: Int, from: Int, searchTerms: List[String]): Task[List[Tweet]] = {
      Task(storage.filter(t => searchTerms.map(t.text.contains(_)).reduce(_ || _)))
    }
  }
}

// trait ElasticsearchTweetRepositoryComponent extends TweetRepositoryComponent with ActorModule {
//   val tweetRepository: TweetRepository = new ElasticsearchTweetRepository{}

//   trait ElasticsearchTweetRepository extends TweetRepository with ElasticSearchConfig {
//     implicit val system = StreamingActorSystem
//     val pipeline = sendReceive

//     def store(tweet: Tweet) = pipeline {
//       Post(s"http://$url/tweets/tweet/", tweet)
//     }

//     def search(size: Int, from: Int, searchTerms: List[String]): Future[List[Tweet]] = {
//       val searchQuery = if (searchTerms.isEmpty) "(*)" else s"(${searchTerms.mkString(" ")})"
//       val esQuery = s"""{"size":${size},"from":${from},"query":{"query_string":{"default_field":"text","query":"${searchQuery} AND lang:en","default_operator":"AND"}},"sort":{"id":"desc"}}"""
//       val tweetListPipeline = sendReceive ~> unmarshal[EsSearchResult]
//       tweetListPipeline(Post(s"http://$url/tweets/tweet/_search", esQuery))
//         .map(res => res.hits.hits.map(_._source))
//     }
//   }
// }
