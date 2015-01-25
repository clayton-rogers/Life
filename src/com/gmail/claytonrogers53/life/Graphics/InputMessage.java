package com.gmail.claytonrogers53.life.Graphics;

/**
 * Carried the information of one "input". This input can be a scroll or pan event.
 *
 * Created by Clayton on 11/12/2014.
 */
public class InputMessage {

    // A type used to encode the type of message.
    public enum MessageType {
        SCROLL_X,
        SCROLL_Y,
        ZOOM
    }

    /** The type of the message */
    private final MessageType messageType;

    /** The value of the message. */
    private final double data;


    /**
     * Creates a new message with the given spec.
     *
     * @param messageType
     *        The type of the message (scroll/zoom).
     *
     * @param data
     *        The data of the message (how far to zoom/scroll).
     */
    InputMessage (MessageType messageType, double data) {
        this.messageType = messageType;
        this.data = data;
    }

    /**
     * Returns the type of the message.
     *
     * @return The type of the message.
     */
    public MessageType getMessageType() {
        return messageType;
    }

    /**
     * Returns the data of the message.
     *
     * @return The data of the message.
     */
    public double getData() {
        return data;
    }

    /**
     * Allows the easy printing of messages.
     *
     * @return A string representation of the message.
     */
    @Override
    public String toString() {
        return "InputMessage{" +
                "data=" + data +
                ", messageType=" + messageType +
                '}';
    }
}
