package com.vackosar.gitobsoletebranchremover.control;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.vackosar.gitobsoletebranchremover.entity.Arguments;
import org.eclipse.jgit.api.TransportConfigCallback;
import org.eclipse.jgit.transport.*;
import org.eclipse.jgit.util.FS;

import java.nio.file.Path;
import java.util.Optional;

@Singleton
public class SshTrasportCallback implements TransportConfigCallback {

    private final Optional<Path> key;
    private String password = null;
    private URIish uri;
    private String username;

    @Inject
    public SshTrasportCallback(Arguments arguments) {
        this.key = arguments.key;
    }

    private JschConfigSessionFactory getFactory(Optional<Path> key) {
        return new JschConfigSessionFactory() {

            @Override
            protected void configure(OpenSshConfig.Host hc, Session session) {
                session.setPassword(password);
            }

            @Override
            protected JSch createDefaultJSch(FS fs) throws JSchException {
                final JSch defaultJSch = super.createDefaultJSch(fs);
                if (key.isPresent()) {
                    defaultJSch.addIdentity(key.toString());
                }
                return defaultJSch;
            }
        };
    }

    @Override
    public void configure(Transport transport) {
        if (transport instanceof SshTransport) {
            ((SshTransport) transport).setSshSessionFactory(getFactory(key));
        } else if (username != null) {
            transport.setCredentialsProvider(new UsernamePasswordCredentialsProvider(username, password));
        }
        uri = transport.getURI();
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public URIish getUri() {
        return uri;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
