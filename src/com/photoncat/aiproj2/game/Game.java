package com.photoncat.aiproj2.game;

import com.photoncat.aiproj2.interfaces.Board;
import com.photoncat.aiproj2.io.Adapter;

/**
 * This is the gaming thread. Each game is handled in a separate thread to support functions of polling.
 */
public class Game extends Thread{
    private static final int POLLING_INTERVAL_MILLISECONDS = 5000;
    private Adapter ioAdapter;
    private int gameId;

    /**
     * Host a game with another team.
     */
    public Game(Adapter ioAdapter, int otherTeamId, int boardSize, int target) {
        this.ioAdapter = ioAdapter;
        gameId = ioAdapter.createGame(otherTeamId, boardSize, target);
    }

    /**
     * Join another game.
     */
    public Game(Adapter ioAdapter, int gameId) {
        this.ioAdapter = ioAdapter;
        this.gameId = gameId;
    }

    @Override
    public void run() {
        Board board = ioAdapter.getBoard(gameId);
        while (board != null && !board.gameover()) {
            // TODO: Decide where to move.
            int x = 0;
            int y = 0;
            ioAdapter.moveAt(gameId, x, y);
            while (ioAdapter.getLastMove(gameId) == Board.PieceType.CROSS) {
                // Wait for 5 seconds - As Professor Arora suggested in slack.
                try {
                    Thread.sleep(POLLING_INTERVAL_MILLISECONDS);
                } catch (InterruptedException e) {
                    // Ignore the interrupted since:
                    // a) There won't be any interruption.
                    // b) Even if there were any, we can simply recheck the status.
                    e.printStackTrace();
                }
            }
            do {
                board = ioAdapter.getBoard(gameId);
            } while (board == null);
        }
    }
}
