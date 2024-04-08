import cats.effect._
import cats.syntax.all._
import com.monovore.decline._
import com.monovore.decline.effect._
import fs2._
import fs2.io._
import fs2.io.file.Files
import fs2.io.file.Path
import org.ekrich.config.ConfigFactory
import org.ekrich.config.ConfigRenderOptions

import java.nio.file.Paths

import scalanative.unsafe._

object Main
    extends CommandIOApp("formatHocon", "Produce formatted HOCON file(s).") {

  @extern
  object libc {
    def isatty(fd: CInt): CInt = extern
  }

  def main = Opts
    .argument[String]("file")
    .orNone
    .validate("If no file provided must pass input on stdin") { opt =>
      opt match {
        case Some(f) => true
        case None    =>
          // zero is the file descriptor for STDIN; if STDIN is a tty, `isatty` returns 0, otherwise it returns 1
          libc.isatty(0) match {
            case 1 => false
            case _ => true
          }
      }
    }
    .map { file =>

      val input = file match {
        case Some(f) =>
          Files[IO]
            .readAll(Path.fromNioPath(Paths.get(f)))
            .through(text.utf8.decode)
            .compile
            .string

        case None => stdinUtf8[IO](1024 * 1024 * 10).compile.string
      }

      val renderOpts =
        ConfigRenderOptions.defaults.setOriginComments(false).setJson(false)
      input
        .map(ConfigFactory.parseString)
        .map(_.root.render(renderOpts))
        .flatMap(IO.print)
        .as(ExitCode.Success)
    }
}
