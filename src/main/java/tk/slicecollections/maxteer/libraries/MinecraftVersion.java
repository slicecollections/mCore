package tk.slicecollections.maxteer.libraries;

import com.google.common.base.Objects;
import org.bukkit.Bukkit;
import org.bukkit.Server;

import static java.lang.Integer.parseInt;

/**
 * @author Maxter
 */
public class MinecraftVersion {

  private int major;
  private int minor;
  private int build;
  private int compareId;

  /**
   * Cria uma versão através do atual servidor<br/>
   * que pode ser pego através do {@link Bukkit#getServer()}
   *
   * @param server Um servidor bukkit {@link Server}
   */
  public MinecraftVersion(Server server) {
    this(extractVersion(server));
  }

  /**
   * Cria uma versão através da build do servidor.<br/>
   * Exemplo de build: 1.8.R3
   *
   * @param version A build do servidor.
   */
  public MinecraftVersion(String version) {
    int[] numbers = parseVersion(version);
    this.major = numbers[0];
    this.minor = numbers[1];
    this.build = numbers[2];
    this.compareId = parseInt(this.major + "" + this.minor + "" + this.build);
  }

  /**
   * Cria uma versão através dos números de build.<br/>
   * Exemplo: 1.8.R3<br/>
   * major: 1
   * minor: 8
   * build: 3
   *
   * @param major A major da versão (1)
   * @param minor A minor da versão (1.X)
   * @param build A build da versão (1.8.R"X")
   */
  public MinecraftVersion(int major, int minor, int build) {
    this.major = major;
    this.minor = minor;
    this.build = build;
    this.compareId = parseInt(major + "" + minor + "" + build);
  }

  /**
   * Verifique se essa versão é menos recente ou igual a requisitada.
   *
   * @param version A versão para comparar.
   * @return TRUE se for menos recente ou igual, FALSE se for mais recente.
   */
  public boolean lowerThan(MinecraftVersion version) {
    return this.compareId <= version.getCompareId();
  }

  /**
   * Verifique se essa versão é mais recente ou igual  a requisitada.
   *
   * @param version A versão para comparar.
   * @return TRUE se for mais recente ou igual, FALSE se for menos recente.
   */
  public boolean newerThan(MinecraftVersion version) {
    return this.compareId >= version.getCompareId();
  }

  /**
   * Verifique se essa versão é menos recente ou igual a @param latest e<br/>
   * se é mais recente ou igual a @param olded.
   *
   * @param latest A versão que precisa que ser menos recente ou igual a atual.
   * @param olded  A versão que precisa ser mais recente ou igual a atual.
   * @return TRUE se as duas condições forem cumpridas, FALSE caso não forem.
   */
  public boolean inRange(MinecraftVersion latest, MinecraftVersion olded) {
    return (this.compareId <= latest.getCompareId()) && (this.compareId >= olded.getCompareId());
  }

  /**
   * Pega o valor Major da versão.
   *
   * @return Major númerico
   */
  public int getMajor() {
    return this.major;
  }

  /**
   * Pega o valor Minor da versão.
   *
   * @return Minor númerico
   */
  public int getMinor() {
    return this.minor;
  }

  /**
   * Pega o valor Build da versão.
   *
   * @return Build númerico
   */
  public int getBuild() {
    return this.build;
  }

  /**
   * Pega o valor de comparação da versão.<br/>
   * Exemplo: 1.8.R3
   * ID de comparação: 183
   *
   * @return Valor de comparação
   */
  public int getCompareId() {
    return this.compareId;
  }

  private int[] parseVersion(String version) {
    String[] elements = version.split("\\.");
    int[] numbers = new int[3];

    if (elements.length <= 1 || version.split("R").length < 1) {
      throw new IllegalStateException("Corrupt MC Server version: " + version);
    }

    for (int i = 0; i < 2; i++) {
      numbers[i] = parseInt(elements[i]);
    }

    numbers[2] = parseInt(version.split("R")[1]);
    return numbers;
  }

  /**
   * Retorna a versão em seu estado inicial (package build).<br/>
   * Exemplo: 1_8_R3
   *
   * @return Versão original.
   */
  public String getVersion() {
    return String.format("v%s_%s_R%s", this.major, this.minor, this.build);
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof MinecraftVersion)) {
      return false;
    }
    if (obj == this) {
      return true;
    }

    MinecraftVersion other = (MinecraftVersion) obj;
    return this.getMajor() == other.getMajor() && this.getMinor() == other.getMinor() && this.getBuild() == other.getBuild();
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(this.getMajor(), this.getMinor(), this.getBuild());
  }

  @Override
  public String toString() {
    return String.format("%s", this.getVersion());
  }

  private static String extractVersion(Server server) {
    return extractVersion(server.getClass().getPackage().getName().split("\\.")[3]);
  }

  private static String extractVersion(String version) {
    return version.replace('_', '.').replace("v", "");
  }

  /**
   * Versão atual
   */
  private static MinecraftVersion currentVersion;

  /**
   * Pega a versão do servidor que está atualmente rodando.
   *
   * @return A versão do servidor representada por um {@link MinecraftVersion}.
   */
  public static MinecraftVersion getCurrentVersion() {
    if (currentVersion == null) {
      currentVersion = new MinecraftVersion(Bukkit.getServer());
    }

    return currentVersion;
  }
}
