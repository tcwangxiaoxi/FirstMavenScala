package river.wang.com.study.cookbook.websocket.listener

import java.util.concurrent.CopyOnWriteArrayList

import org.apache.commons.logging.LogFactory

import scala.collection.JavaConverters._
import scala.reflect.ClassTag
import scala.util.control.NonFatal

/**
  * Created by wxx on 2016/12/20.
  */
trait ListenerBus[L <: AnyRef, E] {

  private val log = LogFactory.getLog(this.getClass)

  val listeners = new CopyOnWriteArrayList[L]

  final def addListener(listener: L) {
    listeners.add(listener)
  }

  final def postToAll(event: E): Unit = {
    val iter = listeners.iterator
    while (iter.hasNext) {
      val listener = iter.next
      try {
        onPostEvent(listener, event)
      } catch {
        case NonFatal(e) =>
          log.error(s"""Listener $listener threw an exception""", e)
      }
    }
  }

  def onPostEvent(listener: L, event: E): Unit

  def findListenersByClass[T <: L : ClassTag](): Seq[T] = {
    val c = implicitly[ClassTag[T]].runtimeClass
    listeners.asScala.filter(_.getClass == c).map(_.asInstanceOf[T])
  }

}
