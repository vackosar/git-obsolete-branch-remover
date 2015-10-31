package com.vackosar.gitiproblemnotifier;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.RepositoryNotFoundException;
import org.eclipse.jgit.internal.storage.dfs.DfsRepositoryDescription;
import org.eclipse.jgit.internal.storage.dfs.InMemoryRepository;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.Daemon;
import org.eclipse.jgit.transport.DaemonClient;
import org.eclipse.jgit.transport.ServiceMayNotContinueException;
import org.eclipse.jgit.transport.resolver.RepositoryResolver;
import org.eclipse.jgit.transport.resolver.ServiceNotAuthorizedException;
import org.eclipse.jgit.transport.resolver.ServiceNotEnabledException;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

public class RemoteRepoMock {

    public static final String REPO_URL = "git://localhost/repo.git";
    private static final File DATA_ZIP = new File("src/test/resource/.git.zip");
    private static final File REPO_DIR = new File("tmp/remote");

    private Map<String, Repository> repositories = new HashMap<>();
    private boolean bare;

    public RemoteRepoMock(boolean bare) {
        this.bare = bare;
        if (bare) {
            try {delete(REPO_DIR);} catch (Exception e) {}
            REPO_DIR.mkdir();
        } else {
            prepareTestingData();
        }
        start();
    }

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


    private void start() {
        try {
            Daemon server = new Daemon(new InetSocketAddress(9418));
            server.getService("git-receive-pack").setEnabled(true);
            server.setRepositoryResolver(new RepositoryResolverImplementation());
            server.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void prepareTestingData() {
        new UnZiper().act(DATA_ZIP, REPO_DIR);
    }

    private final class RepositoryResolverImplementation implements
            RepositoryResolver<DaemonClient> {
        @Override
        public Repository open(DaemonClient client, String name)
                throws RepositoryNotFoundException,
                ServiceNotAuthorizedException, ServiceNotEnabledException,
                ServiceMayNotContinueException {
            Repository repo = repositories.get(name);
            if (repo == null) {
                try {
                    repo = new FileRepositoryBuilder().setGitDir(REPO_DIR).build();
                    if (bare) {
                        repo.create(bare);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                repositories.put(name, repo);
            }
            return repo;
        }
    }

}
