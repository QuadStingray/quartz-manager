package dev.quadstingray.quartz.manager.api.model

case class PaginationInfo(
  totalRows: Int,
  rowsPerPage: Int,
  currentPage: Int,
  totalPages: Int
)
