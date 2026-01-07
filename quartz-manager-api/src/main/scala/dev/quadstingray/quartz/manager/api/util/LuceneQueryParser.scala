package dev.quadstingray.quartz.manager.api.util

import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.queryparser.classic.QueryParser
import org.apache.lucene.search.BooleanClause
import org.apache.lucene.search.BooleanQuery
import org.apache.lucene.search.TermQuery
import org.apache.lucene.search.Query
import org.apache.lucene.index.Term
import scala.jdk.CollectionConverters._
import scala.util.Try

object LuceneQueryParser {

  /**
   * Parses a Lucene query string and returns a function that filters items based on field extractors.
   *
   * @param queryString The Lucene query string (e.g., "jobGroup:batch AND status:SUCCESS")
   * @param fieldExtractors Map of field names to functions that extract the field value from an item
   * @tparam T The type of items to filter
   * @return A filter function that returns true if the item matches the query
   */
  def parseAndFilter[T](
    queryString: Option[String],
    fieldExtractors: Map[String, T => Option[String]]
  ): T => Boolean = {
    queryString match {
      case None | Some("") => (_: T) => true // No filter if query is empty
      case Some(query) =>
        Try {
          val parser = new QueryParser("_default", new StandardAnalyzer())
          val luceneQuery = parser.parse(query)
          (item: T) => matchesQuery(item, luceneQuery, fieldExtractors)
        }.getOrElse((_: T) => true) // If parsing fails, return all items
    }
  }

  private def matchesQuery[T](
    item: T,
    query: Query,
    fieldExtractors: Map[String, T => Option[String]]
  ): Boolean = {
    query match {
      case bq: BooleanQuery =>
        val clauses = bq.clauses().asScala
        val mustClauses = clauses.filter(_.occur() == BooleanClause.Occur.MUST)
        val shouldClauses = clauses.filter(_.occur() == BooleanClause.Occur.SHOULD)
        val mustNotClauses = clauses.filter(_.occur() == BooleanClause.Occur.MUST_NOT)

        // All MUST clauses must match
        val mustMatch = mustClauses.forall(clause => matchesQuery(item, clause.query(), fieldExtractors))

        // At least one SHOULD clause must match (if there are any SHOULD clauses)
        val shouldMatch = shouldClauses.isEmpty || shouldClauses.exists(clause => matchesQuery(item, clause.query(), fieldExtractors))

        // No MUST_NOT clause should match
        val mustNotMatch = mustNotClauses.forall(clause => !matchesQuery(item, clause.query(), fieldExtractors))

        mustMatch && shouldMatch && mustNotMatch

      case tq: TermQuery =>
        val term = tq.getTerm
        val fieldName = term.field()
        val searchValue = term.text().toLowerCase

        fieldExtractors.get(fieldName) match {
          case Some(extractor) =>
            extractor(item) match {
              case Some(value) => value.toLowerCase.contains(searchValue)
              case None        => false
            }
          case None => false
        }

      case _ => true // For other query types, return true (can be extended)
    }
  }
}
