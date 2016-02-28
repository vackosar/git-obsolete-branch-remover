package com.vackosar.gitobsoletebranchremover.entity;

import org.eclipse.jgit.transport.URIish;
import org.junit.Assert;
import org.junit.Test;

import java.net.URISyntaxException;

public class URIishTest {

    @Test
    public void parseUser() throws URISyntaxException {
        Assert.assertEquals("username", new URIish("https://username@host/repo.git").getUser());
    }

}
