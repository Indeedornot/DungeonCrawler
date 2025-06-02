package com.bmisiek.printer.console;

import com.bmisiek.game.dungeon.DungeonManagerInterface;
import com.bmisiek.game.event.*;
import com.bmisiek.game.item.Item;
import com.bmisiek.game.player.Player;
import com.bmisiek.game.room.Room;
import com.bmisiek.printer.console.printers.DungeonPlayerPrinter;
import com.bmisiek.printer.console.printers.DungeonPrinter;
import com.bmisiek.printer.contract.ActionNotFoundException;
import com.bmisiek.printer.contract.GameAction;
import com.bmisiek.printer.contract.GuiInterface;
import com.bmisiek.printer.contract.actions.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.*;
import java.util.function.Function;

@Service
public class ConsoleInterface implements GuiInterface, ApplicationListener<ApplicationEvent> {
    private final DungeonPrinter dungeonPrinter;
    private final DungeonPlayerPrinter dungeonPlayerPrinter;

    /**
     * Used to schedule messages to be fired only after the next turn has been rendered
     */
    private final Stack<String> messageQueue = new Stack<>();

    public ConsoleInterface(DungeonPrinter dungeonPrinter,
                            DungeonPlayerPrinter dungeonPlayerPrinter) {
        this.dungeonPrinter = dungeonPrinter;
        this.dungeonPlayerPrinter = dungeonPlayerPrinter;
    }

    private void PrintNewLines(int count) {
        for (int i = 0; i < count; i++) {
            System.out.println();
        }
    }

    private void PrintUpdateSeparator() {
        PrintNewLines(2);
        System.out.println("-".repeat(20));
        PrintNewLines(2);
    }

    private void DumpMessageQueue() {
        messageQueue.forEach(System.out::println);
        messageQueue.clear();
    }

    public void Update(@NotNull DungeonManagerInterface dungeon) {
        dungeonPrinter.Print(dungeon);
        dungeonPlayerPrinter.Print(dungeon);

        DumpMessageQueue();
        PrintUpdateSeparator();
    }

    // Map of command names to action creators
    private static final Map<String, Function<String, GameAction>> ACTION_CREATORS = Map.of(
        "up", param -> MoveAction.UP,
        "down", param -> MoveAction.DOWN,
        "left", param -> MoveAction.LEFT,
        "right", param -> MoveAction.RIGHT,
        "search", param -> SearchAction.INSTANCE,
        "inventory", param -> InventoryAction.INSTANCE,
        "use", param -> {
            try {
                int index = Integer.parseInt(param) - 1; // Convert to 0-based
                return new UseItemAction(index);
            } catch (NumberFormatException e) {
                return null; // Invalid parameter
            }
        }
    );

    public GameAction Act(DungeonManagerInterface dungeon, Player player) throws RuntimeException {
        Room currentRoom = dungeon.getRooms().get(dungeon.getPlayerPosition());

        if (currentRoom.HasAdditionalActions()) {
            messageQueue.add("You can " + currentRoom.GetAdditionalActionDescription() + " in this room (type 'search')");
        }

        if (!player.getInventory().isEmpty()) {
            messageQueue.add("You have items in your inventory (type 'inventory' to see them, 'use <number>' to use one)");
        }

        Scanner scanner = new Scanner(System.in);
        while (true) {
            try {
                String input = scanner.nextLine().trim();
                String[] parts = input.split("\\s+", 2);
                String command = parts[0].toLowerCase();
                String parameter = parts.length > 1 ? parts[1] : "";

                if (!ACTION_CREATORS.containsKey(command)) {
                    throw new ActionNotFoundException("Unknown command: " + command);
                }

                GameAction action = ACTION_CREATORS.get(command).apply(parameter);

                if (action == null) {
                    messageQueue.add("Invalid parameter for command: " + command);
                    continue;
                }

                // Handle inventory action
                if (action instanceof InventoryAction) {
                    showInventory(player);
                    // No longer using continue - we return the action and GameLoop will handle
                    // requesting a new action based on consumesTurn()
                }

                return action;

            } catch (ActionNotFoundException e) {
                System.out.println(e.getMessage());
            } catch (Exception e) {
                System.out.println("Technical Error: " + e.getMessage());
            }
        }
    }

    private void showInventory(Player player) {
        System.out.println("Inventory:");
        var inventory = player.getInventory();
        if (inventory.isEmpty()) {
            System.out.println("  Your inventory is empty");
        } else {
            for (int i = 0; i < inventory.size(); i++) {
                Item item = inventory.get(i);
                System.out.println("  " + (i + 1) + ". " + item.getName() + " - " + item.getDescription());
            }
        }
    }

    @Override
    public void onApplicationEvent(@NotNull ApplicationEvent event) {
        switch (event) {
            case DungeonEnteredEvent dungeonEnteredEvent -> onDungeonEnteredEvent(dungeonEnteredEvent);
            case DamageTakenEvent damageTakenEvent -> onDamageTakenEvent(damageTakenEvent);
            case HealedEvent healedEvent -> onHealedEvent(healedEvent);
            case PlayerDiedEvent diedEvent -> onDiedEvent(diedEvent);
            case DungeonEmptyEvent dungeonEmptyEvent -> onDungeonEmptyEvent(dungeonEmptyEvent);
            case ItemFoundEvent itemFoundEvent -> onItemFoundEvent(itemFoundEvent);
            default -> {
            }
        }
    }

    private void onDamageTakenEvent(DamageTakenEvent event) {
        messageQueue.add(MessageFormat.format("Player took {0} damage", event.getEventData().getDamage()));
    }

    private void onDungeonEnteredEvent(DungeonEnteredEvent event) {
        messageQueue.add("Player entered the dungeon");
    }

    private void onHealedEvent(HealedEvent event) {
        messageQueue.add(MessageFormat.format("Player has gained additional {0} health", event.getEventData().getHeal()));
    }

    private void onDiedEvent(PlayerDiedEvent event) {
        messageQueue.add("Player died");
    }

    private void onDungeonEmptyEvent(DungeonEmptyEvent event) {
        DumpMessageQueue();
        System.out.println("Dungeon is empty");
    }

    private void onItemFoundEvent(ItemFoundEvent event) {
        messageQueue.add(MessageFormat.format("You found a {0}! It has been added to your inventory.",
                event.getEventData().getItem().getName()));
    }
}
