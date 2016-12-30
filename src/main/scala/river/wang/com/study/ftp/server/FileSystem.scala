package river.wang.com.study.ftp.server

import java.io.File
import java.util

import org.apache.commons.io.FileUtils
import org.apache.commons.io.filefilter.TrueFileFilter
import river.wang.com.study.ftp.model.{Copying, FileInfo, Idle, State}
import river.wang.com.study.ftp.utils.Implicits


/**
  * Created by wxx on 2016/12/30.
  */
class FileSystem(val rootpath: String) extends Implicits {

  val files = new util.HashMap[String, FileInfo]()

  // TODO: 这里应该使用atomic通过内存事务管理，具体可以看书第七章
  def init() = {
    files.clear()
    val rootDir = new File(rootpath)
    val all = TrueFileFilter.INSTANCE

    val fileIterator = FileUtils.iterateFilesAndDirs(rootDir, all, all).asScala

    for (file <- fileIterator) {
      val info = FileInfo(file)
      files.put(info.path, info)
    }
  }

  // TODO: 这里也应该使用atomic通过内存事务管理，具体可以看书第七章
  def getFileList(dir: String): scala.collection.mutable.Map[String, FileInfo] = {
    files.asScala.filter(_._2.parent == dir)
  }

  private def copyFileWithState(info: FileInfo, newState: State, dest: String, destFile: File) = {
    files.put(info.path, info.copy(state = newState))
    files.put(dest, FileInfo.creating(destFile, info.size))
    // TODO: 这里也应该使用Txn.afterCommit通过内存事务管理，具体可以看书第七章
    //    copyOnDisk()
  }

  // TODO: 这里也应该使用atomic通过内存事务管理，具体可以看书第七章
  def copyFile(src: String, dest: String) = {
    val srcFile = new File(src)
    val destFile = new File(dest)
    val info = files.get(srcFile)
    if (files.containsKey(destFile)) sys.error("Destination exists.")
    info.state match {
      case Idle =>
        copyFileWithState(info, Copying(1), dest, destFile)
      case s@Copying(_) =>
        copyFileWithState(info, s.inc, dest, destFile)
    }
  }
}
