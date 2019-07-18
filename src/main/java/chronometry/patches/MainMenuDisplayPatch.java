package chronometry.patches;

import chronometry.SlayTheStreamer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.screens.mainMenu.MainMenuScreen;
import de.robojumper.ststwitch.TwitchConfig;
import basemod.ReflectionHacks;

public class MainMenuDisplayPatch {

	@SpirePatch(cls = "com.megacrit.cardcrawl.screens.mainMenu.MainMenuScreen", method="render")
	public static class render {
	
		@SpireInsertPatch( rloc = 3 )
		public static void Insert(MainMenuScreen __instance, SpriteBatch sb)
		{
			Color white = new Color(1.0F, 1.0F, 1.0F, 1.0F);

			if (AbstractDungeon.topPanel.twitch.isPresent()) {
				TwitchConfig t = AbstractDungeon.topPanel.twitch.get().connection.getTwitchConfig();
				String username = (String)ReflectionHacks.getPrivate(t, TwitchConfig.class, "username");
			    FontHelper.renderFontCentered(sb, FontHelper.bannerFont, CardCrawlGame.languagePack.getUIString("versus:ForPlayer").TEXT[6] + username, Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F - 196.0F * Settings.scale, white);
			    // panelNameFont - tiny, but well balanced
			    // bannerNameFont  - Big, pretty spiffy looking

			    sb.draw(SlayTheStreamer.startScreenImage,
						Settings.WIDTH / 2.0F, 0,
						SlayTheStreamer.startScreenImage.getWidth() * Settings.scale,
						SlayTheStreamer.startScreenImage.getHeight() * Settings.scale);
			}
		}
	}
}