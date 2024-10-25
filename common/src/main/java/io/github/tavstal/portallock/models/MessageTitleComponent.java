package io.github.tavstal.portallock.models;

public class MessageTitleComponent {
    @ConfigField(order = 1)
    public boolean Enable;
    @ConfigField(order = 2)
    public String Title;
    @ConfigField(order = 3)
    public String Subtitle;

    public MessageTitleComponent() {}

    public MessageTitleComponent(boolean enable, String title, String subtitle) {
        Enable = enable;
        Title = title;
        Subtitle = subtitle;
    }
}
