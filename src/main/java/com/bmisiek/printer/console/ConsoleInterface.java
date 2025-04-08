package com.bmisiek.printer.console;

import com.bmisiek.game.dungeon.Dungeon;
import com.bmisiek.game.dungeon.DungeonInterface;
import com.bmisiek.game.event.DamageTakenEvent;
import com.bmisiek.game.event.DungeonEnteredEvent;
import com.bmisiek.game.player.Player;
import com.bmisiek.printer.console.printers.DungeonPrinter;
import com.bmisiek.printer.contract.GameAction;
import com.bmisiek.printer.contract.GuiInterface;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;

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

    public GameAction Act(DungeonInterface dungeon, Player player) throws RuntimeException {
        try {
            var command = System.in.read();
        } catch(Exception e) {
            throw new RuntimeException(e);
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
