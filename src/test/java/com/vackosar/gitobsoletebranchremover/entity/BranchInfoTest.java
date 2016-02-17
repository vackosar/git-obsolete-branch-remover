package com.vackosar.gitobsoletebranchremover.entity;

import com.vackosar.gitobsoletebranchremover.entity.BranchInfo;
import com.vackosar.gitobsoletebranchremover.entity.BranchType;
import org.junit.Assert;
import org.junit.Test;

public class BranchInfoTest {
    @Test
    public void removePrefix() {
        final BranchInfo info = new BranchInfo(null, "refs/remotes/origin/feature/alpha", "a@b.cz");
        Assert.assertEquals("feature/alpha", info.branchName);
        Assert.assertEquals("origin", info.remoteName.get());
        Assert.assertEquals(BranchType.remote, info.branchType);
    }
}
