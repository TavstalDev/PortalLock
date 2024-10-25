package io.github.tavstal.portallock.models;

public class MessageChatComponent {
    @ConfigField(order = 1)
    public boolean Enable;
    @ConfigField(order = 2)
    public String Message;

    public MessageChatComponent() {}

    public MessageChatComponent(boolean enable, String message) {
        Enable = enable;
        Message = message;
    }
}
