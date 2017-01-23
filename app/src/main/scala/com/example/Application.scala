package com.example

import io.prometheus.client._
import io.prometheus.client.exporter.common.TextFormat
import java.io.Writer
import play.api.BuiltInComponents
import play.api.http.HttpEntity
import play.api.mvc._
import play.api.routing.Router
import play.api.routing.sird._
import play.core.server._
import scala.util.Try

class Components(port: Int) extends NettyServerComponents
  with BuiltInComponents with Controller {

  override lazy val serverConfig = ServerConfig(port = Some(port))

  val registry = CollectorRegistry.defaultRegistry
  val counter = Counter.build()
    .name("example_counter")
    .help("An example counter recording the number of hits to the / endpoint.")
    .register()

  lazy val router = Router.from {
    // Hitting this endpoint will increment the example_counter
    case GET(p"/") =>
      counter.inc()
      Action(Ok("Hello"))

    // Endpoint that will be scraped by Prometheus
    case GET(p"/metrics") =>
      val samples = new StringBuilder()
      val writer = new WriterAdapter(samples)
      TextFormat.write004(writer, registry.metricFamilySamples())
      writer.close()

      Action(Ok(samples.toString).as(TextFormat.CONTENT_TYPE_004))
  }
}

object Application {
  val DefaultPort = 9999

  def main(args: Array[String]): Unit = {
    val port = args.headOption.fold(DefaultPort) { arg: String =>
      Try(arg.toInt).getOrElse(throw new Exception(s"Invalid port $arg"))
    }

    val server = new Components(port).server
  }
}

class WriterAdapter(buffer: StringBuilder) extends Writer {
  override def write(charArray: Array[Char], offset: Int, length: Int): Unit = {
    buffer ++= new String(new String(charArray, offset, length).getBytes("UTF-8"), "UTF-8")
  }
  override def flush(): Unit = ()
  override def close(): Unit = ()
}

