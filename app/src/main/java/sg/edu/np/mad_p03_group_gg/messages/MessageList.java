package sg.edu.np.mad_p03_group_gg.messages;

public class MessageList {
    private String name;
    private String id;
    private String lastMessage;
    private int unseenMessages;
    private String profilePic;
    private String chatKey;

    public MessageList(String name, String id, String lastMessage, String profilePic, String chatKey, int unseenMessages) {
        this.name = name;
        this.id = id;
        this.lastMessage = lastMessage;
        this.profilePic = profilePic;
        this.unseenMessages = unseenMessages;
        this.chatKey = chatKey;
    }

    // Getter
    public String getName() {
        return name;
    }

    public String getid() {
        return id;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public int getUnseenMessages() {
        return unseenMessages;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public String getChatKey() {
        return chatKey;
    }
}
