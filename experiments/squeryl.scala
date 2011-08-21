import java.sql.Timestamp
import java.sql.DriverManager
import org.squeryl._
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.KeyedEntity
import org.squeryl.Session
import org.squeryl.SessionFactory
import org.squeryl.adapters.H2Adapter

class BaseEntity extends KeyedEntity[Long] {
  val id: Long = 0
  val lastModified = new Timestamp(System.currentTimeMillis)
}

class User (var email: String, var password: String) extends BaseEntity {
  def this() = this("", "")
}

object schema extends Schema {
  val users = table[User]
  
  on(users)(user => declare(
    user.id is (autoIncremented),
    user.email is (unique)
    ))

  override def drop = {
    Session.cleanupResources
    super.drop
  }
}

object dbTest extends App {
  def startDatabaseSession():Unit = {
    Class.forName("org.h2.Driver")
    SessionFactory.concreteFactory = Some(() =>
      Session.create(DriverManager.getConnection("jdbc:h2:~/asd", "asd", ""), new H2Adapter))
  }

  startDatabaseSession()
  println("Session started")
  transaction {
    schema.create
    println("Created the schema")
  }

  transaction {
    val user1:User = new User("hop@asd.com", "qwe")
    schema.users.insert(user1)
    println("inserted user1")
    
    user1.password = "zzxC"
    schema.users.update(user1)
    println("updated user1 password")

    schema.users.insert(new User("biriki@mail.com", "sifre"))
    schema.users.insert(new User("ucdort@mail.com", "mikre"))
  }

  transaction {
    val ids = from(schema.users) (s => where(s.id === 1234) select(s)).single
    println("ids is " + ids)
  }

}
