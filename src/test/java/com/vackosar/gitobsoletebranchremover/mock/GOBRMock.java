package com.vackosar.gitobsoletebranchremover.mock;

import com.vackosar.gitobsoletebranchremover.mock.LocalRepoMock;
import com.vackosar.gitobsoletebranchremover.mock.UnZiper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class GOBRMock implements AutoCloseable {

    public Path dirName = null;

    private void delete(File f) {
        if (f.isDirectory()) {
            for (File c : f.listFiles()) {
                delete(c);
            }
        }
        if (!f.delete()) {
            throw new RuntimeException("Failed to delete file: " + f);
        }
    }

    public GOBRMock() throws IOException {
        final File zip =
                Arrays.asList(Paths.get(LocalRepoMock.TEST_WORK_DIR + "target")
                        .toFile()
                        .listFiles())
                        .stream()
                        .filter(file -> file.getName().endsWith(".zip"))
                        .findFirst()
                        .get();
        System.out.println(zip);
        new UnZiper().act(zip, new File(LocalRepoMock.TEST_WORK_DIR + "tmp"));
        dirName =  Paths.get(LocalRepoMock.TEST_WORK_DIR + "tmp/" + zip.getName().replaceAll("-bin.zip$", "")).normalize().toAbsolutePath().toRealPath();
    }

    public Process execute(List<String> args) throws IOException, InterruptedException {
        args.add(0, dirName.resolve("gobr.bat").toString());
        final Process process =
                new ProcessBuilder()
                        .command(args)
                        .directory(Paths.get(LocalRepoMock.TEST_WORK_DIR).resolve("tmp/local").toFile())
                        .start();
//        process.waitFor();
        return process;
    }

    public void close() {
        delete(dirName.toFile());
    }

}
