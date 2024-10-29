package io.github.tavstal.portallock.models;

public class InteractableChatComponent {

    public String Text;
    public boolean SuggestCommand;
    public String Command;
    public String HoverText;

    public InteractableChatComponent(String text, boolean suggestCommand, String command, String hoverText) {
        Text = text;
        SuggestCommand = suggestCommand;
        Command = command;
        HoverText = hoverText;
    }

    public InteractableChatComponent(String text, String command, String hoverText) {
        Text = text;
        SuggestCommand = false;
        Command = command;
        HoverText = hoverText;
    }

    public InteractableChatComponent(String text, boolean suggestCommand, String command) {
        Text = text;
        SuggestCommand = suggestCommand;
        Command = command;
        HoverText = null;
    }

    public InteractableChatComponent(String text, String command) {
        Text = text;
        SuggestCommand = false;
        Command = command;
        HoverText = null;
    }

    public InteractableChatComponent(String text) {
        Text = text;
        SuggestCommand = false;
        Command = null;
        HoverText = null;
    }
}
