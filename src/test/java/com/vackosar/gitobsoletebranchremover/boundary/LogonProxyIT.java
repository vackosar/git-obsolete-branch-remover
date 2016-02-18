package com.vackosar.gitobsoletebranchremover.boundary;

import com.vackosar.gitobsoletebranchremover.control.LogonProxy;
import com.vackosar.gitobsoletebranchremover.control.SshTrasportCallback;
import com.vackosar.gitobsoletebranchremover.entity.Arguments;
import com.vackosar.gitobsoletebranchremover.mock.RemoteSshRepo;
import org.junit.Test;

import java.io.ByteArrayInputStream;

public class LogonProxyIT {

    @Test
    public void fetch() throws Exception {
        System.setIn(new ByteArrayInputStream("ubuntu\n".getBytes("UTF-8")));
        final SshTrasportCallback trasportCallback = new SshTrasportCallback(new Arguments(new String[]{"30"}));
        final LogonProxy logonProxy = new LogonProxy(trasportCallback);
        logonProxy.call(() -> { new RemoteSshRepo(trasportCallback); });
    }

    public static void main(String[] args) throws Exception {
        new LogonProxyIT().fetch();
    }

}
