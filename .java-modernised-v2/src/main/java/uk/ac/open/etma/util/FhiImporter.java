// SPDX-FileCopyrightText: 2024 eTMA Handler Contributors
// SPDX-License-Identifier: MIT
package uk.ac.open.etma.util;

import uk.ac.open.etma.service.IntegrityStore;
import uk.ac.open.etma.service.SafeFileService;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.stream.Stream;

/**
 * Utility to import existing FHI files into the IntegrityStore.
 *
 * <p>Run this once to protect all existing student files:
 * <pre>{@code
 * FhiImporter importer = new FhiImporter(integrityStore);
 * ImportResult result = importer.importDirectory(Path.of("/home/hyper/etmas"));
 * System.out.println("Imported: " + result.imported());
 * System.out.println("Corrupted: " + result.corrupted());
 * }</pre>
 */
public final class FhiImporter {

    private static final Charset FHI_CHARSET = Charset.forName("ISO-8859-1");

    private final IntegrityStore integrityStore;
    private final SafeFileService fileService;

    public FhiImporter(IntegrityStore integrityStore) {
        this.integrityStore = integrityStore;
        this.fileService = new SafeFileService();
    }

    /**
     * Import all FHI files from a directory tree into the IntegrityStore.
     *
     * @param rootDirectory Root of etmas directory
     * @return ImportResult with statistics
     */
    public ImportResult importDirectory(Path rootDirectory) {
        List<Path> imported = new ArrayList<>();
        List<CorruptedFile> corrupted = new ArrayList<>();
        List<Path> skipped = new ArrayList<>();

        try (Stream<Path> paths = Files.walk(rootDirectory)) {
            paths.filter(p -> p.toString().endsWith(".fhi"))
                 .filter(Files::isRegularFile)
                 .forEach(fhiPath -> {
                     ImportFileResult result = importFile(fhiPath);
                     switch (result.status()) {
                         case IMPORTED -> imported.add(fhiPath);
                         case CORRUPTED -> corrupted.add(
                             new CorruptedFile(fhiPath, result.error()));
                         case SKIPPED -> skipped.add(fhiPath);
                     }
                 });
        } catch (IOException e) {
            System.err.println("Error walking directory: " + e.getMessage());
        }

        return new ImportResult(imported, corrupted, skipped);
    }

    /**
     * Import a single FHI file.
     */
    public ImportFileResult importFile(Path fhiPath) {
        try {
            String content = Files.readString(fhiPath, FHI_CHARSET);

            // Validate XML
            SaveResult validation = fileService.validateXmlContent(content);
            if (validation.isFailure()) {
                return ImportFileResult.corrupted(validation.getMessage());
            }

            // Store in integrity store
            IntegrityStore.Transaction tx = integrityStore.beginTransaction(
                fhiPath, null, content);
            tx.commit();

            return ImportFileResult.imported();

        } catch (IOException e) {
            return ImportFileResult.corrupted("Cannot read file: " + e.getMessage());
        }
    }

    /**
     * Verify all FHI files in directory against IntegrityStore.
     */
    public VerificationResult verifyDirectory(Path rootDirectory) {
        List<Path> valid = new ArrayList<>();
        List<CorruptedFile> corrupted = new ArrayList<>();
        List<Path> notTracked = new ArrayList<>();

        try (Stream<Path> paths = Files.walk(rootDirectory)) {
            paths.filter(p -> p.toString().endsWith(".fhi"))
                 .filter(Files::isRegularFile)
                 .forEach(fhiPath -> {
                     IntegrityStore.IntegrityCheckResult check =
                         integrityStore.verifyIntegrity(fhiPath);

                     switch (check.getStatus()) {
                         case VALID -> valid.add(fhiPath);
                         case CORRUPTED -> corrupted.add(
                             new CorruptedFile(fhiPath, check.getMessage()));
                         case UNKNOWN -> notTracked.add(fhiPath);
                         case MISSING -> corrupted.add(
                             new CorruptedFile(fhiPath, "File missing"));
                         case ERROR -> corrupted.add(
                             new CorruptedFile(fhiPath, check.getMessage()));
                     }
                 });
        } catch (IOException e) {
            System.err.println("Error walking directory: " + e.getMessage());
        }

        return new VerificationResult(valid, corrupted, notTracked);
    }

