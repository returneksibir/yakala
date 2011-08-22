package yakala.refdb
import java.sql.DriverManager
import org.squeryl._
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.KeyedEntity
import org.squeryl.Session
import org.squeryl.SessionFactory
import org.squeryl.adapters.H2Adapter
import org.squeryl.adapters.PostgreSqlAdapter

class Ref(var id: Long, var ref: String) extends KeyedEntity[Long] {
}

sealed class refSchema extends Schema {
  val refs = table[Ref]
  
  on(refs)(ref => declare (
    ref.id is (unique)
  ))

  override def drop = {
    Session.cleanupResources
    super.drop
  }
}

sealed class refDbPolymorphic(val dbName: String, val username: String, val password: String) {
  def this() = this("Yakala.refs", "", "")
  def this(dbName: String) = this(dbName, "", "")
  def this(dbName: String, username: String) = this(dbName, username, "")
}

trait refDb {
  private val _refSchema = new refSchema
  openSession()
  transaction {
    _refSchema create
  }

  def openSession() {
    throw new Exception("A valid session must be implemented!")
  }

  def closeSession() = {
    /* Don't know what to do here ! */
    transaction {
      _refSchema.drop
    }
  }

  def hasRef(id: Long) = {
    transaction {
      val entry = _refSchema.refs.where(ref => ref.id === id).toList
      if (entry.size > 0) {
	true
      } else {
	false
      }
    }
  }

  def addRef(entry: Ref) = {
    try {
      transaction {
	_refSchema.refs.insert(entry)
      }
      true
    } catch {
      case e => println("Cannot add Ref " + e.getMessage()); false
    }
  }

  def delRef(id: Long) = {
    if (!hasRef(id)) {
      false
    }
    transaction {
      _refSchema.refs.deleteWhere(ref => ref.id === id)
    }
    true
  }
}

/*
 * Follow the link below in order to have basic working postgresql;
 * https://help.ubuntu.com/community/PostgreSQL
 */
class refDbPostgreSql(dbName: String, username: String, password: String) extends refDbPolymorphic(dbName, username, password) with refDb {
  override def openSession {
    Class.forName("org.postgresql.Driver")
    SessionFactory.concreteFactory = Some(() =>
      Session.create(DriverManager.getConnection("jdbc:postgresql://localhost/" + dbName, username, password), new PostgreSqlAdapter))
  }
}

class refDbH2(dbName: String, username: String, password: String)  extends refDbPolymorphic(dbName, username, password) with refDb {
  override def openSession {
    Class.forName("org.h2.Driver")
    SessionFactory.concreteFactory = Some(() =>
      Session.create(DriverManager.getConnection("jdbc:h2:~/" + dbName, username, password), new H2Adapter))
  }
}
