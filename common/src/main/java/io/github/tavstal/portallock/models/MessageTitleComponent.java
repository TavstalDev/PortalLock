package io.github.tavstal.portallock.models;

public class MessageTitleComponent {
    public boolean Enable;
    public String Title;
    public String Subtitle;

    public MessageTitleComponent(boolean enable, String title, String subtitle) {
        Enable = enable;
        Title = title;
        Subtitle = subtitle;
    }
}
