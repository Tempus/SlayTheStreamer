package chronometry.patches;

import chronometry.SlayTheStreamer;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.screens.CardRewardScreen;

import java.util.ArrayList;

public class CardRewardPatch {
    @SpirePatch(clz=CardRewardScreen.class, method="renderTwitchVotes")
    public static class renderTwitchVotes { 
        public static void Postfix(CardRewardScreen self, final SpriteBatch sb) {
            sb.draw(SlayTheStreamer.startScreenImage, Settings.WIDTH / 2.0F, 0);
        }
    }

    @SpirePatch(clz=CardRewardScreen.class, method="open")
    public static class openHook { 
        public static void Postfix(CardRewardScreen self, final ArrayList<AbstractCard> cards, final RewardItem rItem, final String header) {
            AbstractDungeon.dynamicBanner.appear("Choose the Worst Card");
        }
    }
}