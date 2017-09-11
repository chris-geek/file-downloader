package com.github.phudekar.downloader;

public class ConsoleOutput implements ProgressListener {

    private StringBuilder messageBuilder = new StringBuilder();

    @Override
    public void onProgress(DownloadEntry entry) {
        if (entry.getStatus().isComplete()) {
            printProgress(entry);
            System.out.println("\nFile downloaded successfully at " + entry.getFile().getAbsolutePath());
        } else {
            printProgress(entry);
        }
    }

    private void printProgress(DownloadEntry entry) {
        DownloadStatus status = entry.getStatus();
        for (int i = 0; i < messageBuilder.length() + 5; i++) {
            System.out.print("\b");
        }
        messageBuilder = new StringBuilder();
        messageBuilder.append("[");

        int progressBarWidth = 20;
        for (int i = 1; i <= progressBarWidth; i++) {
            if (status.getDownloadedSize() * progressBarWidth / status.getTotalSize() >= i)
                messageBuilder.append("=");
            else
                messageBuilder.append(".");
        }

        messageBuilder.append("] ");
        messageBuilder.append(Math.round(status.getDownloadedSize() / 1024) + "/" + Math.round(status.getTotalSize() / 1024) + " KB ");
        messageBuilder.append("Enter '" + ConsoleInput.PAUSE + "' to pause ");

        System.out.print(messageBuilder.toString());
    }


}
