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
public class TransportCallback implements TransportConfigCallback {

    private final Optional<Path> key;
    private final CredentialsProvider credentialsProvider;

    @Inject
    public TransportCallback(Arguments arguments, ConsoleCredentialsProvider credentialsProvider) {
        this.key = arguments.key;
        this.credentialsProvider = credentialsProvider;
    }

    private JschConfigSessionFactory getFactory(Optional<Path> key) {
        return new JschConfigSessionFactory() {

            @Override
            protected void configure(OpenSshConfig.Host hc, Session session) {
                session.setUserInfo(new CredentialsProviderUserInfo(session, credentialsProvider));
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
        } else {
            transport.setCredentialsProvider(credentialsProvider);
        }
    }

}
