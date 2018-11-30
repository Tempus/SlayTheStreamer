package chronometry.patches;

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
import com.megacrit.cardcrawl.screens.select.BossRelicSelectScreen;

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
import com.megacrit.cardcrawl.helpers.*;
import com.megacrit.cardcrawl.unlock.*;
import java.util.*;
import java.util.stream.*;
import java.util.function.*;
import org.apache.logging.log4j.*;
import de.robojumper.ststwitch.*;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import chronometry.SlayTheStreamer;
import chronometry.BossSelectRoom;
import basemod.ReflectionHacks;

public class NoSkipBossRelicPatch {

    static boolean isVoting = false;
    static boolean mayVote = false;

    @SpirePatch(clz=BossRelicSelectScreen.class, method="renderTwitchVotes")
    public static class RenderHook { 
        public static void Prefix(BossRelicSelectScreen self, SpriteBatch sb) {
            SlayTheStreamer.noSkip.RenderVote(sb);
            SpireReturn.Return(null);
        }
    }

    @SpirePatch(clz=BossRelicSelectScreen.class, method="updateVote")
    public static class updateHook { 
        public static void Replace(BossRelicSelectScreen self) {
            SlayTheStreamer.noSkip.updateVote();
        }
    }

    // This will create this class at the beginning of the game
    @SpirePatch(clz=BossRelicSelectScreen.class, method="open")
    public static class openHook { 
        public static void Postfix(BossRelicSelectScreen self, final ArrayList<AbstractRelic> chosenRelics) {
            AbstractDungeon.dynamicBanner.appear(800.0f * Settings.scale, "Choose the Worst Boss Relic");
            NoSkipBossRelicPatch.mayVote = true;
            SlayTheStreamer.noSkip.updateVote();
        }
    }

    public NoSkipBossRelicPatch() {
        TwitchVoter.registerListener(new TwitchVoteListener() {
            @Override
            public void onTwitchAvailable() {
                SlayTheStreamer.noSkip.updateVote();
            }
            
            @Override
            public void onTwitchUnavailable() {
                SlayTheStreamer.noSkip.updateVote();
            }
        });
    }

    public void RenderVote(final SpriteBatch sb) {
        sb.draw(SlayTheStreamer.startScreenImage, Settings.WIDTH / 2.0F, 0);
        if (this.isVoting == false) {
            return;
        }
        if (TwitchPanel.getDefaultVoter().isPresent()) {
            final TwitchVoter twitchVoter = TwitchPanel.getDefaultVoter().get();
            final TwitchVoteOption[] options = twitchVoter.getOptions();
            final int sum = Arrays.stream(options).map(c -> c.voteCount).reduce(0, Integer::sum);
            for (int i = 0; i < AbstractDungeon.bossRelicScreen.relics.size(); ++i) {
                String s = "#" + (i) + ": " + options[i].voteCount;
                if (sum > 0) {
                    s = s + " (" + options[i].voteCount * 100 / sum + "%)";
                }
                switch (i) {
                    case 0: {
                        FontHelper.renderFontCentered(sb, FontHelper.panelNameFont, s, 964.0f * Settings.scale, 700.0f * Settings.scale - 75.0f * Settings.scale, Color.WHITE.cpy());
                        break;
                    }
                    case 1: {
                        FontHelper.renderFontCentered(sb, FontHelper.panelNameFont, s, 844.0f * Settings.scale, 560.0f * Settings.scale - 75.0f * Settings.scale, Color.WHITE.cpy());
                        break;
                    }
                    case 2: {
                        FontHelper.renderFontCentered(sb, FontHelper.panelNameFont, s, 1084.0f * Settings.scale, 560.0f * Settings.scale - 75.0f * Settings.scale, Color.WHITE.cpy());
                        break;
                    }
                }
            }
            FontHelper.renderFontCentered(sb, FontHelper.panelNameFont, BossRelicSelectScreen.TEXT[4] + twitchVoter.getSecondsRemaining() + BossRelicSelectScreen.TEXT[5], Settings.WIDTH / 2.0f, 192.0f * Settings.scale, Color.WHITE.cpy());
        }
    }

    public void updateVote() {
        if (TwitchPanel.getDefaultVoter().isPresent()) {
            final TwitchVoter twitchVoter = TwitchPanel.getDefaultVoter().get();
            if (this.mayVote && twitchVoter.isVotingConnected() && !this.isVoting) {
                String[] relicList = new String[3];
                for (int i = 0; i < AbstractDungeon.bossRelicScreen.relics.size(); i++) {
                    relicList[i] = AbstractDungeon.bossRelicScreen.relics.get(i).toString();
                }

                this.isVoting = twitchVoter.initiateSimpleNumberVote(relicList, this::completeVoting);
            }
            else if (this.isVoting && (!this.mayVote || !twitchVoter.isVotingConnected())) {
                twitchVoter.endVoting(true);
                this.isVoting = false;
            }
        }
    }

    public void completeVoting(int option) {
        if (!this.isVoting) {
            return;
        }
        this.isVoting = false;
        if (AbstractDungeon.screen == AbstractDungeon.CurrentScreen.BOSS_REWARD) {
            if (TwitchPanel.getDefaultVoter().isPresent()) {
                final TwitchVoter twitchVoter = TwitchPanel.getDefaultVoter().get();
                AbstractDungeon.topPanel.twitch.ifPresent(twitchPanel -> twitchPanel.connection.sendMessage("Voting on relic ended... chose " + twitchVoter.getOptions()[option].displayName));
            }
            while (AbstractDungeon.screen != AbstractDungeon.CurrentScreen.BOSS_REWARD) {
                AbstractDungeon.closeCurrentScreen();
            }
            final AbstractRelic r = AbstractDungeon.bossRelicScreen.relics.get(option);
            if (!r.relicId.equals("Black Blood") && !r.relicId.equals("Ring of the Serpent")) {
                r.obtain();
            }
            r.isObtained = true;
        }

        this.mayVote = false;
        this.updateVote();
    }


