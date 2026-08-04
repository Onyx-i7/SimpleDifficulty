package com.charles445.simpledifficulty.util;

/**
 * Utility class for parsing and comparing semantic version strings (major.minor.patch).
 */
public class VersionDelimiter {
    public int major;
    public int minor;
    public int patch;

    public VersionDelimiter(String ss) {
        if (ss == null || ss.isEmpty()) {
            wipeVersion();
            return;
        }

        String[] split = ss.split("\\.");

        if (split.length != 3) {
            wipeVersion();
            return;
        }

        try {
            this.major = Integer.parseInt(split[0]);
            this.minor = Integer.parseInt(split[1]);
            this.patch = Integer.parseInt(split[2]);
        } catch (NumberFormatException e) {
            wipeVersion();
        }
    }

    public VersionDelimiter(int major, int minor, int patch) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
    }

    public boolean isSameOrNewerVersion(VersionDelimiter vd) {
        return isSameOrNewerVersion(vd.major, vd.minor, vd.patch);
    }

    public boolean isSameOrNewerVersion(int major, int minor) {
        return isSameOrNewerVersion(major, minor, 0);
    }

    public boolean isSameOrNewerVersion(int major, int minor, int patch) {
        if (this.major > major) {
            return true;
        } else if (this.major == major) {
            if (this.minor > minor) {
                return true;
            } else if (this.minor == minor) {
                return this.patch >= patch;
            }
        }
        return false;
    }

    private void wipeVersion() {
        this.major = 0;
        this.minor = 0;
        this.patch = 0;
    }

    @Override
    public String toString() {
        return major + "." + minor + "." + patch;
    }
}