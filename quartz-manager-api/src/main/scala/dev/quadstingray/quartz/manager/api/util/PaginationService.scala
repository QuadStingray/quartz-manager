package dev.quadstingray.quartz.manager.api.util

import dev.quadstingray.quartz.manager.api.model.Paging.DefaultRowsPerPage
import dev.quadstingray.quartz.manager.api.model.PaginationInfo

object PaginationService {

  /**
   * Paginates a list and returns the requested page along with pagination info
   *
   * @param list The full list to paginate
   * @param page The page number (1-based)
   * @param rowsPerPage Number of rows per page
   * @return Tuple of (paginated list, pagination info)
   */
  def listToPage[T](list: List[T], page: Int, rowsPerPage: Int): (List[T], PaginationInfo) = {
    val actualPage        = Math.max(1, page)
    val actualRowsPerPage = if (rowsPerPage <= 0) DefaultRowsPerPage else rowsPerPage
    val totalRows         = list.size
    val totalPages        = Math.max(1, Math.ceil(totalRows.toDouble / actualRowsPerPage).toInt)
    val safePage          = Math.min(actualPage, totalPages)
    val startIndex        = (safePage - 1) * actualRowsPerPage
    val endIndex          = Math.min(startIndex + actualRowsPerPage, totalRows)

    val paginatedList = if (startIndex < totalRows) {
      list.slice(startIndex, endIndex)
    } else {
      List.empty[T]
    }

    val paginationInfo = PaginationInfo(
      totalRows = totalRows,
      rowsPerPage = actualRowsPerPage,
      currentPage = safePage,
      totalPages = totalPages
    )

    (paginatedList, paginationInfo)
  }
}
