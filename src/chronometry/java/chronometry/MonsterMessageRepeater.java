package chronometry;

import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.SpeechBubble;

public class MonsterMessageRepeater {

	static int MsgLength = 64;

	static void parseMessage(String msg, String user) {
		if (CardCrawlGame.isInARun()) {
			if (AbstractDungeon.getCurrRoom() != null) {
				if (AbstractDungeon.getCurrRoom().monsters != null) {
					for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
						if (m.isDying) { return; }
						String username = user;
						if (chronometry.SlayTheStreamer.displayNames.containsKey(username)) {
							username = chronometry.SlayTheStreamer.displayNames.get(username);
						}
						if (m.name.split(" ")[0].toLowerCase().equals(username.split(" ")[0].toLowerCase())) {
							msg = msg.substring(0, Math.min(msg.length(), MonsterMessageRepeater.MsgLength));
							if (msg.length() == MonsterMessageRepeater.MsgLength) {
								msg = msg.substring(0, msg.lastIndexOf(" ")) + "...";
							}
							AbstractDungeon.effectList.add(new SpeechBubble(m.hb.cX + m.dialogX, m.hb.cY + m.dialogY, 5.0F, msg, false));
						}
					}
				}
			}
		}
	}

}
