package com.tbrown.twitterStream


object EmojiTracker extends ElementCounterThatManagesMemory[Emoji]

trait ElementCounter[A] {
  protected var map = collection.mutable.Map[A, Int]()

  def topElements(n: Int = 3): List[A] =
    map.toList.foldLeft(List[(A, Int)]())( (b, a) =>
      if (b.size < n) a :: b
      else {
        val min = b.map(_._2).min
        val b_ = b.sortWith( _._2 < _._2 )
        if (a._2 > min) a :: b_.drop(1)
        else b
      }
    ).sortWith(_._2 > _._2).map(_._1)

  def incrElementCount(elem: A) = map.update(elem, map.getOrElse(elem, 0) + 1)
}

trait ElementCounterThatManagesMemory[A] extends ElementCounter[A] {
  private val arbitraryDeletionMin = 5
  private var lastAddition = currentTime
  private def currentTime = (System.currentTimeMillis / 1000.0)
  private def timeToClear = (currentTime - lastAddition) > 5
  private def clearOldData = if (timeToClear) map = map.filter(_._2 > arbitraryDeletionMin)

  override def incrElementCount(elem: A) = {
    incrElementCount(elem)
    clearOldData
    lastAddition
  }
}
