package chronometry.patches;

import basemod.ReflectionHacks;
import chronometry.SlayTheStreamer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.gikk.twirk.Twirk;
import com.gikk.twirk.types.users.TwitchUser;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import de.robojumper.ststwitch.TwitchPanel;
import de.robojumper.ststwitch.TwitchVoter;

import java.util.*;
import java.util.regex.Pattern;

import static com.badlogic.gdx.math.MathUtils.random;

public class MonsterNamesPatch {

	public static final Pattern QUOTE_PATTERN = Pattern.compile("(?i)\"(.*)\"(?:.*?)-?- ?joinrbs(?:[ \\.,].*)?");

    @SpirePatch(clz=AbstractMonster.class,
    	method=SpirePatch.CONSTRUCTOR,
    	paramtypez={
    		String.class,
    		String.class,
    		int.class,
    		float.class,
    		float.class,
    		float.class,
    		float.class,
    		String.class,
    		float.class,
    		float.class,
    		boolean.class
    	})
    public static class changeMonsterNames {
        public static void Postfix(AbstractMonster self, final String name, final String id, final int maxHealth, final float hb_x, final float hb_y, final float hb_w, final float hb_h, final String imgUrl, final float offsetX, final float offsetY, final boolean ignoreBlights) {
        	// Get the right name
            final TwitchVoter twitchVoter = TwitchPanel.getDefaultVoter().get();
			Set<String> votedUsernames = (Set<String>)ReflectionHacks.getPrivate(twitchVoter, TwitchVoter.class, "votedUsernames");

			if (votedUsernames.size() > 0) {

				/* ¡éBetter Randomness¡é */
				List<String> screwYouList = new ArrayList(votedUsernames);
				Map<String, Double> weightedMap = new HashMap(); // <username, weight> pool contains who voted right before
				double totalWeight = 0.0d;

				for(String e: screwYouList){
					String tarName = e;
					if (SlayTheStreamer.displayNames.containsKey(tarName)) {
						tarName = SlayTheStreamer.displayNames.get(tarName);
					}

					int chosenTimes;
					if (SlayTheStreamer.usedNames.containsKey(tarName)) {
						chosenTimes = SlayTheStreamer.usedNames.get(tarName);
					} else {
						chosenTimes = 0;
					}

					// Weight: pow(<voted> + 15, 1.05) / pow(<used> + 5, 2.5)

					if(SlayTheStreamer.votedTimes.containsKey(tarName)){
						SlayTheStreamer.votedTimes.put(tarName, SlayTheStreamer.votedTimes.get(tarName) + 1);
						weightedMap.put(e, Math.pow((double)(SlayTheStreamer.votedTimes.get(tarName)+15),1.05d)/Math.pow((double)(chosenTimes + 5),2.5d));
						totalWeight = totalWeight + weightedMap.get(e);
					}
					else{ // not voted before
						SlayTheStreamer.votedTimes.put(tarName, 1);
						weightedMap.put(e, Math.pow((double)(SlayTheStreamer.votedTimes.get(tarName)+15),1.05d)/Math.pow((double)(chosenTimes + 5),2.5d));
						totalWeight = totalWeight + weightedMap.get(e);
					}
					SlayTheStreamer.log("Name " + tarName + ", Voted " + SlayTheStreamer.votedTimes.get(tarName) + " time(s), " +
							"Chosed " + chosenTimes + " time(s), " + "weight is " + weightedMap.get(e));
				}

				String username = null;
				double randomVal = random.nextDouble() * totalWeight;

				for(String e: weightedMap.keySet()){
					randomVal -= weightedMap.get(e);
					if(randomVal <= 0.0d){
						username = e;
						break;
					}
				}

				String usernameOrigin = username;

				/* ¡èBetter Randomness¡è */

				if (SlayTheStreamer.displayNames.containsKey(username)) {
					username = SlayTheStreamer.displayNames.get(username);
				}

				// If they've been seen before they get a suffix
				if (SlayTheStreamer.usedNames.containsKey(username)) {
					SlayTheStreamer.usedNames.put(username, SlayTheStreamer.usedNames.get(username)+1);

					if (SlayTheStreamer.usedNames.get(username) == 2) {
						username = username + " Jr.";
					} else {
						username = username + " " + MonsterNamesPatch.IntegerToRomanNumeral(SlayTheStreamer.usedNames.get(username));
					}

				} else {
					SlayTheStreamer.usedNames.put(username, 1);
				}

				//.substring(0, 1).toUpperCase() + username.substring(1);
				self.name = username;
				votedUsernames.remove(usernameOrigin);
			}
        }
    }


