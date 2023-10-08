/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package HourTracker;

/**
 *
 * @author James Knox
 */
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import javax.swing.JTable;

public class FileBackupListener {

    private WatchService watchService;
    private Path backupDirectory;
    private Thread backupThread;

    public void startFileBackupListener() throws IOException {
        // Get the user's documents directory
        String userHome = System.getProperty("user.home");
        Path documentsDirectory = Paths.get(userHome, "Documents");

        // Create the backup directory
        backupDirectory = documentsDirectory.resolve("SchoolSyncBackups");
        Files.createDirectories(backupDirectory);

        // Register the directory with the watch service to listen for create events
        watchService = FileSystems.getDefault().newWatchService();
        documentsDirectory.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);

        // Start the backup thread
        backupThread = new Thread(this::processEvents);
        backupThread.start();
    }

    private void processEvents() {
        try {
            System.out.println("File Backup Listener started.");

            while (true) {
                WatchKey key = watchService.take();

                for (WatchEvent<?> event : key.pollEvents()) {
                    WatchEvent.Kind<?> kind = event.kind();

                    if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
                        @SuppressWarnings("unchecked")
                        WatchEvent<Path> pathEvent = (WatchEvent<Path>) event;
                        Path createdFile = pathEvent.context();

                        // Create backup of the file
                        createBackup(createdFile);
                    }
                }

                key.reset();
            }
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    private void createBackup(Path file) throws IOException, InterruptedException {
       Path sourcePath = file.toAbsolutePath();
    Path targetPath = file.getParent().resolve("SchoolSyncBackups").resolve(file.getFileName()).normalize();

    // Create directories in the backup directory if they don't exist
    Files.createDirectories(targetPath.getParent());

    // Check if the source file exists
    if (Files.exists(sourcePath)) {
        // Wait for a specified delay (e.g., 1 second)
        int delayMillis = 1000;
        Thread.sleep(delayMillis);

        // Copy the file to the backup directory
        Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
        System.out.println("Created backup of file: " + sourcePath);
    } else {
        System.out.println("Source file doesn't exist: " + sourcePath);
    }
}

    void backupFile(String cbDataFilePath) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    void backupHourLog(JTable hourLog) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
