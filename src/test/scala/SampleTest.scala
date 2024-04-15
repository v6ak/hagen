import collection.mutable.Stack
import org.scalatest._
import flatspec._
import matchers._
import com.v6ak.hagen.expressions._


enum Foo:
  case Bar, Baz, Barbar

final class SampleTest extends AnyFlatSpec with should.Matchers {

  val c = Context.Empty

  "constants" should "serialize correctly" in {
    Const(true).asJinja(c) should be ("true")
    Const(42).asJinja(c) should be ("42")
    Const(42.0).asJinja(c) should be ("42.0")
    Const("42.0").asJinja(c) should be ("\"42.0\"")
    Const(Foo.Barbar).asJinja(c) should be ("\"Barbar\"")
  }

  "constants" should "serialize correctly context-safe" in {
    Const(true).asContextSafeJinja(c) should be ("true")
    Const(42).asContextSafeJinja(c) should be ("42")
    Const(42.0).asContextSafeJinja(c) should be ("42.0")
    Const("42.0").asContextSafeJinja(c) should be ("\"42.0\"")
    Const(Foo.Barbar).asContextSafeJinja(c) should be ("\"Barbar\"")
  }

  "entities" should "serialize correctly" in {
    // TODO: use single fetch for BooleanEntity
    Entity[Boolean]("chachar").asJinja(c) should be("""false if (states("chachar") == "off") else (true if (states("chachar") == "on") else (unexpected_value()))""")
    Entity[Int]("chachar").asJinja(c) should be("states(\"chachar\") | int")
    Entity[Double]("chachar").asJinja(c) should be("states(\"chachar\") | float")
    Entity[String]("chachar").asJinja(c) should be("states(\"chachar\")")
    Entity[Foo]("chachar").asJinja(c) should be("states(\"chachar\")")
  }

  "entities" should "serialize correctly context-safe" in {
    Entity[Boolean]("chachar").asContextSafeJinja(c) should be("""(false if (states("chachar") == "off") else (true if (states("chachar") == "on") else (unexpected_value())))""")
    Entity[Int]("chachar").asContextSafeJinja(c) should be("(states(\"chachar\") | int)")
    Entity[Double]("chachar").asContextSafeJinja(c) should be("(states(\"chachar\") | float)")
    Entity[String]("chachar").asContextSafeJinja(c) should be("states(\"chachar\")")
    Entity[Foo]("chachar").asContextSafeJinja(c) should be("states(\"chachar\")")
  }

}
