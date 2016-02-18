package com.vackosar.gitobsoletebranchremover.control;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.TransportException;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Singleton
public class LogonProxy {

    private SshTrasportCallback trasportCallback;

    @Inject
    public LogonProxy(SshTrasportCallback trasportCallback) {
        this.trasportCallback = trasportCallback;
    }

    public interface Callback { void act() throws GitAPIException; }

    public void call(Callback callback) throws GitAPIException {
        try {
            callback.act();
        } catch (TransportException e) {
            System.out.println("Authentification failed.");
            if ("https".equals(trasportCallback.getUri().getScheme())) {
                System.out.println("Provide username.");
                final String username = readLine();
                trasportCallback.setUsername(username);
            }
            System.out.println("Provide password.");
            final String password = readLine();
            trasportCallback.setPassword(password);
        }
    }

    public static String readLine() {
        try {
            InputStreamReader streamReader = new InputStreamReader(System.in);
            BufferedReader bufferedReader = new BufferedReader(streamReader);
            return bufferedReader.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
//        return System.console().readLine();
    }
}
