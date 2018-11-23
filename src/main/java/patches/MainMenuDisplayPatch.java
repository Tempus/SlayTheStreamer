package chronometry.patches;

import java.util.ArrayList;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;

import com.megacrit.cardcrawl.screens.mainMenu.MainMenuScreen;
import com.megacrit.cardcrawl.ui.panels.TopPanel;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.VictoryRoom;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.screens.stats.CharStat;
import com.megacrit.cardcrawl.screens.DeathScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import de.robojumper.ststwitch.*;

import chronometry.SlayTheStreamer;

public class MainMenuDisplayPatch {

	@SpirePatch(cls = "com.megacrit.cardcrawl.screens.mainMenu.MainMenuScreen", method="render")
	public static class render {
	
		@SpireInsertPatch( rloc = 3 )
		public static void Insert(MainMenuScreen __instance, SpriteBatch sb)
		{
			Color white = new Color(1.0F, 1.0F, 1.0F, 1.0F);

			if (AbstractDungeon.topPanel.twitch.isPresent()) {
				TwitchConfig t = AbstractDungeon.topPanel.twitch.get().connection.getTwitchConfig();
				String username = t.getUsername();
			    FontHelper.renderFontCentered(sb, FontHelper.bannerFont, "Chat vs. " + username, Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F - 196.0F * Settings.scale, white);
			    // panelNameFont - tiny, but well balanced
			    // bannerNameFont  - Big, pretty spiffy looking

			    sb.draw(SlayTheStreamer.startScreenImage, Settings.WIDTH / 2.0F, 0);
			}
		}
	}
}