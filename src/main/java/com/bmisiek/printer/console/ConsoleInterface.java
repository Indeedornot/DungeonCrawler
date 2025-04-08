package com.bmisiek.printer.console;

import com.bmisiek.game.dungeon.DungeonInterface;
import com.bmisiek.game.event.DamageTakenEvent;
import com.bmisiek.game.event.DungeonEnteredEvent;
import com.bmisiek.game.player.Player;
import com.bmisiek.printer.console.printers.DungeonPrinter;
import com.bmisiek.printer.contract.ActionNotFoundException;
import com.bmisiek.printer.contract.GameAction;
import com.bmisiek.printer.contract.GuiInterface;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

@Service
public class ConsoleInterface implements GuiInterface, ApplicationListener<ApplicationEvent> {
    private final DungeonPrinter dungeonPrinter;

    public ConsoleInterface(DungeonPrinter dungeonPrinter) {
        this.dungeonPrinter = dungeonPrinter;
    }

    private void PrintNewLines(int count) {
        for(int i = 0; i < count; i++){
            System.out.println();
        }
    }

    private void SeparateUpdates(){
        PrintNewLines(2);
        System.out.println("-".repeat(20));
        PrintNewLines(2);
    }

    public void Update(@NotNull DungeonInterface dungeon) {
        dungeonPrinter.Print(dungeon);
        SeparateUpdates();
    }

    private static final HashMap<String, GameAction> actions = new HashMap<>() {{
        put("down", GameAction.MOVE_DOWN);
        put("up", GameAction.MOVE_UP);
        put("right", GameAction.MOVE_RIGHT);
        put("left", GameAction.MOVE_LEFT);
    }};

    public GameAction Act(DungeonInterface dungeon, Player player) throws RuntimeException {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            try {
                return GetAction(scanner);
            } catch (ActionNotFoundException e) {
                System.out.println(e.getMessage());
            } catch (Exception e) {
                System.out.println("Technical Error");
            }
        }
    }

    private static GameAction GetAction(Scanner scanner) throws ActionNotFoundException {
        var command = scanner.nextLine();

        //TODO: Possible parameters in the future
        var commandName = Arrays.stream(command.split(" ")).findFirst().orElseThrow();
        AssertValidCommand(commandName);

        return actions.get(commandName);
    }

    private static void AssertValidCommand(String commandName) throws ActionNotFoundException {
        if(commandName == null || commandName.isEmpty()){
            throw new ActionNotFoundException("Empty command");
        }

        if(!actions.containsKey(commandName)){
            throw new ActionNotFoundException("Invalid command");
        }
    }

    private void printLine(String line) {
        System.console().printf(line + System.lineSeparator());
    }

    @Override
    public void onApplicationEvent(@NotNull ApplicationEvent event) {
        if (event instanceof DungeonEnteredEvent) {
            onDungeonEnteredEvent((DungeonEnteredEvent) event);
        } else if (event instanceof DamageTakenEvent) {
            onDamageTakenEvent((DamageTakenEvent) event);
        }
    }

    private void onDamageTakenEvent(DamageTakenEvent event) {
        printLine(MessageFormat.format("Player took {0} damage", event.getEventData().getDamage()));
    }

    private void onDungeonEnteredEvent(DungeonEnteredEvent event) {
        printLine("Player entered the dungeon");
    }
}
