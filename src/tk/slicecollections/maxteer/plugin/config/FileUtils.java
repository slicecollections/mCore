package tk.slicecollections.maxteer.plugin.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import tk.slicecollections.maxteer.plugin.MPlugin;

public class FileUtils {

  private MPlugin plugin;

  public FileUtils(MPlugin plugin) {
    this.plugin = plugin;
  }

  /**
   * Deleta um arquivo/pasta.
   * 
   * @param file O arquivo para deletar.
   */
  public void deleteFile(File file) {
    if (!file.exists()) {
      return;
    }

    if (file.isDirectory()) {
      Arrays.stream(file.listFiles()).forEach(this::deleteFile);
    }

    file.delete();
  }

  /**
   * Copia um arquivo de um diretório para outro.
   * 
   * @param in Arquivo para copiar.
   * @param out Destinário para colar.
   * @param ignore Arquivos chaves para ignorar caso for uma pasta de arquivos.
   */
  public void copyFiles(File in, File out, String... ignore) {
    List<String> list = Arrays.asList(ignore);
    if (in.isDirectory()) {
      if (!out.exists()) {
        out.mkdirs();
      }

      for (File file : in.listFiles()) {
        if (list.contains(file.getName())) {
          continue;
        }

        copyFiles(file, new File(out, file.getName()));
      }
    } else {
      try {
        copyFile(new FileInputStream(in), out);
      } catch (IOException ex) {
        this.plugin.getLogger().log(Level.WARNING, "Um erro inesperado ocorreu ao copiar o arquivo \"" + out.getName() + "\": ", ex);
      }
    }
  }

  /**
   * Copia um arquivo de um {@link InputStream}.
   * 
   * @param input {@link InputStream} para copiar.
   * @param out Destinário para colar.
   */
  public void copyFile(InputStream input, File out) {
    FileOutputStream ou = null;
    try {
      ou = new FileOutputStream(out);
      byte[] buff = new byte[1024];
      int len;
      while ((len = input.read(buff)) > 0) {
        ou.write(buff, 0, len);
      }
    } catch (IOException ex) {
      this.plugin.getLogger().log(Level.WARNING, "Um erro inesperado ocorreu ao copiar o arquivo \"" + out.getName() + "\": ", ex);
    } finally {
      try {
        if (ou != null) {
          ou.close();
        }
        if (input != null) {
          input.close();
        }
      } catch (IOException e) {
      }
    }
  }
}
