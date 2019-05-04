package chronometry;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

public class BossSelectRoom extends AbstractRoom {
  public boolean choseRelic = false;
  
  public BossSelectRoom()
  {
    // if ((AbstractDungeon.actNum < 4)) {
    //   this.phase = AbstractRoom.RoomPhase.COMPLETE;
    // } else {
    this.phase = AbstractRoom.RoomPhase.COMPLETE;
    // }
    this.mapImg = ImageMaster.MAP_NODE_TREASURE;
    this.mapImgOutline = ImageMaster.MAP_NODE_TREASURE_OUTLINE;
    this.monsters = new MonsterGroup(new AbstractMonster[0]);
    this.mapSymbol = "T";
  }
    
  public void onPlayerEntry()
  {
    CardCrawlGame.music.silenceBGM();
    this.phase = AbstractRoom.RoomPhase.COMPLETE;
    SlayTheStreamer.bossSelectScreen.open();
    playBGM("SHRINE");
  }
    
  public void update()
  {
    super.update();
    SlayTheStreamer.bossSelectScreen.update();
  }
  
  public AbstractCard.CardRarity getCardRarity(int roll)
  {
    return null;
  }
}
