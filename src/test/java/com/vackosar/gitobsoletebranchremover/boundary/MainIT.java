package com.vackosar.gitobsoletebranchremover.boundary;

import com.vackosar.gitobsoletebranchremover.mock.GOBRMock;
import com.vackosar.gitobsoletebranchremover.mock.LocalRepoMock;
import com.vackosar.gitobsoletebranchremover.mock.RemoteRepoMock;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainIT {

    @Test
    public void listRemote() throws Exception {
        final List<String> args = Arrays.asList("0", "--list", "--remote");
        Assert.assertEquals(
                "branch1\t2015-11-01\tvackosar@github.com\tmerged" + System.lineSeparator()
                + "branch2\t2015-11-01\tvackosar@github.com\tmerged" + System.lineSeparator()
                + "branch3\t2016-02-27\tvackosar@github.com\tunmerged" + System.lineSeparator(),
                execute(args)
        );
    }

    @Test
    public void printUsage() throws Exception {
        final List<String> args = Arrays.asList("0", "--liXXst", "--remote");
        Assert.assertTrue(execute(args).startsWith("usage"));
    }

    private String execute(List<String> args) throws IOException, InterruptedException, GitAPIException {
        try (
                final RemoteRepoMock remoteRepoMock = new RemoteRepoMock(false);
                final LocalRepoMock localRepoMock = new LocalRepoMock(remoteRepoMock.repoUrl);
                final GOBRMock gobrMock = new GOBRMock();
        ) {
            final Process process = gobrMock.execute(new ArrayList<>(args));
            process.waitFor();
            return convertStreamToString(process.getInputStream());
        }
    }

    private static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    private void print(final Process process) throws IOException {
        new Thread() {
            @Override
            public void run() {
                try (final BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));)  {
                    reader.lines().forEach(System.out::println);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }.start();
        new Thread() {
            @Override
            public void run() {
                try (final BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));)  {
                    reader.lines().forEach(System.err::println);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }.start();
    }
}
