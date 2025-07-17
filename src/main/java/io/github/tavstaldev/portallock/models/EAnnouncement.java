package io.github.tavstaldev.portallock.models;

/**
 * Enum representing different types of announcements in the PortalLock plugin.
 */
public enum EAnnouncement {
    /** Announcement for successful entry. */
    Enter_Success,

    /** Announcement for failed entry. */
    Enter_Fail,

    /** Announcement for entry failure due to lack of permission. */
    Enter_Fail_Permission,

    /** Announcement for entry failure due to auto-unlock. */
    Enter_Fail_Auto_Unlock,

    /** Announcement for successful entry into an unlocked portal. */
    Enter_Unlocked,

    /** Announcement for successful exit. */
    Leave_Success,

    /** Announcement for failed exit. */
    Leave_Fail,

    /** Announcement for exit failure due to lack of permission. */
    Leave_Fail_Permission,

    /** Announcement for exit failure due to auto-unlock. */
    Leave_Fail_Auto_Unlock,

    /** Announcement for successful exit from an unlocked portal. */
    Leave_Unlocked
}
