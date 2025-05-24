package com.shatteredpixel.shatteredpixeldungeon.windows;

import com.shatteredpixel.shatteredpixeldungeon.GamesInProgress;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.StartScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.TitleScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.watabou.utils.FileUtils;

import java.util.ArrayList;

public class WndRestoreGame extends Window {

    private static final int WIDTH = 120;
    private static final int HEIGHT = 20;
    private static final int MARGIN = 2;

    public WndRestoreGame() {
        super();

        ArrayList<String> deletedGames = FileUtils.filesInDir("DeletedGame");
        if (deletedGames.isEmpty()) {
            RenderedTextBlock text = PixelScene.renderTextBlock("No deleted saves found", 6);
            text.maxWidth(WIDTH - MARGIN * 2);
            add(text);
            resize(WIDTH, (int) text.height() + MARGIN * 2);
            return;
        }

        float pos = MARGIN;
        for (String gameFolder : deletedGames) {
            String gamePath = "DeletedGame/" + gameFolder;
            if (FileUtils.dirExists(gamePath)) {
                // Create game info button
                RedButton gameBtn = new RedButton(gameFolder) {
                    @Override
                    protected void onClick() {
                        hide();
                        ShatteredPixelDungeon.scene().add(new WndOptions(
                                "Restore Game",
                                "What would you like to do with this save?",
                                "Restore",
                                "Delete Permanently",
                                "Cancel") {
                            @Override
                            protected void onSelect(int index) {
                                if (index == 0) {
                                    // Restore
                                    restoreGame(gameFolder);
                                } else if (index == 1) {
                                    // Delete permanently
                                    deleteGame(gameFolder);
                                }
                            }
                        });
                    }
                };
                gameBtn.setRect(MARGIN, pos, WIDTH - MARGIN * 2, HEIGHT);
                add(gameBtn);
                pos += HEIGHT + MARGIN;
            }
        }

        resize(WIDTH, (int) pos);
    }

    private void restoreGame(String gameFolder) {
        String srcDir = "DeletedGame/" + gameFolder;
        String destDir = gameFolder;

        // Move the game folder back to its original location
        if (FileUtils.moveDir(srcDir, destDir)) {
            // Refresh the game list
            ShatteredPixelDungeon.switchNoFade(TitleScene.class);
        }
    }

    private void deleteGame(String gameFolder) {
        String gamePath = "DeletedGame/" + gameFolder;
        if (FileUtils.deleteDir(gamePath)) {
            // Refresh the window
            ShatteredPixelDungeon.scene().add(new WndRestoreGame());
        }
    }
}