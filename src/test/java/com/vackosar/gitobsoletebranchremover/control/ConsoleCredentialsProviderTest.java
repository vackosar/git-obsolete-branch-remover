package com.vackosar.gitobsoletebranchremover.control;

import com.vackosar.gitobsoletebranchremover.boundary.Console;
import org.eclipse.jgit.transport.CredentialItem;
import org.eclipse.jgit.transport.URIish;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.net.URISyntaxException;

@RunWith(MockitoJUnitRunner.class)
public class ConsoleCredentialsProviderTest {

    public static final String URI = "https://example.com/git.git";
    public static final String PROMPT_TEXT = "Yes No?";
    public static final String Y = "y";

    @Mock private Console console;
    @InjectMocks private ConsoleCredentialsProvider provider = new ConsoleCredentialsProvider();;

    @Test
    public void test() throws URISyntaxException {
        Mockito.when(console.readLine(Mockito.anyString())).thenReturn(Y);
        CredentialItem.YesNoType yesNoType = new CredentialItem.YesNoType(PROMPT_TEXT);
        Assert.assertTrue(provider.get(new URIish(URI), yesNoType));
        Assert.assertTrue(yesNoType.getValue());
        CredentialItem.YesNoType yesNoType2 = new CredentialItem.YesNoType(PROMPT_TEXT);
        Assert.assertTrue(provider.get(new URIish(URI), yesNoType2));
        Assert.assertTrue(yesNoType2.getValue());
    }

}
