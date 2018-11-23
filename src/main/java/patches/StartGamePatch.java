package chronometry.patches;

import java.util.*;
import java.lang.reflect.*;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.neow.NeowEvent;
import com.megacrit.cardcrawl.neow.NeowReward;
import com.megacrit.cardcrawl.neow.NeowRoom;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.map.DungeonMap;
import de.robojumper.ststwitch.*;

import com.megacrit.cardcrawl.events.RoomEventDialog;

import chronometry.SlayTheStreamer;
import basemod.ReflectionHacks;

public class StartGamePatch {
	public static boolean mayVote = false;
	public static boolean isVoting = false;
	public static String[] neowOptions = new String[4];
	public static int option;

	// Choose your cards
	@SpirePatch(clz=NeowEvent.class, method="blessing")
	public static class SetDeckBlessing { 
		public static SpireReturn Prefix(NeowEvent self) {
			if (!SlayTheStreamer.config.getBool("VoteOnNeow")) {
				StartGamePatch.chooseCards(self); 
				return SpireReturn.Return(null);
			}
			return SpireReturn.Continue();
		}

		public static void Postfix(NeowEvent self) {
			if (SlayTheStreamer.config.getBool("VoteOnNeow")) {
		        TwitchVoter.registerListener(new TwitchVoteListener() {
		            @Override
		            public void onTwitchAvailable() {
		                StartGamePatch.updateVote();
		            }
		            
		            @Override
		            public void onTwitchUnavailable() {
		                StartGamePatch.updateVote();
		            }
		        });
				StartGamePatch.mayVote = true;

				for (int i = 0; i < 4; i++) {
					StartGamePatch.neowOptions[i] = ((ArrayList<NeowReward>)ReflectionHacks.getPrivate(self, NeowEvent.class, "rewards")).get(i).optionLabel.replace("#g", "").replace("#r", "").replace("[ ", 
					"").replace(" ]", "");
				}
				StartGamePatch.updateVote();
			}
		}
	}

	@SpirePatch(clz=NeowRoom.class, method="renderAboveTopPanel")
	public static class RenderNeowVotes { 
		public static void Postfix(NeowRoom self, SpriteBatch sb) {
			StartGamePatch.renderTwitchVotes(sb);
		}
	}

    // Twitch Voting Stuff goes here            
    
    public static Optional<TwitchVoter> getVoter() {
        return TwitchPanel.getDefaultVoter();
    }

    public static void updateVote() {
        if (StartGamePatch.getVoter().isPresent()) {
            final TwitchVoter twitchVoter = StartGamePatch.getVoter().get();
            if (StartGamePatch.mayVote && twitchVoter.isVotingConnected() && !StartGamePatch.isVoting) {

                StartGamePatch.isVoting = twitchVoter.initiateSimpleNumberVote(StartGamePatch.neowOptions, StartGamePatch::completeVoting);
            }
            else if (StartGamePatch.isVoting && (!StartGamePatch.mayVote || !twitchVoter.isVotingConnected())) {
                twitchVoter.endVoting(true);
                StartGamePatch.isVoting = false;
            }
        }
    }

    public static void completeVoting(final int option) {
        if (!StartGamePatch.isVoting) {
            return;
        }
        StartGamePatch.isVoting = false;
        if (StartGamePatch.getVoter().isPresent()) {
            final TwitchVoter twitchVoter = StartGamePatch.getVoter().get();
            AbstractDungeon.topPanel.twitch.ifPresent(twitchPanel -> twitchPanel.connection.sendMessage("Voting on Neow's bonus ended... chose " + twitchVoter.getOptions()[option].displayName));
        }

        // DO THE THING

        // Save the chosen option
        StartGamePatch.option = option;

        // Clear off the visible text
        try {
            Method m = NeowEvent.class.getDeclaredMethod("dismissBubble");
            m.setAccessible(true);
            m.invoke(AbstractDungeon.getCurrRoom().event);
        }
        catch (Throwable e) {}
        ((NeowEvent)AbstractDungeon.getCurrRoom().event).roomEventText.clearRemainingOptions();

        // Construct your deck
        StartGamePatch.chooseCards((NeowEvent)AbstractDungeon.getCurrRoom().event);

        // this settles our options.

        // // Private functions are dumb
        // try {
        //     Method m = NeowEvent.class.getDeclaredMethod("dismissBubble", Integer.class);
        //     m.setAccessible(true);
        //     m.invoke(AbstractDungeon.getCurrRoom().event, option);
        // }
        // catch (Throwable e) {}
    }

