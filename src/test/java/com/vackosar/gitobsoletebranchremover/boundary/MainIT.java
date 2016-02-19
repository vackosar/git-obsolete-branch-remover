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

import java.io.PrintWriter;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class MainIT {

//    @Test
    public void fetchSsh() throws Exception {
        final String[] args = {"30", "--list", "--remote"};
        final SshTrasportCallback trasportCallback = new SshTrasportCallback(new Arguments(args));
        try (
                final LocalRepoMock localRepoMock = new LocalRepoMock();
                final GOBRMock gobrMock = new GOBRMock();
        ) {
            RemoteSshRepo.configureRemote(localRepoMock.get());
            final Process process = gobrMock.execute(new ArrayList<>(Arrays.asList(args)));
            new PrintWriter(process.getOutputStream()).write("ubuntu\n");
            print(process.getInputStream());
            Assert.assertEquals(
                    "branch1\t2015-11-01\tvackosar@github.com" + System.lineSeparator()
                            + "branch2\t2015-11-01\tvackosar@github.com" + System.lineSeparator(),
                    convertStreamToString(process.getInputStream())
            );
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
    public void main() throws Exception {
        try (
                final RemoteRepoMock remoteRepoMock = new RemoteRepoMock(false);
                final LocalRepoMock localRepoMock = new LocalRepoMock(remoteRepoMock.repoUrl);
                final GOBRMock gobrMock = new GOBRMock();
        ) {
            final Process process = gobrMock.execute(new ArrayList<>(Arrays.asList("30", "--list", "--remote")));
            new PrintWriter(process.getOutputStream()).write("ubuntu\n");
            Assert.assertEquals(
                    "branch1\t2015-11-01\tvackosar@github.com" + System.lineSeparator()
                            + "branch2\t2015-11-01\tvackosar@github.com" + System.lineSeparator(),
                    convertStreamToString(process.getInputStream())
            );
            process.waitFor();
        }
    }

    private static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    private static void print(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        final Instant start = Instant.now().plusSeconds(5);
        while (s.hasNext() && start.isAfter(Instant.now())) {
            System.out.println(s.next());
        }
    }
}