    /**
     * Attempt to repair corrupted files.
     */
    public RepairResult repairCorrupted(List<CorruptedFile> corrupted) {
        List<Path> repaired = new ArrayList<>();
        List<CorruptedFile> unrepairable = new ArrayList<>();

        for (CorruptedFile cf : corrupted) {
            try {
                String content = Files.readString(cf.path(), FHI_CHARSET);
                Optional<String> fixed = fileService.attemptXmlRepair(content);

                if (fixed.isPresent()) {
                    // Backup corrupted version
                    fileService.preserveCorruptedFile(cf.path());

                    // Write repaired version
                    SaveResult saveResult = fileService.saveFhi(cf.path(), fixed.get());
                    if (saveResult.isSuccess()) {
                        // Import into integrity store
                        IntegrityStore.Transaction tx = integrityStore.beginTransaction(
                            cf.path(), content, fixed.get());
                        tx.commit();
                        repaired.add(cf.path());
                    } else {
                        unrepairable.add(cf);
                    }
                } else {
                    unrepairable.add(cf);
                }
            } catch (IOException e) {
                unrepairable.add(new CorruptedFile(cf.path(),
                    cf.error() + "; repair failed: " + e.getMessage()));
            }
        }

        return new RepairResult(repaired, unrepairable);
    }

    // === Result types ===

    public record ImportResult(
        List<Path> imported,
        List<CorruptedFile> corrupted,
        List<Path> skipped
    ) {
        public int total() {
            return imported.size() + corrupted.size() + skipped.size();
        }

        public void printSummary() {
            System.out.println("=== FHI Import Summary ===");
            System.out.println("Total files found: " + total());
            System.out.println("Successfully imported: " + imported.size());
            System.out.println("Corrupted (need repair): " + corrupted.size());
            System.out.println("Skipped: " + skipped.size());

            if (!corrupted.isEmpty()) {
                System.out.println("\n=== Corrupted Files ===");
                for (CorruptedFile cf : corrupted) {
                    System.out.println("  " + cf.path().getFileName() + ": " + cf.error());
                }
            }
        }
    }

    public record VerificationResult(
        List<Path> valid,
        List<CorruptedFile> corrupted,
        List<Path> notTracked
    ) {
        public void printSummary() {
            System.out.println("=== FHI Verification Summary ===");
            System.out.println("Valid: " + valid.size());
            System.out.println("Corrupted: " + corrupted.size());
            System.out.println("Not tracked: " + notTracked.size());

            if (!corrupted.isEmpty()) {
                System.out.println("\n=== Corrupted Files ===");
                for (CorruptedFile cf : corrupted) {
                    System.out.println("  " + cf.path().getFileName() + ": " + cf.error());
                }
            }
        }
    }

    public record RepairResult(
        List<Path> repaired,
        List<CorruptedFile> unrepairable
    ) {
        public void printSummary() {
            System.out.println("=== Repair Summary ===");
            System.out.println("Repaired: " + repaired.size());
            System.out.println("Unrepairable: " + unrepairable.size());

            if (!unrepairable.isEmpty()) {
                System.out.println("\n=== Unrepairable Files ===");
                for (CorruptedFile cf : unrepairable) {
                    System.out.println("  " + cf.path().getFileName() + ": " + cf.error());
                }
            }
        }
    }

    public record CorruptedFile(Path path, String error) {}

    public record ImportFileResult(Status status, String error) {
        public enum Status { IMPORTED, CORRUPTED, SKIPPED }

        public static ImportFileResult imported() {
            return new ImportFileResult(Status.IMPORTED, null);
        }

        public static ImportFileResult corrupted(String error) {
            return new ImportFileResult(Status.CORRUPTED, error);
        }

        public static ImportFileResult skipped() {
            return new ImportFileResult(Status.SKIPPED, null);
        }
    }

    /**
     * Command-line entry point.
     */
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: FhiImporter <etmas-directory> [--verify|--repair]");
            System.out.println("  Default: import all FHI files into integrity store");
            System.out.println("  --verify: verify existing files against store");
            System.out.println("  --repair: attempt to repair corrupted files");
            System.exit(1);
        }

        Path etmasDir = Path.of(args[0]);
        if (!Files.isDirectory(etmasDir)) {
            System.err.println("Not a directory: " + etmasDir);
            System.exit(1);
        }

        IntegrityStore store = new IntegrityStore();
        FhiImporter importer = new FhiImporter(store);

        if (args.length > 1 && args[1].equals("--verify")) {
            VerificationResult result = importer.verifyDirectory(etmasDir);
            result.printSummary();
        } else if (args.length > 1 && args[1].equals("--repair")) {
            // First verify to find corrupted
            VerificationResult verification = importer.verifyDirectory(etmasDir);
            if (verification.corrupted().isEmpty()) {
                System.out.println("No corrupted files to repair.");
            } else {
                RepairResult repair = importer.repairCorrupted(verification.corrupted());
                repair.printSummary();
            }
        } else {
            ImportResult result = importer.importDirectory(etmasDir);
            result.printSummary();
        }
    }
}
