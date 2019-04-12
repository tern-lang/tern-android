package org.ternlang.game.framework.gfx;

public interface AndroidGameFactory {
   void createGame(AndroidActivity activity, AndroidGameLauncher launcher, float width, float height, boolean isLandscape);
}
