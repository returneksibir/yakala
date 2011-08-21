package yakala.refdb
import java.sql.DriverManager
import org.squeryl._
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.KeyedEntity
import org.squeryl.Session
import org.squeryl.SessionFactory
import org.squeryl.adapters.H2Adapter

class Ref(var id: Long, var ref: String) extends KeyedEntity[Long] {
}

abstract class refDb {
  def openSession()
  def closeSession()
  def hasRef(id: Long): Boolean
  def addRef(entry: Ref): Boolean
  def delRef(id: Long): Boolean
}

sealed class refH2Schema extends Schema {
  val refs = table[Ref]
  
  on(refs)(ref => declare (
    ref.id is (unique)
  ))

  override def drop = {
    Session.cleanupResources
    super.drop
  }
}

class refDbH2(val dbName: String) extends refDb {
  def this() = this("Yakala.refs")
  private val refSchema = new refH2Schema
  openSession()
  transaction {
    refSchema create
  }

  def openSession() {
    Class.forName("org.h2.Driver")
    SessionFactory.concreteFactory = Some( () =>
      Session.create(DriverManager.getConnection("jdbc:h2:" + dbName, dbName, ""), new H2Adapter))
      // Session.create(DriverManager.getConnection("jdbc:h2:mem", dbName, ""), new H2Adapter))
  }

  def closeSession() {
    /* Don't know what to do here ! */
    transaction {
      refSchema.drop
    }
  }

  def hasRef(id: Long) = {
    transaction {
      val entry = refSchema.refs.where(ref => ref.id === id).toList
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
	refSchema.refs.insert(entry)
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
      refSchema.refs.deleteWhere(ref => ref.id === id)
    }
    true
  }
}
  
