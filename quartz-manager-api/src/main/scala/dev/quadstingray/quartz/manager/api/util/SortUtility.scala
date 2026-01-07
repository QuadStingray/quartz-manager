package dev.quadstingray.quartz.manager.api.util

import scala.math.Ordering

object SortUtility {

  /** Sorts a list based on a list of sort strings. Sort strings are field names, optionally prefixed with '-' for descending order.
    *
    * @param items
    *   The list to sort
    * @param sortFields
    *   List of sort field specifications (e.g., List("-executionTime", "jobName"))
    * @param fieldExtractors
    *   Map of field names to functions that extract comparable values
    * @tparam T
    *   The type of items to sort
    * @return
    *   The sorted list
    */
  def sort[T](items: List[T], sortFields: Option[List[String]], fieldExtractors: Map[String, T => Option[Comparable[Any]]]): List[T] = {
    sortFields match {
      case None | Some(Nil) => items
      case Some(fields) =>
        items.sortWith {
          (a, b) =>
            compareItems(a, b, fields, fieldExtractors)
        }
    }
  }

  private def compareItems[T](a: T, b: T, sortFields: List[String], fieldExtractors: Map[String, T => Option[Comparable[Any]]]): Boolean = {
    sortFields match {
      case Nil => false
      case field :: rest =>
        val (fieldName, descending) = if (field.startsWith("-")) {
          (field.substring(1), true)
        }
        else {
          (field, false)
        }

        fieldExtractors.get(fieldName) match {
          case Some(extractor) =>
            (extractor(a), extractor(b)) match {
              case (Some(valA), Some(valB)) =>
                val comparison = compareValues(valA, valB)
                if (comparison == 0) {
                  compareItems(a, b, rest, fieldExtractors)
                }
                else {
                  if (descending) comparison > 0 else comparison < 0
                }
              case (Some(_), None) => true
              case (None, Some(_)) => false
              case (None, None)    => compareItems(a, b, rest, fieldExtractors)
            }
          case None => compareItems(a, b, rest, fieldExtractors)
        }
    }
  }

  private def compareValues(a: Comparable[Any], b: Comparable[Any]): Int = {
    try
      a.compareTo(b)
    catch {
      case _: ClassCastException => 0
    }
  }
}
