package dev.quadstingray.quartz.manager.api.util

import dev.quadstingray.quartz.manager.api.model.PaginationInfo
import dev.quadstingray.quartz.manager.api.model.Paging
import dev.quadstingray.quartz.manager.api.model.Paging.DefaultRowsPerPage
import sttp.tapir._
import sttp.tapir.server.PartialServerEndpoint
import sttp.tapir.typelevel.ParamConcat

object PaginationExtensions {

  final val HeaderPaginationPerPage    = "x-pagination-rows-per-page"
  final val HeaderPaginationPage       = "x-pagination-current-page"
  final val HeaderPaginationCountRows  = "x-pagination-count-rows"
  final val HeaderPaginationPagesCount = "x-pagination-count-pages"

  val pagingParameter = query[Option[Int]]("rowsPerPage")
    .example(Some(DefaultRowsPerPage))
    .description("Number of items returned per page")
    .and(query[Option[Int]]("page").example(Some(1)).description("Desired page of the result set"))
    .mapTo[Paging]

  val pagingHeaderOutput = header[Int](HeaderPaginationCountRows)
    .example(200)
    .description("Total number of records")
    .and(header[Int](HeaderPaginationPerPage).example(20).description("Number of records per page"))
    .and(header[Int](HeaderPaginationPage).example(1).description("Current page"))
    .and(header[Int](HeaderPaginationPagesCount).example(10).description("Total number of pages"))
    .mapTo[PaginationInfo]

  implicit class PaginationExtendedEndpoint[SECURITY_INPUT, INPUT, ERROR_OUTPUT, OUTPUT, R](
    endpoint: Endpoint[SECURITY_INPUT, INPUT, ERROR_OUTPUT, OUTPUT, R]
  ) {
    def addPaginationInput[NEW_INPUT](implicit
      concatInput: ParamConcat.Aux[INPUT, Paging, NEW_INPUT]
    ): Endpoint[SECURITY_INPUT, NEW_INPUT, ERROR_OUTPUT, OUTPUT, R] = {
      endpoint.in(pagingParameter)
    }

    def addPaginationOutput[NEW_OUTPUT](implicit
      concatOutput: ParamConcat.Aux[OUTPUT, PaginationInfo, NEW_OUTPUT]
    ): Endpoint[SECURITY_INPUT, INPUT, ERROR_OUTPUT, NEW_OUTPUT, R] = {
      endpoint.out(pagingHeaderOutput)
    }

    def addPagination[NEW_INPUT, NEW_OUTPUT](implicit
      concatInput: ParamConcat.Aux[INPUT, Paging, NEW_INPUT],
      concatOutput: ParamConcat.Aux[OUTPUT, PaginationInfo, NEW_OUTPUT]
    ): Endpoint[SECURITY_INPUT, NEW_INPUT, ERROR_OUTPUT, NEW_OUTPUT, R] = {
      endpoint.addPaginationInput.addPaginationOutput
    }
  }

  implicit class PaginationExtendedPartialServerEndpoint[SECURITY_INPUT, PRINCIPAL, INPUT, ERROR_OUTPUT, OUTPUT, -R, F[_]](
    endpoint: PartialServerEndpoint[SECURITY_INPUT, PRINCIPAL, INPUT, ERROR_OUTPUT, OUTPUT, R, F]
  ) {
    def addPaginationInput[NEW_INPUT](implicit
      concatInput: ParamConcat.Aux[INPUT, Paging, NEW_INPUT]
    ): PartialServerEndpoint[SECURITY_INPUT, PRINCIPAL, NEW_INPUT, ERROR_OUTPUT, OUTPUT, R, F] = {
      endpoint.in(pagingParameter)
    }

    def addPaginationOutput[NEW_OUTPUT](implicit
      concatOutput: ParamConcat.Aux[OUTPUT, PaginationInfo, NEW_OUTPUT]
    ): PartialServerEndpoint[SECURITY_INPUT, PRINCIPAL, INPUT, ERROR_OUTPUT, NEW_OUTPUT, R, F] = {
      endpoint.out(pagingHeaderOutput)
    }

    def addPagination[NEW_INPUT, NEW_OUTPUT](implicit
      concatInput: ParamConcat.Aux[INPUT, Paging, NEW_INPUT],
      concatOutput: ParamConcat.Aux[OUTPUT, PaginationInfo, NEW_OUTPUT]
    ): PartialServerEndpoint[SECURITY_INPUT, PRINCIPAL, NEW_INPUT, ERROR_OUTPUT, NEW_OUTPUT, R, F] = {
      endpoint.addPaginationInput.addPaginationOutput
    }
  }
}
