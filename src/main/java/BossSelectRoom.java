package chronometry;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.audio.MusicMaster;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardRarity;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.OverlayMenu;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.localization.LocalizedStrings;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.rewards.chests.AbstractChest;
import com.megacrit.cardcrawl.rewards.chests.BossChest;
import com.megacrit.cardcrawl.ui.buttons.ProceedButton;
import com.megacrit.cardcrawl.vfx.scene.SpookierChestEffect;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import java.util.ArrayList;

import chronometry.SlayTheStreamer;

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
