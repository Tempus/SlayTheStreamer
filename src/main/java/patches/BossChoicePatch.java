package chronometry;

import java.util.ArrayList;

import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireEnum;
import com.evacipated.cardcrawl.modthespire.lib.ByRef;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.neow.NeowEvent;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.map.DungeonMap;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.TreasureRoom;
import com.megacrit.cardcrawl.ui.buttons.ProceedButton;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.controller.CInputAction;
import com.megacrit.cardcrawl.helpers.controller.CInputActionSet;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import chronometry.SlayTheStreamer;
import chronometry.BossSelectRoom;
import basemod.ReflectionHacks;

public class BossChoicePatch {
	// Change Boss Image on Map when hidden
	@SpirePatch(clz=AbstractDungeon.class, method="setBoss")
	public static class HideBoss { 
		public static SpireReturn Prefix(AbstractDungeon self, String key) {
			if (SlayTheStreamer.config.getBool("VoteOnBosses")) {
				if (SlayTheStreamer.bossHidden == true) {
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
				if (AbstractDungeon.actNum != 4) {
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
