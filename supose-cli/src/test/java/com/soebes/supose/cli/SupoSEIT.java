package com.soebes.supose.cli;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.log4j.Logger;
import org.testng.annotations.Test;
import org.tmatesoft.svn.core.SVNException;

public class SupoSEIT {
    private static Logger LOGGER = Logger.getLogger(SupoSEIT.class);

    @Test
    public void versionOptionTest() throws SVNException {
        SupoSECLI cli = new SupoSECLI();
        String[] args = { "--version" };
        cli.run(args);
        assertThat( cli.getReturnCode()).isEqualTo(0);
    }

}
