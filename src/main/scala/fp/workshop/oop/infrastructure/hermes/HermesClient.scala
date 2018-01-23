package fp.workshop.oop.infrastructure.hermes

object HermesClient {

  def publishEvent(topic: String, event: AnyRef): Unit = {

    if (Math.random() < 0.2)
      throw new IllegalAccessError("Hermes died")

    println(s"[Hermes] $topic: published $event")
  }
}
