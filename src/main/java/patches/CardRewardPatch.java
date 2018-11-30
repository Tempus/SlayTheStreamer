package chronometry.patches;

import java.util.ArrayList;

import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireEnum;
import com.evacipated.cardcrawl.modthespire.lib.ByRef;

import com.megacrit.cardcrawl.localization.*;
import com.megacrit.cardcrawl.relics.*;
import com.megacrit.cardcrawl.blights.*;
import com.megacrit.cardcrawl.screens.mainMenu.*;
import com.badlogic.gdx.*;
import com.megacrit.cardcrawl.dungeons.*;
import com.badlogic.gdx.math.*;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.helpers.controller.*;
import com.megacrit.cardcrawl.core.*;
import com.megacrit.cardcrawl.rooms.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.megacrit.cardcrawl.vfx.*;
import com.badlogic.gdx.graphics.*;
import com.megacrit.cardcrawl.rewards.*;
import com.megacrit.cardcrawl.cards.*;
import com.megacrit.cardcrawl.screens.*;
import java.util.*;
import java.util.stream.*;
import java.util.function.*;
import org.apache.logging.log4j.*;
import de.robojumper.ststwitch.*;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import chronometry.SlayTheStreamer;
import basemod.ReflectionHacks;

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