package com.froop.app.kegs;

import android.util.Log;
import java.lang.Integer;
import java.io.File;

class DiskImage implements Comparable {
  // NOTE: "this class has a natural ordering that is inconsistent with equals"
  //       (sorted on the filename excluding path, case insensitive)

  // template
  public static final String BOOT_SLOT_5 = "boot_slot_5";
  public static final String BOOT_SLOT_6 = "boot_slot_6";
  public static final String BOOT_SLOT_7 = "boot_slot_7";

  // origin
  public static final int ASSET = 0;
  public static final int DOWNLOAD = 1;
  public static final int LOCALFILE = 2;

  // action
  public static final int BOOT = 0;
  public static final int SWAP = 1;
  public static final int ASK = 2;
  public static final int CANCEL = 3;

  public String filename;
  public String drive;
  public int speed;
  public String template;
  public int origin;
  public int action;
  public String extract_filename = null;  // for extracting from zipfiles

  // Example:
  //   DiskImage("XMAS_DEMO.2MG", "s5d1", 2, "boot_slot_5", LOCALFILE);
  public DiskImage(final String filename, final String drive,
                   final int speed, final String template, final int origin) {
    this.filename = filename;
    this.drive = drive;
    this.speed = speed;
    this.template = template;
    this.origin = origin;
    this.action = BOOT;
  }

  public static DiskImage fromPath(String path) {
    final File file = new File(path);
    if (!file.exists()) {
      return null;
    }
    final String lower = path.toLowerCase();
    final long length = file.length();

    if (lower.endsWith(".hdv") || (length >= 1024 * 1024 && !lower.endsWith(".dsk") && !lower.endsWith(".nib"))) {
      return new DiskImage(path, "s7d1", 3, BOOT_SLOT_7, LOCALFILE);
    } else if (lower.endsWith(".2mg") || lower.endsWith(".po") || length >= 400 * 1024) {
      return new DiskImage(path, "s5d1", 2, BOOT_SLOT_5, LOCALFILE);
    } else if (length > 0) {
      return new DiskImage(path, "s6d1", 1, BOOT_SLOT_6, LOCALFILE);
    } else {
      return null;
    }
  }

  public static boolean isDiskImageFilename(String filename) {
    String lower = filename.toLowerCase();
    return lower.endsWith(".2mg") ||
           lower.endsWith(".dsk") ||
           lower.endsWith(".nib") ||
           lower.endsWith(".hdv") ||
           lower.endsWith(".po")  ||
           lower.endsWith(".do")  ||
           lower.endsWith(".bin");
  }

  public String getBaseFilename() {
    int pos = this.filename.lastIndexOf("/");
    return this.filename.substring(pos + 1);
  }

  public String getTitle() {
    // Use better names for included assets.
    return AssetImages.translateTitle(getBaseFilename());
  }

  public int getIconId() {
    if (BOOT_SLOT_7.equals(this.template)) {
      return (R.drawable.ic_menu_save);
    } else if (BOOT_SLOT_5.equals(this.template)) {
      return (R.drawable.ic_menu_save);
    } else if (BOOT_SLOT_6.equals(this.template)) {
      return (R.drawable.ic_menu_save);
    } else {
      return (R.drawable.ic_menu_save);
    }
  }

  public int getSlot() {
    if (this.drive != null && this.drive.startsWith("s") && this.drive.contains("d")) {
      try {
        return Integer.parseInt(this.drive.substring(1, this.drive.indexOf('d')));
      } catch (Exception e) {
        return 7;
      }
    }
    return 7;
  }

  public int getDriveNumber() {
    if (this.drive != null && this.drive.contains("d")) {
      try {
        return Integer.parseInt(this.drive.substring(this.drive.indexOf('d') + 1));
      } catch (Exception e) {
        return 1;
      }
    }
    return 1;
  }

  public void setDrive(int slot, int driveNum) {
    this.drive = "s" + slot + "d" + driveNum;
    if (slot == 7) {
      this.template = BOOT_SLOT_7;
      this.speed = 3;
    } else if (slot == 5) {
      this.template = BOOT_SLOT_5;
      this.speed = 2;
    } else if (slot == 6) {
      this.template = BOOT_SLOT_6;
      this.speed = 1;
    }
  }

  public Event.DiskImageEvent getDiskImageEvent() {
    if (this.drive != null && this.drive.startsWith("s") && this.drive.contains("d")) {
      int dIndex = this.drive.indexOf('d');
      try {
        int slot = Integer.parseInt(this.drive.substring(1, dIndex));
        int drive = Integer.parseInt(this.drive.substring(dIndex + 1));
        return new Event.DiskImageEvent(this.filename, slot, drive);
      } catch (Exception e) {
        Log.e("kegs", "Failed to parse drive string: " + this.drive, e);
      }
    }
    Log.e("kegs", "disk image " + this.filename + " has bad drive " + this.drive);
    return null;
  }

  public boolean isHardDrive() {
    return this.drive != null && this.drive.startsWith("s7");
  }

  public int compareTo(Object other) {
    if (other instanceof DiskImage) {
      return this.getTitle().compareToIgnoreCase(((DiskImage)other).getTitle());
    } else {
      throw new ClassCastException();
    }
  }
}
