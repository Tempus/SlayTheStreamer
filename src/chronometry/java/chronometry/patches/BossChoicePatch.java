package chronometry.patches;

import chronometry.BossSelectRoom;
import chronometry.SlayTheStreamer;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.dungeons.TheEnding;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.map.DungeonMap;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.TreasureRoom;
import com.megacrit.cardcrawl.ui.buttons.ProceedButton;

public class BossChoicePatch {
	// Change Boss Image on Map when hidden
	@SpirePatch(clz=AbstractDungeon.class, method="setBoss")
	public static class HideBoss { 
		public static SpireReturn Prefix(AbstractDungeon self, String key) {
			if (SlayTheStreamer.config.getBool("VoteOnBosses")) {
				if (SlayTheStreamer.bossHidden) {
	    			AbstractDungeon.bossKey = key;
	    	      	DungeonMap.boss = ImageMaster.loadImage("versusImages/unknownBoss.png");
		      		DungeonMap.bossOutline = ImageMaster.loadImage("versusImages/unknownBossOutline.png");
					return SpireReturn.Return(null);
		      	} 
		    }
	      	return SpireReturn.Continue();
   		}
	}

	// Rehide the boss after each act... except act 4
   	@SpirePatch(clz=ProceedButton.class, method="goToNextDungeon")
	public static class HideBossAtDungeonStart { 
		public static void Postfix(ProceedButton self, AbstractRoom room) {
			if (SlayTheStreamer.config.getBool("VoteOnBosses")) {
				if (Settings.isEndless || !AbstractDungeon.id.equals(TheEnding.ID)) {
			        SlayTheStreamer.bossHidden = true;
		      	}
		    }
	    }
	}

	// We're making the bosses tinier, awwww, so cute
	@SpirePatch(clz=AbstractCreature.class, method="loadAnimation")
	public static class MakeBossesCute { 
		public static void Prefix(AbstractCreature self, String atlasUrl, String skeletonUrl, @ByRef float[] scale) {
			if (AbstractDungeon.screen == BossChoicePatch.BOSS_SELECT) {
				scale[0] = scale[0] * 2.0F;
	      	} 
   		}
	}

	//TODO: Brave undead...
//	@SpirePatch(clz = AbstractMonster.class, method = SpirePatch.CONSTRUCTOR, paramtypez = {String.class, String.class, Integer.class, Float.class, Float.class, Float.class, Float.class, String.class, Float.class, Float.class, Boolean.class})
//	public static class MakeStaticImageBossesCute {
//		public static void Postfix(AbstractMonster __instance, String name, String id, int maxHealth, float hb_x, float hb_y, float hb_w, float hb_h, String imgUrl, float offsetX, float offsetY, boolean ignoreBlights)
//		{
//			if (AbstractDungeon.screen == BossChoicePatch.BOSS_SELECT && imgUrl != null)
//			{
//				Field img = __instance.getClass().getDeclaredField("img");
//				img.setAccessible(true);
//
//			}
//		}
//	}

	// Swap to boss choice after the treasure room
	@SpirePatch(clz=ProceedButton.class, method="update")
	public static class ChangeBossRoom { 
		@SpireInsertPatch( rloc = 22 )
		public static SpireReturn Insert(ProceedButton self) {
			if (SlayTheStreamer.config.getBool("VoteOnBosses")) {
				SlayTheStreamer.log("~~~~~~~ Activating Code " + AbstractDungeon.currMapNode.getRoomSymbol(true));
	            if ((AbstractDungeon.currMapNode.getRoomSymbol(true) == "T") && (AbstractDungeon.getCurrRoom() instanceof TreasureRoom))
	            {
					SlayTheStreamer.log("~~~~~~~ We're a treasure room");
				    CardCrawlGame.music.fadeOutTempBGM();
				    AbstractDungeon.currMapNode.setRoom(new BossSelectRoom());
				    AbstractDungeon.nextRoom = AbstractDungeon.currMapNode;
				    AbstractDungeon.closeCurrentScreen();
				    AbstractDungeon.nextRoomTransitionStart();
				    self.hide();
					return SpireReturn.Return(null);
	            }
	        }
	      	return SpireReturn.Continue();
		}
	}

	// Add in a custom Enum for the CurrentScreen to adjust render time
    @SpireEnum
    public static AbstractDungeon.CurrentScreen BOSS_SELECT;

   	@SpirePatch(clz=AbstractDungeon.class, method="render")
	public static class RenderBossSelect {
		@SpireInsertPatch( rloc = 133 )
		public static void Insert(AbstractDungeon self, SpriteBatch sb) {
			if (self.screen == BossChoicePatch.BOSS_SELECT) {
    			SlayTheStreamer.bossSelectScreen.render(sb);
	      	}
	    }
	}
}
