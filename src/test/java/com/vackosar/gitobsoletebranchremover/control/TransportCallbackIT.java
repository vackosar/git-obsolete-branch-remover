package com.vackosar.gitobsoletebranchremover.control;

import com.vackosar.gitobsoletebranchremover.entity.Arguments;
import com.vackosar.gitobsoletebranchremover.mock.GOBRMock;
import com.vackosar.gitobsoletebranchremover.mock.LocalRepoMock;
import com.vackosar.gitobsoletebranchremover.mock.RemoteSshRepo;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.transport.CredentialItem;
import org.eclipse.jgit.transport.URIish;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;

public class TransportCallbackIT {

    private String answer;

    @Test
    public void ssh() throws Exception {
        ConsoleCredentialsProvider providerMock = Mockito.mock(ConsoleCredentialsProvider.class);
        Mockito.when(providerMock.get(Mockito.any(URIish.class), Mockito.any(CredentialItem.class))).thenAnswer(this::answer);
        answer = "passphrase";
        final TrasportCallback trasportCallback = new TrasportCallback(new Arguments(new String[]{"30"}), providerMock);
        new RemoteSshRepo(trasportCallback).close();
    }

    @Test(expected = TransportException.class)
    public void sshFail() throws Exception {
        ConsoleCredentialsProvider providerMock = Mockito.mock(ConsoleCredentialsProvider.class);
        Mockito.when(providerMock.get(Mockito.any(URIish.class), Mockito.any(CredentialItem.class))).thenAnswer(this::answer);
        answer = "wrong";
        final TrasportCallback trasportCallback = new TrasportCallback(new Arguments(new String[]{"30"}), providerMock);
        new RemoteSshRepo(trasportCallback).close();
    }

    private Object answer(InvocationOnMock invocationOnMock) {
        final CredentialItem credentialItem = (CredentialItem) invocationOnMock.getArguments()[1];
        if (credentialItem instanceof CredentialItem.StringType) {
            ((CredentialItem.StringType) credentialItem).setValue(answer);
        } else if (credentialItem instanceof CredentialItem.CharArrayType) {
            ((CredentialItem.CharArrayType) credentialItem).setValue(answer.toCharArray());
        } else {
            throw new IllegalArgumentException(credentialItem.toString());
        }
        return true;
    }

    @Test
    public void prepareForManualConsoleTest() throws Exception {
        final String[] args = {"30", "--list", "--remote"};
        try (
                final LocalRepoMock localRepoMock = new LocalRepoMock();
                final GOBRMock gobrMock = new GOBRMock();
        ) {
            RemoteSshRepo.configureRemote(localRepoMock.get());
            Thread.sleep(5000);
        }
    }

}
