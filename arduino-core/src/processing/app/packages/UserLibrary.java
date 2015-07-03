/*
 * This file is part of Arduino.
 *
 * Copyright 2014 Arduino LLC (http://www.arduino.cc/)
 *
 * Arduino is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * As a special exception, you may use this file as part of a free software
 * library without restriction.  Specifically, if other files instantiate
 * templates or use macros or inline functions from this file, or you compile
 * this file and link it with other files to produce an executable, this
 * file does not by itself cause the resulting executable to be covered by
 * the GNU General Public License.  This exception does not however
 * invalidate any other reasons why the executable file might be covered by
 * the GNU General Public License.
 */
package processing.app.packages;

import cc.arduino.contributions.libraries.ContributedLibrary;
import cc.arduino.contributions.libraries.ContributedLibraryReference;
import processing.app.packages.LibrarySelection;
import processing.app.helpers.FileUtils;
import processing.app.helpers.PreferencesMap;
import processing.app.debug.Compiler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;
import java.util.Map;
import java.util.HashSet;
import java.util.TreeSet;
import java.util.SortedSet;
import java.util.Set;

public class UserLibrary extends ContributedLibrary {

  protected String globalName;
  private String name;
  private String version;
  private String author;
  private String maintainer;
  private String sentence;
  private String paragraph;
  private String website;
  private String category;
  private String license;
  private List<String> architectures;
  private List<String> types;
  private List<String> declaredTypes;

  private static final List<String> MANDATORY_PROPERTIES = Arrays
    .asList("name", "version", "author", "maintainer",
      "sentence", "paragraph", "url");

  private static final List<String> CATEGORIES = Arrays.asList(
    "Display", "Communication", "Signal Input/Output", "Sensors",
    "Device Control", "Timing", "Data Storage", "Data Processing", "Other",
    "Uncategorized");

  public static UserLibrary create(File libFolder) throws IOException {
    // Parse metadata
    File propertiesFile = new File(libFolder, "library.properties");
    PreferencesMap properties = new PreferencesMap();
    properties.load(propertiesFile);

    // Library sanity checks
    // ---------------------

    // Compatibility with 1.5 rev.1 libraries:
    // "email" field changed to "maintainer"
    if (!properties.containsKey("maintainer") && properties.containsKey("email")) {
      properties.put("maintainer", properties.get("email"));
    }

    // Compatibility with 1.5 rev.1 libraries:
    // "arch" folder no longer supported
    File archFolder = new File(libFolder, "arch");
    if (archFolder.isDirectory())
      throw new IOException("'arch' folder is no longer supported! See http://goo.gl/gfFJzU for more information");

    // Check mandatory properties
    for (String p : MANDATORY_PROPERTIES)
      if (!properties.containsKey(p))
        throw new IOException("Missing '" + p + "' from library");

    // Check layout
    LibraryLayout layout;
    File srcFolder = new File(libFolder, "src");

    if (srcFolder.exists() && srcFolder.isDirectory()) {
      // Layout with a single "src" folder and recursive compilation
      layout = LibraryLayout.RECURSIVE;

      File utilFolder = new File(libFolder, "utility");
      if (utilFolder.exists() && utilFolder.isDirectory()) {
        throw new IOException("Library can't use both 'src' and 'utility' folders.");
      }
    } else {
      // Layout with source code on library's root and "utility" folders
      layout = LibraryLayout.FLAT;
    }

    // Warn if root folder contains development leftovers
    File[] files = libFolder.listFiles();
    if (files == null) {
      throw new IOException("Unable to list files of library in " + libFolder);
    }
    for (File file : files) {
      if (file.isDirectory() && FileUtils.isSCCSOrHiddenFile(file)) {
        if (!FileUtils.isSCCSFolder(file) && FileUtils.isHiddenFile(file)) {
          System.out.println("WARNING: Spurious " + file.getName() + " folder in '" + properties.get("name") + "' library");
        }
      }
    }

    // Extract metadata info
    String architectures = properties.get("architectures");
    if (architectures == null)
      architectures = "*"; // defaults to "any"
    List<String> archs = new ArrayList<String>();
    for (String arch : architectures.split(","))
      archs.add(arch.trim());

    String category = properties.get("category");
    if (category == null)
      category = "Uncategorized";
    if (!CATEGORIES.contains(category)) {
      System.out.println("WARNING: Category '" + category + "' in library " + properties.get("name") + " is not valid. Setting to 'Uncategorized'");
      category = "Uncategorized";
    }

    String license = properties.get("license");
    if (license == null) {
      license = "Unspecified";
    }

    String types = properties.get("types");
    if (types == null) {
      types = "Contributed";
    }
    List<String> typesList = new LinkedList<String>();
    for (String type : types.split(",")) {
      typesList.add(type.trim());
    }

    UserLibrary res = new UserLibrary();

    String globalName = properties.get("global_name");
    if (globalName != null) {
      globalName = globalName.trim();
    }

    res.setInstalledFolder(libFolder);
    res.setInstalled(true);
    res.name = properties.get("name").trim();
    res.version = properties.get("version").trim();
    res.author = properties.get("author").trim();
    res.maintainer = properties.get("maintainer").trim();
    res.sentence = properties.get("sentence").trim();
    res.paragraph = properties.get("paragraph").trim();
    res.website = properties.get("url").trim();
    res.category = category.trim();
    res.license = license.trim();
    res.architectures = archs;
    res.layout = layout;
    res.declaredTypes = typesList;
    res.setGlobalName(globalName);
    return res;
  }

