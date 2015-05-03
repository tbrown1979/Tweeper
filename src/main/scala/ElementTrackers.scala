package com.tbrown.twitterStream

object EmojiTracker extends ManagedMemoryCounter[Emoji]
object HashtagTracker extends ManagedMemoryCounter[String]

trait ElemCounter[A] {
  def topElements(n: Int = 3): Future[List[A]]
  def incr(elem: A): Unit
}

trait MemoryCounter[A] extends ElemCounter[A] {
  protected var map = collection.mutable.Map[A, Int]()

  def topElements(n: Int = 3): Future[List[A]] =
    future {
      val topElementList =
        map.toList.foldLeft(List[(A, Int)]())( (b, a) =>
          if (b.size < n) a :: b
          else {
            val min = b.map(_._2).min
            val b_ = b.sortWith( _._2 < _._2 )
            if (a._2 > min) a :: b_.drop(1)
            else b
          }
        ).sortWith(_._2 > _._2).map(_._1)
      topElementList
    }

  def incr(elem: A) = map.update(elem, map.getOrElse(elem, 0) + 1)
}

trait ManagedMemoryCounter[A] extends MemoryCounter[A] {
  private val arbitraryDeletionMinimum = 5
  private var lastClear = currentTime
  private def currentTime = (System.currentTimeMillis / 1000.0)
  private def totalWipe = map = collection.mutable.Map[A, Int]()
  private def timeToClear = (currentTime - lastClear) > 120
  private def clearOldData =
    if (timeToClear) {
      map = map.filter(_._2 > arbitraryDeletionMinimum)
      lastClear = currentTime
    }

  override def incr(elem: A): Unit = {
    super.incr(elem)
    clearOldData
  }
}
