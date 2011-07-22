import org.scalatest.FunSuite
import Database._

class DatabaseTestSuite extends FunSuite {
  
	test("Databases must have unique names") {
		val db1 = DatabaseFactory.get("http://deneme.com")	
		val db2 = DatabaseFactory.get("http://asd.com")
		val db3 = DatabaseFactory.get("http://deneme.com")	

		assert(DatabaseFactory.databases.size == 2)
	}
	 
   	test("Yet another Databse test") (pending)
}
