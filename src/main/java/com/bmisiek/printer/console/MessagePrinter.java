package com.bmisiek.printer.console;

import com.bmisiek.game.item.Item;
import com.bmisiek.game.room.Room;
import com.bmisiek.printer.contract.ActionNotFoundException;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.List;
import java.util.Stack;
import java.util.function.Function;

@Service
public class MessagePrinter {
    private final Stack<String> messageQueue = new Stack<>();

    public MessagePrinter immediate(Function<MessagePrinter, String> consumer) {
        String message = consumer.apply(this);
        System.out.println(message);
        return this;
    }

    public MessagePrinter delayed(Function<MessagePrinter, String> consumer) {
        String message = consumer.apply(this);
        messageQueue.push(message);
        return this;
    }

    public int dumpDelayed() {
        int count = messageQueue.size();
        messageQueue.forEach(System.out::println);
        messageQueue.clear();
        return count;
    }

    public String itemsInInventory() {
        return "You have items in your inventory (type 'inventory' to see them, 'use <number>' to use one)";
    }

    public String additionalRoomAction(@NotNull Room room) {
        return "You can " + room.GetAdditionalActionDescription() + " in this room (type 'search')";
    }

    public String itemFound(@NotNull String itemName) {
        return MessageFormat.format(
                "You found a {0}! It has been added to your inventory.",
                itemName
        );
    }

    public String damageTaken(int damage) {
        return MessageFormat.format("Player took {0} damage", damage);
    }

    public String playerDied() {
        return "Player died. Game over.";
    }

    public String healthRestored(int health) {
        return MessageFormat.format("Player has gained additional {0} health", health);
    }

    public String emptyDungeon() {
        return "Dungeon is empty.";
    }

    public String enteredDungeon() {
        return "Player entered the dungeon.";
    }

    public String actionNotFound(ActionNotFoundException exception) {
        return exception.getMessage();
    }

    public String technicalError(@NotNull Exception exception) {
        return "Technical error: " + exception.getMessage();
    }

    public String showInventory(List<Item> items) {
        var builder = new StringBuilder();
        builder.append("Inventory:");
        if (items.isEmpty()) {
            builder.append("  Your inventory is empty");
        } else {
            for (int i = 0; i < items.size(); i++) {
                Item item = items.get(i);
                builder.append("  ")
                       .append(i + 1)
                       .append(". ")
                       .append(item.getName())
                       .append(" - ")
                       .append(item.getDescription());
            }
        }

        return builder.toString();
    }

    public String floorCompleted(String newFloor) {
        return "Congratulations! You have reached floor %s".formatted(newFloor);
    }
}
