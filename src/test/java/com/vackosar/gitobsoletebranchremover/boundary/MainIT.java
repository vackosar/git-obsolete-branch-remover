package com.vackosar.gitobsoletebranchremover.boundary;

import com.vackosar.gitobsoletebranchremover.control.SshTrasportCallback;
import com.vackosar.gitobsoletebranchremover.entity.Arguments;
import com.vackosar.gitobsoletebranchremover.mock.GOBRMock;
import com.vackosar.gitobsoletebranchremover.mock.LocalRepoMock;
import com.vackosar.gitobsoletebranchremover.mock.RemoteRepoMock;
import com.vackosar.gitobsoletebranchremover.mock.RemoteSshRepo;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainIT {

//    @Test
    public void fetchSsh() throws Exception {
        final String[] args = {"30", "--list", "--remote"};
        try (
                final LocalRepoMock localRepoMock = new LocalRepoMock();
                final GOBRMock gobrMock = new GOBRMock();
        ) {
            RemoteSshRepo.configureRemote(localRepoMock.get());
            final Process process = gobrMock.execute(new ArrayList<>(Arrays.asList(args)));
            new PrintWriter(process.getOutputStream()).write("ubuntu\n");
            print(process);
            process.waitFor();
        }
    }

    @Test
    public void ssh() throws Exception {
        final SshTrasportCallback trasportCallback = new SshTrasportCallback(new Arguments(new String[]{"30"}));
        trasportCallback.setPassword("ubuntu");
        new RemoteSshRepo(trasportCallback).close();
    }

    @Test
    public void listRemote() throws Exception {
        final List<String> args = Arrays.asList("30", "--list", "--remote");
        Assert.assertEquals(
                "branch1\t2015-11-01\tvackosar@github.com" + System.lineSeparator()
                        + "branch2\t2015-11-01\tvackosar@github.com" + System.lineSeparator(),
                execute(args)
        );
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