    /// This is the old code


    // @SpirePatch(clz=BossRelicSelectScreen.class, method="renderTwitchVotes")
    // public static class renderTwitchVotes { 
    //     public static void Replace(BossRelicSelectScreen self, final SpriteBatch sb) {
    //         if ((boolean)ReflectionHacks.getPrivate(self, BossRelicSelectScreen.class, "isVoting") == false) {
    //             return;
    //         }
    //         if (TwitchPanel.getDefaultVoter().isPresent()) {
    //             final TwitchVoter twitchVoter = TwitchPanel.getDefaultVoter().get();
    //             final TwitchVoteOption[] options = twitchVoter.getOptions();
    //             final int sum = Arrays.stream(options).map(c -> c.voteCount).reduce(0, Integer::sum);
    //             for (int i = 0; i < self.relics.size(); ++i) {
    //                 String s = "#" + (i) + ": " + options[i].voteCount;
    //                 if (sum > 0) {
    //                     s = s + " (" + options[i].voteCount * 100 / sum + "%)";
    //                 }
    //                 switch (i) {
    //                     case 0: {
    //                         FontHelper.renderFontCentered(sb, FontHelper.panelNameFont, s, 964.0f * Settings.scale, 700.0f * Settings.scale - 75.0f * Settings.scale, Color.WHITE.cpy());
    //                         break;
    //                     }
    //                     case 1: {
    //                         FontHelper.renderFontCentered(sb, FontHelper.panelNameFont, s, 844.0f * Settings.scale, 560.0f * Settings.scale - 75.0f * Settings.scale, Color.WHITE.cpy());
    //                         break;
    //                     }
    //                     case 2: {
    //                         FontHelper.renderFontCentered(sb, FontHelper.panelNameFont, s, 1084.0f * Settings.scale, 560.0f * Settings.scale - 75.0f * Settings.scale, Color.WHITE.cpy());
    //                         break;
    //                     }
    //                 }
    //             }
    //             FontHelper.renderFontCentered(sb, FontHelper.panelNameFont, BossRelicSelectScreen.TEXT[4] + twitchVoter.getSecondsRemaining() + BossRelicSelectScreen.TEXT[5], Settings.WIDTH / 2.0f, 192.0f * Settings.scale, Color.WHITE.cpy());
    //         }
    //         sb.draw(SlayTheStreamer.startScreenImage, Settings.WIDTH / 2.0F, 0);
    //     }
    // }

    // @SpirePatch(clz=BossRelicSelectScreen.class, method="updateVote")
    // public static class updateVote { 
    //     public static void Replace(BossRelicSelectScreen self) {
    //         if (TwitchPanel.getDefaultVoter().isPresent()) {
    //             final TwitchVoter twitchVoter = TwitchPanel.getDefaultVoter().get();
    //             if ((boolean)ReflectionHacks.getPrivate(self, BossRelicSelectScreen.class, "mayVote") && twitchVoter.isVotingConnected() && !(boolean)ReflectionHacks.getPrivate(self, BossRelicSelectScreen.class, "isVoting")) {
    //                 String[] relicList = new String[3];
    //                 for (int i = 0; i < self.relics.size(); i++) {
    //                     relicList[i] = self.relics.get(i).toString();
    //                 }

    //                 boolean result = twitchVoter.initiateSimpleNumberVote(relicList, NoSkipBossRelicPatch::completeVoting);
    //                 ReflectionHacks.setPrivate(self, BossRelicSelectScreen.class, "isVoting", result);
    //             }
    //             else if ((boolean)ReflectionHacks.getPrivate(self, BossRelicSelectScreen.class, "isVoting") && (!(boolean)ReflectionHacks.getPrivate(self, BossRelicSelectScreen.class, "mayVote") || !twitchVoter.isVotingConnected())) {
    //                 twitchVoter.endVoting(true);
    //                 ReflectionHacks.setPrivate(self, BossRelicSelectScreen.class, "isVoting", false);
    //             }
    //         }
    //     }
    // }

    // public static void completeVoting(int option) {
    //     BossRelicSelectScreen self = AbstractDungeon.bossRelicScreen;
    //     if (!(boolean)ReflectionHacks.getPrivate(self, BossRelicSelectScreen.class, "isVoting")) {
    //         return;
    //     }
    //     ReflectionHacks.setPrivate(self, BossRelicSelectScreen.class, "isVoting", false);
    //     if (TwitchPanel.getDefaultVoter().isPresent()) {
    //         final TwitchVoter twitchVoter = TwitchPanel.getDefaultVoter().get();
    //         AbstractDungeon.topPanel.twitch.ifPresent(twitchPanel -> twitchPanel.connection.sendMessage("Voting on relic ended... chose " + twitchVoter.getOptions()[option].displayName));
    //     }
    //     while (AbstractDungeon.screen != AbstractDungeon.CurrentScreen.BOSS_REWARD) {
    //         AbstractDungeon.closeCurrentScreen();
    //     }
    //     final AbstractRelic r = self.relics.get(option);
    //     if (!r.relicId.equals("Black Blood") && !r.relicId.equals("Ring of the Serpent")) {
    //         r.obtain();
    //     }
    //     r.isObtained = true;
    // }
}
