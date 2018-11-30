package chronometry.patches;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.lang.reflect.*;

import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireEnum;
import com.evacipated.cardcrawl.modthespire.lib.ByRef;

import com.gikk.twirk.*;
import com.gikk.twirk.events.*;
import com.gikk.twirk.types.users.*;
import com.gikk.twirk.types.twitchMessage.*;

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
import com.megacrit.cardcrawl.screens.*;
import com.megacrit.cardcrawl.monsters.*;
import java.util.*;
import java.util.stream.*;
import java.util.function.*;
import org.apache.logging.log4j.*;
import de.robojumper.ststwitch.*;
import de.robojumper.ststwitch.TwitchConnection;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import chronometry.SlayTheStreamer;
import basemod.ReflectionHacks;

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
				List<String> screwYouList = new ArrayList(votedUsernames);
				Collections.shuffle(screwYouList);

				String username = screwYouList.get(0);

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
				votedUsernames.remove(screwYouList.get(0));
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
            float y = m.intentHb.cY - 60.0f;
            float x = m.hb.cX - m.animX;
            sb.setColor(Settings.CREAM_COLOR);
            // TextureAtlas.AtlasRegion img = ImageMaster.MOVE_NAME_BG;
            // sb.draw(img, x - img.packedWidth / 2.0f, y - img.packedHeight / 2.0f, img.packedWidth / 2.0f, img.packedHeight / 2.0f, img.packedWidth, img.packedHeight, Settings.scale, Settings.scale * 2.0f, 0.0f);
            FontHelper.renderFontCentered(sb, FontHelper.tipHeaderFont, m.name, x, y, Settings.CREAM_COLOR);
	    }
	}

    @SpirePatch(clz=AbstractMonster.class, method="refreshIntentHbLocation")
    public static class changeIntentHBPosition { 
	    public static void Postfix(AbstractMonster m) {
			m.intentHb.y = m.intentHb.y + 20.0F;
			m.intentHb.cY = m.intentHb.cY + 20.0F;
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