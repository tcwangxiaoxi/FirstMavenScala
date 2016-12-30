package river.wang.com.study.ftp.model

import java.io.File

sealed trait State

case object Created extends State

case object Idle extends State

case class Copying(n: Int) extends State {
  def inc: State = Copying(n + 1)

  def dec: State = if (n > 1) Copying(n - 1) else Idle
}

case object Deleted extends State

case class FileInfo(path: String, name: String, parent: String, modified: String, isDir: Boolean, size: Long, state: State)

object FileInfo {
  def apply(file: File): FileInfo = new FileInfo(file.getCanonicalPath, file.getName, file.getParent,
    // TODO:不知道modified屬性应该填写什么...
    "", file.isDirectory, file.getTotalSpace, Idle)

  def creating(file: File, size: Long): FileInfo = new FileInfo(file.getCanonicalPath, file.getName, file.getParent,
    // TODO:同样不知道modified屬性应该填写什么...
    "", file.isDirectory, size, Created)
}


