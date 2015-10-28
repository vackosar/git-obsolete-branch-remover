package com.vackosar.gitiproblemnotifier;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.RepositoryNotFoundException;
import org.eclipse.jgit.internal.storage.dfs.DfsRepositoryDescription;
import org.eclipse.jgit.internal.storage.dfs.InMemoryRepository;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.Daemon;
import org.eclipse.jgit.transport.DaemonClient;
import org.eclipse.jgit.transport.ServiceMayNotContinueException;
import org.eclipse.jgit.transport.resolver.RepositoryResolver;
import org.eclipse.jgit.transport.resolver.ServiceNotAuthorizedException;
import org.eclipse.jgit.transport.resolver.ServiceNotEnabledException;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

public class RepoMock {

    private static Map<String, Repository> repositories = new HashMap<>();

    public static void start() throws GitAPIException, IOException, URISyntaxException {
        Daemon server = new Daemon(new InetSocketAddress(9418));
        server.getService("git-receive-pack").setEnabled(true);
        server.setRepositoryResolver(new RepositoryResolverImplementation());
        server.start();
    }

    private static final class RepositoryResolverImplementation implements
            RepositoryResolver<DaemonClient> {
        @Override
        public Repository open(DaemonClient client, String name)
                throws RepositoryNotFoundException,
                ServiceNotAuthorizedException, ServiceNotEnabledException,
                ServiceMayNotContinueException {
            Repository repo = repositories.get(name);
            if (repo == null) {
                try {
                    repo = new InMemoryRepository(new DfsRepositoryDescription(name));
                    repo.create(true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                repositories.put(name, repo);
            }
            return repo;
        }
    }

}
