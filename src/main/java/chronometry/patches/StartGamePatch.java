package chronometry.patches;

import basemod.ReflectionHacks;
import chronometry.SlayTheStreamer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.curses.AscendersBane;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.RoomEventDialog;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.neow.NeowEvent;
import com.megacrit.cardcrawl.neow.NeowReward;
import com.megacrit.cardcrawl.neow.NeowRoom;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import de.robojumper.ststwitch.TwitchPanel;
import de.robojumper.ststwitch.TwitchVoteOption;
import de.robojumper.ststwitch.TwitchVoter;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

// Attempt 1
// 10:01:51.513 INFO neow.NeowEvent> 0
// 10:01:51.514 INFO neow.NeowEvent> BLESSING
// 10:01:51.514 INFO neow.NeowEvent> COUNTER: 0
// OUT PRIVMSG #chronometrics :VOTE NOW: #0: Remove a Card from your deck; #1: Obtain 100 Gold; #2: Lose all Gold, Remove 2 Cards; #3: Lose your starting Relic Obtain a random boss Relic
// 10:01:53.227 INFO audio.MusicMaster> Properly faded out MENU
// 10:02:04.567 INFO core.CardCrawlGame> PAUSE()
// OUT PRIVMSG #chronometrics :Voting on Neow's bonus ended... chose Remove a Card from your deck
// 10:02:31.337 INFO neow.NeowReward> [ERROR]   Neow Reward Drawback: NONE
// 10:02:41.805 INFO helpers.CardHelper> Obtained Pummel (UNCOMMON). You have 2 now
// 10:02:41.805 INFO unlock.UnlockTracker> Already seen: Pummel


// Attmept 2
// 10:03:01.210 INFO neow.NeowEvent> 0
// 10:03:01.211 INFO neow.NeowEvent> BLESSING
// 10:03:01.211 INFO neow.NeowEvent> COUNTER: 0
// OUT PRIVMSG #chronometrics :VOTE NOW: #0: Upgrade a Card; #1: Max HP +7; #2: Lose 7 Max HP, Obtain a random rare Relic; #3: Lose your starting Relic Obtain a random boss Relic
// 10:03:01.228 INFO neow.NeowReward> [ERROR] Missing Neow Reward Drawback: NONE




public class StartGamePatch {
	public static boolean mayVote = false;
	public static boolean isVoting = false;
	public static String[] neowOptions;
	public static int option;

	// Reset Variables
	// @SpirePatch(clz=NeowEvent.class, method=SpirePatch.CONSTRUCTOR, paramtypez={boolean.class})
	// public static class ResetVariables { 
	// 	public static void Prefix(NeowEvent self, @ByRef boolean[] isDone) {
	// 		StartGamePatch.mayVote = false;
	// 		StartGamePatch.isVoting = false;
	// 		StartGamePatch.neowOptions = new String[4];
	// 		ReflectionHacks.setPrivate(self, NeowEvent.class, "pickCard", false);
	// 		// isDone[0]= false;
	// 	}
	// }

   //  	NeowEvent n = (NeowEvent)AbstractDungeon.getCurrRoom().event;
   //  	if ((Integer)ReflectionHacks.getPrivate(n, NeowEvent.class, "screenNum") == 99) {
   //  		StartGamePatch.mayVote = false;
   //          final TwitchVoter twitchVoter = StartGamePatch.getVoter().get();
	// twitchVoter.endVoting(true);
   //          StartGamePatch.isVoting = false;
	  //       StartGamePatch.chooseCards((NeowEvent)AbstractDungeon.getCurrRoom().event);
   //          return;
   //  	}

	// Choose your cards
	@SpirePatch(clz=NeowEvent.class, method="blessing")
	public static class SetDeckBlessing { 
		public static SpireReturn Prefix(NeowEvent self) {
			// If we aren't voting on Neow, just choose the cards right away, thanks.
			if (!SlayTheStreamer.config.getBool("VoteOnNeow")) {
				StartGamePatch.chooseCards(self); 
				return SpireReturn.Return(null);
			}
			return SpireReturn.Continue();
		}