  /**
   * Call this after setting website, author, and name.
   */
  public void setGlobalName(String gn) {
    globalName = gn;
    boolean invalid = false;
    if (globalName == null || globalName.equals("")) {
      invalid = true;
      String rest = website.replaceFirst(".*://", "").replaceFirst("\\.[^/.]*$", "");
      List<String> parts = Arrays.asList(rest.split("/"));
      List<String> hostParts = Arrays.asList(parts.get(0).split("\\."));
      Collections.reverse(hostParts);
      globalName = String.join(".", hostParts);
      if (globalName.endsWith(".www")) {
        // Remove www component
        globalName = globalName.substring(0, globalName.length() - 4);
      }
      if (parts.size() > 1) {
        // Path parts
        globalName += "." + String.join(".", parts.subList(1, parts.size()));
      }
      if (globalName.startsWith("com.github.")) { 
        // Better to use the user-only namespace.
        globalName = "io.github." + globalName.substring(11);
      }
    }
    if (globalName == null || globalName.equals("")) {
      invalid = true;
      // Fallback.  Note: the global name is used to test for equality,
      // so it must have a value.
      globalName = author.replace('|', '_') + "|" + name;
    }
    if (invalid) {
      System.err.println("WARNING: global_name not set in library " + name + ". Guessing '" + globalName + "'.\nPlease set this to a suitable Java-style package name, e.g. io.github.myaccount.myproject\nThe name should be set manually to avoid confusion when forking.");
    }
  }

  @Override
  public String getGlobalName() {
    return globalName;
  }

  public String getDepSpec() {
    String depSpec = null;
    if (globalName != null) {
      depSpec = globalName;
      if (version != null) {
        depSpec += ":" + version;
      }
    }
    return depSpec;
  }

  @Override
  public int hashCode() {
    return getDepSpec().hashCode();
  }

  public boolean matchesDepSpec(String spec) {
    String[] parts = spec.split(":", 2);
    return getGlobalName().equals(parts[0]);
  }

  public boolean matchesDepSpecVersion(String spec) {
    String[] parts = spec.split(":", 2);
    if (parts.length == 1) {
      // No version spec - accept any version.
      return true;
    }
    if (getVersion() == null) {
      // Unlabeled version - fails to match any version spec.
      return false;
    }
    String[] vers = parts[1].split("-");
    if (vers.length == 2) {
      return versionsOrdered(vers[0], getVersion()) &&
             versionsOrdered(getVersion(), vers[1]);
    } else if (parts[1].endsWith("+")) {
      return versionsOrdered(parts[1].substring(0, parts[1].length() - 1), getVersion());
    } else if (parts[1].endsWith("*")) {
      return getVersion().startsWith(parts[1].substring(0, parts[1].length() - 1));
    } else {
      return parts[1].equals(getVersion());
    }
  }

