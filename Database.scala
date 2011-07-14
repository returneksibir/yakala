/*
 * Copyright (C) 2011 Sinan Nalkaya, Cem Eliguzel
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 */
package Database

object DatabaseFactory {
  private var cache = Map[String, Database]()
  
  def get(addr: String) = {
    if (cache.contains(addr)) {
      cache(addr)
    } else {
      val db = new Database(addr)
      cache += (addr -> db)
      db
    }
  }

  def databases = { cache.keySet }
}

class Database(addr: String) {
  private val domain = addr
  private val db = scala.collection.mutable.Map[Int, Map[String, Any]] ()

  private def get_entry(url: String) = {
    val key = url stripPrefix domain
    (key, key.hashCode)
  }

  def add(url: String) {
    val (entry, hash) = get_entry(url)
    if (!db.contains(hash)) {
      db += (hash -> Map("url" -> entry, "visited" -> false, "crawled" -> false, "hash" -> hash))
    }
  }

  def remove(url: String) = {
    val (entry, hash) = get_entry(url)
    if (!db.contains(hash)) {
      false
    } else {
      db remove hash
      true
    }
  }

  def print_entries {
    println("Domain : " + domain)
    for ((key, value) <- db)
      println("Entry\t\t:" + value)
  }
}