	@SpirePatch(clz=NeowEvent.class, method="update")
	public static class WaitForDeckConstruction { 
		public static void Prefix(NeowEvent self) {
			if (SlayTheStreamer.config.getBool("VoteOnNeow")) {
				if (!AbstractDungeon.gridSelectScreen.confirmScreenUp && AbstractDungeon.gridSelectScreen.isJustForConfirming) {
					RoomEventDialog.waitForInput = false;
					RoomEventDialog.selectedOption = StartGamePatch.option;
				}
			}
		}
	}

    public static void renderTwitchVotes(final SpriteBatch sb) {
        if (!StartGamePatch.isVoting) {
            return;
        }
        if (StartGamePatch.getVoter().isPresent()) {
            final TwitchVoter twitchVoter = StartGamePatch.getVoter().get();
            final TwitchVoteOption[] options = twitchVoter.getOptions();
            final int sum = Arrays.stream(options).map(c -> c.voteCount).reduce(0, Integer::sum);
            for (int i = 0; i < 4; ++i) {
                String s = "#" + (i) + ": " + options[i].voteCount;
                if (sum > 0) {
                    s = s + " (" + options[i].voteCount * 100 / sum + "%)";
                }

                float y = (Settings.OPTION_Y - 500.0F * Settings.scale);
      			y += i * -82.0F;
      			y -= 4 * -82.0F;
      			// y += 18.5F;

                FontHelper.renderFontRightAligned(sb, FontHelper.panelEndTurnFont, s, 160.0F * Settings.scale, y, Color.WHITE.cpy());
            }
            FontHelper.renderFontCentered(sb, FontHelper.panelNameFont, "VOTE NOW: " + twitchVoter.getSecondsRemaining() + "s left.", 340.0F, 77.0F + 82.0F * 4 * Settings.scale, Color.WHITE.cpy());
        }
    }

	// Deck Construction Code

	static void chooseCards(NeowEvent self) {
		ReflectionHacks.setPrivate(self, NeowEvent.class, "pickCard", true);

		// Don't forget remove all cards in deck
		AbstractDungeon.player.masterDeck.group.clear();

		CardGroup sealedGroup = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
		
		// Guaranteed Rares
		for (int rares = 0; rares < SlayTheStreamer.config.getInt("GuaranteedRares"); rares++) {
			AbstractCard card = AbstractDungeon.getCard(AbstractCard.CardRarity.RARE);
			sealedGroup.addToBottom(card.makeCopy());
		}

		// Guaranteed Rares
		for (int uncommons = 0; uncommons < SlayTheStreamer.config.getInt("GuaranteedUncommons"); uncommons++) {
			AbstractCard card = AbstractDungeon.getCard(AbstractCard.CardRarity.UNCOMMON);
			sealedGroup.addToBottom(card.makeCopy());
		}

		// Guaranteed Rares
		for (int commons = 0; commons < SlayTheStreamer.config.getInt("GuaranteedCommons"); commons++) {
			AbstractCard card = AbstractDungeon.getCard(AbstractCard.CardRarity.COMMON);
			sealedGroup.addToBottom(card.makeCopy());
		}

		// Unguaranteed cards
		if (sealedGroup.size() < SlayTheStreamer.config.getInt("CardPickPool")) {
			int size = sealedGroup.size();
			for (int i = 0; i < SlayTheStreamer.config.getInt("CardPickPool")-size; i++)
			{
				AbstractCard card = AbstractDungeon.getCard(AbstractDungeon.rollRarity());
				if (!sealedGroup.contains(card)) {
					sealedGroup.addToBottom(card.makeCopy());
				} else { i--; SlayTheStreamer.log("WUTTTTTTTTTTTTTTTTTTTTTTTTTTTTT"); }
			}
		}

		// Make sure all the cards are visible
		for (AbstractCard c : sealedGroup.group) {
		  UnlockTracker.markCardAsSeen(c.cardID);
		}

		// Open the choice dialog
		AbstractDungeon.gridSelectScreen.open(sealedGroup, SlayTheStreamer.config.getInt("CardPickChoices"), "Choose " + SlayTheStreamer.config.getInt("CardPickChoices") + " cards.", false);
	}
}