  static private boolean versionsOrdered(String ver1, String ver2) {
    String[] c1 = ver1.split("\\.");
    String[] c2 = ver2.split("\\.");
    int i;
    for (i = 0; i < c1.length && i < c2.length; i++) {
      int n1 = Integer.parseInt(c1[i]);
      int n2 = Integer.parseInt(c2[i]);
      if (n1 > n2) {
        return false;
      } else if (n1 < n2) {
        return true;
      }
    }
    return c1.length <= c2.length;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public List<String> getArchitectures() {
    return architectures;
  }

  @Override
  public String getAuthor() {
    return author;
  }

  @Override
  public String getParagraph() {
    return paragraph;
  }

  @Override
  public String getSentence() {
    return sentence;
  }

  @Override
  public String getWebsite() {
    return website;
  }

  @Override
  public String getCategory() {
    return category;
  }

  @Override
  public List<String> getTypes() {
    return types;
  }

  public void setTypes(List<String> types) {
    this.types = types;
  }

  @Override
  public String getLicense() {
    return license;
  }

  public static List<String> getCategories() {
    return CATEGORIES;
  }

  @Override
  public void setCategory(String category) {
    this.category = category;
  }

  @Override
  public String getVersion() {
    return version;
  }

  @Override
  public String getMaintainer() {
    return maintainer;
  }

  @Override
  public String getChecksum() {
    return null;
  }

  @Override
  public long getSize() {
    return 0;
  }

  @Override
  public String getUrl() {
    return null;
  }

  @Override
  public String getArchiveFileName() {
    return null;
  }

  @Override
  public List<ContributedLibraryReference> getRequires() {
    return null;
  }

  private Map<String,long[]> lastUpdateTimes = new TreeMap<String,long[]>();

  private List<LibrarySelection> requiredLibs = null;
  private List<LibrarySelection> requiredLibsRec = null;

  private boolean changedSinceLastUpdate(int idx) {
    List<File> files = Compiler.findAllSources(getSrcFolder(), useRecursion());
    // Important: must update timestamps from ALL files before returning.
    boolean changed = false;
    for (File file : files) {
      String fname = file.toString();
      long modTime = file.lastModified();
      if (!lastUpdateTimes.containsKey(fname)) {
        long[] times = new long[2];
        times[idx] = modTime;
        lastUpdateTimes.put(fname, times);
        changed = true;
      } else if (lastUpdateTimes.get(fname)[idx] < modTime) {
        lastUpdateTimes.get(fname)[idx] = modTime;
        changed = true;
      }
    }
    return changed;
  }

  public boolean changedSinceLastUpdateRec(int idx, SortedSet<String> visited) {
    // Prevent infinite recursion.
    if (visited.contains(getDepSpec())) {
      return false;
    }
    visited.add(getDepSpec());

    if (changedSinceLastUpdate(idx)) {
      return true;
    }
    for (LibrarySelection libSel : getRequiredLibs()) {
      if (libSel.get().changedSinceLastUpdateRec(idx, visited)) {
        return true;
      }
    }
    return false;
  }

  public List<LibrarySelection> getRequiredLibs() {
    if (requiredLibs == null || changedSinceLastUpdate(0)) {
      // Note: the "recursion" in useRecursion() refers to a strategy for
      // finding files within an individual project
      Set<UserLibrary> preferSet = new HashSet<>();
      preferSet.add(this);
      requiredLibs = Compiler.findRequiredLibs(getSrcFolder(), useRecursion(), preferSet);
      requiredLibs.remove(this);
    }
    return requiredLibs;
  }

  public List<LibrarySelection> getRequiredLibsRec() {
    return getRequiredLibsRec(new TreeSet<>());
  }

  public List<LibrarySelection> getRequiredLibsRec(SortedSet<String> visited) {
    // Prevent infinite recursion.
    if (visited.contains(getDepSpec())) {
      return new ArrayList<>();
    }
    visited.add(getDepSpec());

    if (requiredLibsRec == null || changedSinceLastUpdateRec(1, new TreeSet<>(visited))) {
      requiredLibsRec = new ArrayList<>();
      for (LibrarySelection libSel : getRequiredLibs()) {
        if (!requiredLibsRec.contains(libSel) && libSel.get() != this) {
          requiredLibsRec.add(libSel);
          for (LibrarySelection libSelRec : libSel.get().getRequiredLibsRec(visited)) {
            if (!requiredLibsRec.contains(libSelRec) && libSelRec.get() != this) {
              requiredLibsRec.add(libSelRec);
            }
          }
        }
      }
    }
    return requiredLibsRec;
  }

  public List<String> getDeclaredTypes() {
    return declaredTypes;
  }

  protected enum LibraryLayout {
    FLAT, RECURSIVE
  }

  protected LibraryLayout layout;

  public File getSrcFolder() {
    switch (layout) {
      case FLAT:
        return getInstalledFolder();
      case RECURSIVE:
        return new File(getInstalledFolder(), "src");
      default:
        return null; // Keep compiler happy :-(
    }
  }

  public boolean useRecursion() {
    return (layout == LibraryLayout.RECURSIVE);
  }

  @Override
  public String toString() {
    String res = "Library: " + name + "\n";
    res += "         (global_name=" + globalName + ")\n";
    res += "         (version=" + version + ")\n";
    res += "         (author=" + author + ")\n";
    res += "         (maintainer=" + maintainer + ")\n";
    res += "         (sentence=" + sentence + ")\n";
    res += "         (paragraph=" + paragraph + ")\n";
    res += "         (url=" + website + ")\n";
    res += "         (architectures=" + architectures + ")\n";
    return res;
  }

}
