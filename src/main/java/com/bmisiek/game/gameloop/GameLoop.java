package com.bmisiek.game.gameloop;

import com.bmisiek.game.dungeon.DungeonManagerInterface;
import com.bmisiek.game.dungeon.generator.DungeonGenerator;
import com.bmisiek.game.event.DungeonEmptyEvent;
import com.bmisiek.game.exception.InvalidActionException;
import com.bmisiek.game.player.Player;
import com.bmisiek.game.player.PlayerFactory;
import com.bmisiek.game.player.PlayerManager;
import com.bmisiek.printer.contract.GameAction;
import com.bmisiek.printer.contract.GuiInterface;
import com.bmisiek.printer.contract.actions.InventoryAction;
import com.bmisiek.printer.contract.actions.MoveAction;
import com.bmisiek.printer.contract.actions.SearchAction;
import com.bmisiek.printer.contract.actions.UseItemAction;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@Service
public class GameLoop implements ApplicationListener<DungeonEmptyEvent> {
    private final GuiInterface gui;

    @Getter
    private final DungeonManagerInterface dungeonManager;

    @Getter
    private final PlayerManager playerManager;

    @Getter
    private final Player player;

    private boolean isCancelled = false;

    public GameLoop(
            GuiInterface gui,
            DungeonManagerInterface dungeonManager,
            DungeonGenerator generator,
            PlayerFactory playerFactory,
            PlayerManager playerManager
    ) {
        this.gui = gui;
        this.playerManager = playerManager;
        this.player = playerFactory.createPlayer();

        dungeonManager.setDungeon(generator.createDungeon());
        this.dungeonManager = dungeonManager;
    }

    public void run() {
        dungeonManager.enter(player);

        while(!isCancelled) {
            this.gui.Update(dungeonManager);
            GameAction action;

            do {
                action = this.gui.GetAction(dungeonManager, player);
                handleAction(action);
            } while (!action.consumesTurn() && !isCancelled);
        }
    }

    private final Map<Class<?>, Consumer<GameAction>> actionHandlers = new HashMap<>() {{
        put(MoveAction.class, action -> {
            MoveAction moveAction = (MoveAction) action;
            getDungeonManager().tryMove(moveAction.getMovement());
        });
        put(SearchAction.class, _ -> getDungeonManager().searchRoom());
        put(UseItemAction.class, action -> {
            UseItemAction useItemAction = (UseItemAction) action;
            String result = getPlayerManager().useItem(getPlayer(), useItemAction.getItemIndex());
            System.out.println(result);
        });
        put(InventoryAction.class, _ -> {});
    }};

    private void handleAction(@NotNull GameAction action) {
        try {
            Consumer<GameAction> handler = actionHandlers.get(action.getClass());
            if (handler != null) {
                handler.accept(action);
            }
        } catch (InvalidActionException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void onApplicationEvent(@NotNull DungeonEmptyEvent event) {
        this.isCancelled = true;
    }
}
