package com.github.spy.sea.monitor.common;
public final class BuildInfo {
    /** project version */
    public static final String VERSION = "${project.version}";
    /** SCM(git) revision */
    public static final String SCM_REVISION= "${buildNumber}";
    /** SCM branch */
    public static final String SCM_BRANCH = "${scmBranch}";
    /** build timestamp */
    public static final String TIMESTAMP ="${buildtimestamp}";
}