    @SpirePatch(clz=Twirk.class, method="incommingMessage")
    public static class storeTwitchNames {
    	@SpireInsertPatch( rloc = 33, localvars={"user"} )
        public static void Insert(Twirk self, String line, TwitchUser user) {
        	SlayTheStreamer.displayNames.put(user.getUserName(), user.getDisplayName());
        }
    }

    @SpirePatch(clz=AbstractMonster.class, method="renderName")
    public static class renderMonsterNames {
	    public static void Replace(AbstractMonster m, final SpriteBatch sb) {
	    	if (AbstractDungeon.screen == BossChoicePatch.BOSS_SELECT) { return; }

            float y = m.intentHb.cY - 56.0f;
            float x = m.hb.cX - m.animX;
            Color c = Settings.CREAM_COLOR;

			if (m.isDying) {
	            c = m.tint.color;
				return;
			}

            sb.setColor(Settings.CREAM_COLOR);
            FontHelper.renderFontCentered(sb, FontHelper.tipHeaderFont, m.name, x, y, Settings.CREAM_COLOR);

            Random nameIndicer = new Random(m.name.hashCode());
            String titles[] = SlayTheStreamer.config.getString("MonsterTitles").split(",");
			String title = titles[(nameIndicer.nextInt(titles.length))];

            FontHelper.renderFontCentered(sb, FontHelper.powerAmountFont, "the " + title, x, y - 20.0F * Settings.scale, Settings.CREAM_COLOR);
	    }
	}

    @SpirePatch(clz=AbstractMonster.class, method="refreshIntentHbLocation")
    public static class changeIntentHBPosition {
	    public static void Postfix(AbstractMonster m) {
			m.intentHb.y = m.intentHb.y + 42.0F;
			m.intentHb.cY = m.intentHb.cY + 42.0F;
	    }
	}

    // @SpirePatch(clz=TwitchConnection.class, method="getMessageListener")
    // public static class renderTwitchVotes {
    //     public static TwirkListener Replace(TwitchConnection self, Twirk twirk, Object gameLock, final TwitchConnection conn) {

		  //   return new TwirkListenerBaseImpl()
		  //   {
		  //       public void onDisconnect() {}

	   //          public void onPrivMsg(final TwitchUser sender, final TwitchMessage message) {
	   //              final String content = message.getContent().trim();
	   //              final String senderName = sender.getDisplayName();
	   //              if (content.length() > 1) {
    //                     final Matcher m = MonsterNamesPatch.QUOTE_PATTERN.matcher(content);
    //                     try {
    //                     	Class<?> stupidPrivateClass = Class.forName("de.robojumper.ststwitch.TwitchConnection$MessageUserPair");
			 //            	Constructor<?> ctor = stupidPrivateClass.getDeclaredConstructor(TwitchConnection.class, String.class, String.class);
			 //            	ctor.setAccessible(true);

			 //            	Object o = ReflectionHacks.getPrivate(conn, TwitchConnection.class, "inMessages");

	   //                      Method add = o.getClass().getDeclaredMethod("add", stupidPrivateClass);
	   //                      add.invoke(o, ctor.newInstance(self, content, senderName));
			 //            } catch (Exception e) {
			 //            	e.printStackTrace();
			 //            }
	   //              }
	   //          }
		  //   };

    //     }
    // }

    // Nature is beautiful, this specimen found on StackOverflow
	public static String IntegerToRomanNumeral(int input) {
	    if (input < 1 || input > 3999)
	        return "Invalid Roman Number Value";
	    String s = "";
	    while (input >= 1000) {
	        s += "M";
	        input -= 1000;        }
	    while (input >= 900) {
	        s += "CM";
	        input -= 900;
	    }
	    while (input >= 500) {
	        s += "D";
	        input -= 500;
	    }
	    while (input >= 400) {
	        s += "CD";
	        input -= 400;
	    }
	    while (input >= 100) {
	        s += "C";
	        input -= 100;
	    }
	    while (input >= 90) {
	        s += "XC";
	        input -= 90;
	    }
	    while (input >= 50) {
	        s += "L";
	        input -= 50;
	    }
	    while (input >= 40) {
	        s += "XL";
	        input -= 40;
	    }
	    while (input >= 10) {
	        s += "X";
	        input -= 10;
	    }
	    while (input >= 9) {
	        s += "IX";
	        input -= 9;
	    }
	    while (input >= 5) {
	        s += "V";
	        input -= 5;
	    }
	    while (input >= 4) {
	        s += "IV";
	        input -= 4;
	    }
	    while (input >= 1) {
	        s += "I";
	        input -= 1;
	    }
	    return s;
	}
}
