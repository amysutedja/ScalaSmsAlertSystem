package gateway;

class Main {
  import java.io.{InputStream, OutputStream, PrintStream}

  def router(input: InputStream, output: OutputStream): Unit = {
    val result = s"Greetings." 
    output.write(result.getBytes("UTF-8"))
  }
}