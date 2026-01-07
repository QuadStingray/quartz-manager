package dev.quadstingray.quartz.manager.api.model

case class Paging(
  rowsPerPage: Option[Int],
  page: Option[Int]
)

object Paging {
  val DefaultRowsPerPage = 20
}