		public static void Postfix(NeowEvent self) {
			if (SlayTheStreamer.config.getBool("VoteOnNeow")) {
				// Starts voting and populates and formats the options for chat message.
				StartGamePatch.mayVote = true;

				ArrayList<NeowReward> neowRewards = (ArrayList<NeowReward>)ReflectionHacks.getPrivate(self, NeowEvent.class, "rewards");
				StartGamePatch.neowOptions = new String[neowRewards.size()];
				int i = 0;

				for (NeowReward nr : neowRewards)
                {
                    String voteOption = nr.optionLabel;

                    if (voteOption.contains("#r"))
                    {
                        voteOption = voteOption.replaceFirst(" #g", ", ")
						.replace("#g", "")
						.replace("#r", "")
						.replace("[ ", "")
						.replace(" ]", "")
						.replace("]", "") //hi reina
						.replace("[", "")
						.replace(".,", ",");
                    }
                    else
                    {
                        voteOption = voteOption.replace("#g", "")
						.replace("#r", "")
						.replace("[ ", "")
						.replace(" ]", "");
                    }

                    StartGamePatch.neowOptions[i] = voteOption;
                    i++;
                }

//				for (int i = 0; i < 4; i++) {
//					if (i >= 2) {
//						StartGamePatch.neowOptions[i] = ((ArrayList<NeowReward>)ReflectionHacks.getPrivate(self, NeowEvent.class, "rewards")).get(i).optionLabel
//						.replaceFirst(" #g", ", ")
//						.replace("#g", "")
//						.replace("#r", "")
//						.replace("[ ", "")
//						.replace(" ]", "")
//						.replace("]", "") //hi reina
//						.replace("[", "")
//						.replace(".,", ",");
//					} else {
//						StartGamePatch.neowOptions[i] = ((ArrayList<NeowReward>)ReflectionHacks.getPrivate(self, NeowEvent.class, "rewards")).get(i).optionLabel
//						.replace("#g", "")
//						.replace("#r", "")
//						.replace("[ ", "")
//						.replace(" ]", "");
//					}
//				}
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
            AbstractDungeon.topPanel.twitch.ifPresent(twitchPanel -> twitchPanel.connection.sendMessage(CardCrawlGame.languagePack.getUIString("versus:ForPlayer").TEXT[5] + twitchVoter.getOptions()[option].displayName));
        }

        // Occurs when the vote runs out of time.

        // Save the chosen option
        SlayTheStreamer.log("Voting Ended");
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
    }

	@SpirePatch(clz=NeowEvent.class, method="update")
	public static class WaitForDeckConstruction { 
		public static void Prefix(NeowEvent self) {
			// Resets the voting variables upon completion of the room
			if (AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMPLETE) {
				StartGamePatch.mayVote = false;
				StartGamePatch.updateVote();
			}

			if (SlayTheStreamer.config.getBool("VoteOnNeow")) {
				// Controls pressing a button after the cards have been selected... probably...? I don't think this is activating when I want it to.
				if (!StartGamePatch.isVoting && 
					!AbstractDungeon.gridSelectScreen.confirmScreenUp && AbstractDungeon.gridSelectScreen.isJustForConfirming &&
					// !AbstractDungeon.isScreenUp &&
					((Integer)ReflectionHacks.getPrivate(self, NeowEvent.class, "screenNum")) == 3) {
					SlayTheStreamer.log("ADVANCING OPTION - ACTIVATING NEW REWARDS ~~~~~~~~~~~~~~~~~~~~~~~~");


					RoomEventDialog.waitForInput = false;
					RoomEventDialog.selectedOption = StartGamePatch.option;
					ReflectionHacks.setPrivate(self, NeowEvent.class, "pickCard", false);
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
      			y += i * -82.0F * Settings.scale;
      			y -= 4 * -82.0F * Settings.scale;
      			// y += 18.5F;

                FontHelper.renderFontRightAligned(sb, FontHelper.panelEndTurnFont, s, 160.0F * Settings.scale, y, Color.WHITE.cpy());
            }
            FontHelper.renderFontCentered(sb, FontHelper.panelNameFont, CardCrawlGame.languagePack.getUIString("versus:ForPlayer").TEXT[2]
																			+ twitchVoter.getSecondsRemaining() + CardCrawlGame.languagePack.getUIString("versus:ForPlayer").TEXT[3],
					340.0F * Settings.scale, 77.0F * Settings.scale + 82.0F * 4 * Settings.scale, Color.WHITE.cpy());
        }
    }

	// Deck Construction Code

	static void chooseCards(NeowEvent self) {
		ReflectionHacks.setPrivate(self, NeowEvent.class, "pickCard", true);

		// Don't forget remove all cards in deck
		AbstractDungeon.player.masterDeck.group.removeIf(c -> !c.cardID.equals(AscendersBane.ID));

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
		AbstractDungeon.gridSelectScreen.open(sealedGroup, SlayTheStreamer.config.getInt("CardPickChoices"),
				CardCrawlGame.languagePack.getUIString("versus:ForPlayer").TEXT[9] +
						SlayTheStreamer.config.getInt("CardPickChoices") +
						CardCrawlGame.languagePack.getUIString("versus:ForPlayer").TEXT[10],
				false);
	}
}
