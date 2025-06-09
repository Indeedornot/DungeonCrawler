package com.bmisiek.printer.console;

import com.bmisiek.game.dungeon.DungeonManagerInterface;
import com.bmisiek.game.event.*;
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

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;

@Service
public class ConsoleInterface implements GuiInterface, ApplicationListener<ApplicationEvent> {
    private final DungeonPrinter dungeonPrinter;
    private final DungeonPlayerPrinter dungeonPlayerPrinter;
    private MessagePrinter messagePrinter;

    public ConsoleInterface(DungeonPrinter dungeonPrinter,
                            DungeonPlayerPrinter dungeonPlayerPrinter,
                            MessagePrinter messagePrinter) {
        this.dungeonPrinter = dungeonPrinter;
        this.dungeonPlayerPrinter = dungeonPlayerPrinter;
        this.messagePrinter = messagePrinter;
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

    public void Update(@NotNull DungeonManagerInterface dungeon) {
        dungeonPrinter.Print(dungeon);
        dungeonPlayerPrinter.Print(dungeon);

        messagePrinter.dumpDelayed();
        PrintUpdateSeparator();
    }

    private static final Map<String, Function<String, Optional<GameAction>>> ACTION_CREATORS = Map.of(
        "up", _ -> Optional.of(MoveAction.UP),
        "down", _ -> Optional.of(MoveAction.DOWN),
        "left", _ -> Optional.of(MoveAction.LEFT),
        "right", _ -> Optional.of(MoveAction.RIGHT),
        "search", _ -> Optional.of(SearchAction.INSTANCE),
        "inventory", _ -> Optional.of(InventoryAction.INSTANCE),
        "use", param -> {
            try {
                int index = Integer.parseInt(param) - 1; // Convert to 0-based
                return Optional.of(new UseItemAction(index));
            } catch (NumberFormatException e) {
                return Optional.empty(); // Invalid parameter
            }
        }
    );

    private final Map<GameAction, BiConsumer<Player, GameAction>> UI_ACTION_HANDLERS = Map.of(
        InventoryAction.INSTANCE, (player, action) -> messagePrinter.immediate(f -> f.showInventory(player.getInventory()))
    );

    public @NotNull GameAction GetAction(DungeonManagerInterface dungeon, Player player) throws RuntimeException {
        Room currentRoom = dungeon.getRooms().get(dungeon.getPlayerPosition());

        if (currentRoom.HasAdditionalActions()) {
            messagePrinter.immediate(f -> f.additionalRoomAction(currentRoom));
        }

        if (!player.getInventory().isEmpty()) {
            messagePrinter.immediate(MessagePrinter::itemsInInventory);
        }

        Scanner scanner = new Scanner(System.in);
        while (true) {
            try {
                GameAction action = ReadCommand(scanner);
                HandleUIAction(player, action);

                return action;

            } catch (ActionNotFoundException e) {
                messagePrinter.delayed(f -> f.actionNotFound(e));
            } catch (Exception e) {
                messagePrinter.delayed(f -> f.technicalError(e));
            }
        }
    }

    private @NotNull GameAction ReadCommand(Scanner scanner) throws ActionNotFoundException {
        String input = scanner.nextLine().trim();
        String[] parts = input.split("\\s+", 2);
        String command = parts[0].toLowerCase();
        String parameter = parts.length > 1 ? parts[1] : "";

        return Optional.ofNullable(ACTION_CREATORS.get(command))
                .orElseThrow(() -> new ActionNotFoundException("Unknown command: " + command))
                .apply(parameter)
                .orElseThrow(() -> new ActionNotFoundException("Invalid parameter for command: " + command));
    }

    private void HandleUIAction(Player player, GameAction action) {
        UI_ACTION_HANDLERS.getOrDefault(action, (_, _) -> {}).accept(player, action);
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
            default -> {}
        }
    }

    private void onDamageTakenEvent(DamageTakenEvent event) {
        var damage = event.getEventData().getDamage();
        messagePrinter.delayed(f -> f.damageTaken(damage));
    }

    private void onDungeonEnteredEvent(DungeonEnteredEvent event) {
        messagePrinter.delayed(MessagePrinter::enteredDungeon);
    }

    private void onHealedEvent(HealedEvent event) {
        var healing = event.getEventData().getHeal();
        messagePrinter.delayed(f -> f.healthRestored(healing));
    }

    private void onDiedEvent(PlayerDiedEvent event) {
        messagePrinter.delayed(MessagePrinter::playerDied);
    }

    private void onDungeonEmptyEvent(DungeonEmptyEvent event) {
        messagePrinter.dumpDelayed();
        messagePrinter.immediate(MessagePrinter::emptyDungeon);
    }

    private void onItemFoundEvent(ItemFoundEvent event) {
        var itemName = event.getEventData().getItem().getName();
        messagePrinter.immediate(f -> f.itemFound(itemName));
        messagePrinter.immediate(MessagePrinter::itemsInInventory);
    }
}
