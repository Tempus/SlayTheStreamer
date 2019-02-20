package chronometry;

import com.megacrit.cardcrawl.localization.*;
import com.megacrit.cardcrawl.monsters.exordium.*;
import com.megacrit.cardcrawl.monsters.city.*;
import com.megacrit.cardcrawl.monsters.beyond.*;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.screens.mainMenu.*;
import com.megacrit.cardcrawl.dungeons.*;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.helpers.controller.*;
import com.megacrit.cardcrawl.core.*;
import com.megacrit.cardcrawl.actions.animations.*;
import com.megacrit.cardcrawl.rooms.*;
import com.megacrit.cardcrawl.vfx.*;
import com.megacrit.cardcrawl.helpers.*;
import com.megacrit.cardcrawl.unlock.*;
import com.megacrit.cardcrawl.vfx.GlowyFireEyesEffect;
import com.megacrit.cardcrawl.vfx.StaffFireEffect;
import com.megacrit.cardcrawl.vfx.combat.InflameEffect;

import com.badlogic.gdx.*;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.*;
import com.esotericsoftware.spine.Skeleton;

import java.util.*;
import java.util.stream.*;
import java.util.function.*;
import java.lang.reflect.*;
import org.apache.logging.log4j.*;
import de.robojumper.ststwitch.*;

import chronometry.SlayTheStreamer;
import basemod.ReflectionHacks;

public class MonsterMessageRepeater {

	static int MsgLength = 64;

	static void parseMessage(String msg, String user) {
		if (CardCrawlGame.isInARun()) {
			if (AbstractDungeon.getCurrRoom() != null) {
				if (AbstractDungeon.getCurrRoom().monsters != null) {
					for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
						if (m.isDying) { return; }
						String displayName = SlayTheStreamer.displayNames.get(user).split(" ")[0];
						if (m.name.split(" ")[0].toLowerCase().equals(displayName.toLowerCase())) {
							msg = msg.substring(0, Math.min(msg.length(), MonsterMessageRepeater.MsgLength));
							if (msg.length() == MonsterMessageRepeater.MsgLength) {
								msg = msg.substring(0, msg.lastIndexOf(" ")) + "...";
							}
							AbstractDungeon.actionManager.addToBottom(new TalkAction(m,msg,1.5F,2.5F));
						}
					}
				}
			}
		}
	}

}
