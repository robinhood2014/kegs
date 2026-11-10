package com.froop.app.kegs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import android.app.DialogFragment;

import java.util.ArrayList;

public class SwapDiskFragment extends DialogFragment {
  private DiskImage mImage;

  public SwapDiskFragment(DiskImage image) {
    super();
    mImage = image;
  }

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    builder.setTitle(mImage.getTitle());
    builder.setIcon(mImage.getIconId());

    final ArrayList<String> choices = new ArrayList<String>();
    final ArrayList<Runnable> actions = new ArrayList<Runnable>();

    // Boot option
    choices.add("Boot this disk (" + mImage.drive.toUpperCase() + ")");
    actions.add(new Runnable() {
      public void run() {
        mImage.action = DiskImage.BOOT;
        ((KegsMain) getActivity()).loadDiskImage(mImage);
      }
    });

    int currentSlot = mImage.getSlot();
    int maxDrives = (currentSlot == 7) ? 11 : 2;

    // Drive options for the primary slot of this image type
    for (int d = 1; d <= maxDrives; d++) {
      final int driveNum = d;
      final int slotNum = currentSlot;
      String label = "Insert into Slot " + slotNum + ", Drive " + driveNum + " (s" + slotNum + "d" + driveNum + ")";
      choices.add(label);
      actions.add(new Runnable() {
        public void run() {
          mImage.setDrive(slotNum, driveNum);
          mImage.action = DiskImage.SWAP;
          ((KegsMain) getActivity()).loadDiskImage(mImage);
        }
      });
    }

    // Custom drive selection
    choices.add("Select other Slot / Drive...");
    actions.add(new Runnable() {
      public void run() {
        showAllDrivesDialog();
      }
    });

    builder.setItems(choices.toArray(new String[0]), new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialog, int item) {
        dismiss();
        if (item >= 0 && item < actions.size()) {
          actions.get(item).run();
        }
      }
    });
    return builder.create();
  }

  private void showAllDrivesDialog() {
    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    builder.setTitle("Select Target Drive");

    final ArrayList<String> choices = new ArrayList<String>();
    final ArrayList<int[]> driveTargets = new ArrayList<int[]>();

    // Slot 5: 2 3.5" Floppy Drives
    for (int d = 1; d <= 2; d++) {
      choices.add("Slot 5, Drive " + d + " (s5d" + d + " - 3.5\" Floppy)");
      driveTargets.add(new int[]{5, d});
    }

    // Slot 6: 2 5.25" Floppy Drives
    for (int d = 1; d <= 2; d++) {
      choices.add("Slot 6, Drive " + d + " (s6d" + d + " - 5.25\" Floppy)");
      driveTargets.add(new int[]{6, d});
    }

    // Slot 7: 11 Hard Disk Drives
    for (int d = 1; d <= 11; d++) {
      choices.add("Slot 7, Drive " + d + " (s7d" + d + " - Hard Drive)");
      driveTargets.add(new int[]{7, d});
    }

    builder.setItems(choices.toArray(new String[0]), new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialog, int item) {
        if (item >= 0 && item < driveTargets.size()) {
          int[] target = driveTargets.get(item);
          mImage.setDrive(target[0], target[1]);
          mImage.action = DiskImage.SWAP;
          ((KegsMain) getActivity()).loadDiskImage(mImage);
        }
      }
    });
    builder.show();
  }
}
