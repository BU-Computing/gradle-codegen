/*
 * Copyright (c) 2016.
 *
 * This file is part of ProcessManager.
 *
 * This file is licenced to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You should have received a copy of the license with the source distribution.
 * Alternatively, you may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package uk.ac.bournemouth.kotlinsql

/**
 * Created by pdvrieze on 01/04/16.
 */
sealed abstract class AbstractColumnConfiguration<T:Any, S: BaseColumnType<T, S>,out C: Column<T, S>>(val table: TableRef, val name: String, val type: S) {

  enum class ColumnFormat { FIXED, MEMORY, DEFAULT }
  enum class StorageFormat { DISK, MEMORY, DEFAULT }

  var notnull: Boolean? = null
  var unique: Boolean = false
  var autoincrement: Boolean = false
  var default: T? = null
  var comment:String? = null
  var columnFormat: ColumnFormat? = null
  var storageFormat: StorageFormat? = null
  var references: ColsetRef? = null

  val NULL:Unit get() { notnull=false }
  val NOT_NULL:Unit get() { notnull = true }
  val AUTO_INCREMENT:Unit get() { autoincrement = true }
  val UNIQUE:Unit get() { unique = true }

  inline fun DEFAULT(value:T) { default=value }
  inline fun COMMENT(comment:String) { this.comment = comment }
  inline fun COLUMN_FORMAT(format: ColumnFormat) { columnFormat = format }
  inline fun STORAGE(format: StorageFormat) { storageFormat = format }
  inline fun REFERENCES(table: TableRef, col1: ColumnRef<*, *>, vararg columns: ColumnRef<*, *>) { references= ColsetRef(
        table,
        col1,
        *columns)
  }

  final class NormalColumnConfiguration<T:Any, S: BaseColumnType<T, S>>(table: TableRef, name: String, type: S): AbstractColumnConfiguration<T, S, Column<T, S>>(table, name, type)

  sealed abstract class AbstractNumberColumnConfiguration<T:Any, S: BaseColumnType<T, S>, C: NumericColumn<T, S>>(table: TableRef, name: String, type: S): AbstractColumnConfiguration<T, S, C>(table, name, type) {
    var unsigned: Boolean = false
    var zerofill: Boolean = false
    var displayLength: Int = -1

    val UNSIGNED:Unit get() { unsigned = true }

    val ZEROFILL:Unit get() { unsigned = true }

    final class NumberColumnConfiguration<T:Any, S: BaseColumnType<T, S>>(table: TableRef, name: String, type: S): AbstractNumberColumnConfiguration<T, S, NumericColumn<T, S>>(table, name, type)

    final class DecimalColumnConfiguration<T:Any, S: BaseColumnType<T, S>>(table: TableRef, name: String, type: S, val precision: Int, val scale: Int): AbstractNumberColumnConfiguration<T, S, DecimalColumn<T, S>>(table, name, type) {
      val defaultPrecision=10
      val defaultScale=0
    }

  }

  sealed abstract class AbstractCharColumnConfiguration<T:Any, S: BaseColumnType<T, S>, C: CharColumn<T, S>>(table: TableRef, name: String, type: S): AbstractColumnConfiguration<T, S, C>(table, name, type) {
    var charset: String? = null
    var collation: String? = null
    var binary:Boolean = false

    val BINARY:Unit get() { binary = true }

    inline fun CHARACTER_SET(charset:String) { this.charset = charset }
    inline fun COLLATE(collation:String) { this.collation = collation }

    final class CharColumnConfiguration<T:Any, S: BaseColumnType<T, S>>(table: TableRef, name: String, type: S): AbstractCharColumnConfiguration<T, S, CharColumn<T, S>>(table, name, type)
    final class LengthCharColumnConfiguration<T:Any, S: BaseColumnType<T, S>>(table: TableRef, name: String, type: S, override val length: Int): AbstractCharColumnConfiguration<T, S, LengthCharColumn<T, S>>(table, name, type), BaseLengthColumnConfiguration<T, S, LengthCharColumn<T, S>>
  }

  final class LengthColumnConfiguration<T:Any, S: BaseColumnType<T, S>>(table: TableRef, name: String, type: S, override val length: Int): AbstractColumnConfiguration<T, S, LengthColumn<T, S>>(table, name, type), BaseLengthColumnConfiguration<T, S, LengthColumn<T, S>>
}

