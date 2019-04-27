package chronometry;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.MathHelper;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.BobEffect;

public class HexaghostModel extends AbstractMonster {
  public static final String ID = "Hexaghost";
  public static final String IMAGE = "images/monsters/theBottom/boss/ghost/core.png";
  
  private Texture plasma1;
  private Texture plasma2;
  private Texture plasma3;
  private Texture shadow;
  private Texture core;
  private float plasma1Angle = 0.0F;
  private float plasma2Angle = 0.0F;
  private float plasma3Angle = 0.0F;

  public float rotationSpeed = 5.0F;
  public float targetRotationSpeed = 120.0F;
  private BobEffect effect = new BobEffect(2.5F * Settings.scale, 0.5F);

  public HexaghostModel()
  {
    super("Hexaghost", "Hexaghost", 250, 20.0F, 0.0F, 450.0F, 450.0F, "images/monsters/theBottom/boss/ghost/core.png");
    this.type = AbstractMonster.EnemyType.BOSS;

    this.plasma1 = ImageMaster.loadImage("images/monsters/theBottom/boss/ghost/plasma1.png");
    this.plasma2 = ImageMaster.loadImage("images/monsters/theBottom/boss/ghost/plasma2.png");
    this.plasma3 = ImageMaster.loadImage("images/monsters/theBottom/boss/ghost/plasma3.png");
    this.shadow = ImageMaster.loadImage("images/monsters/theBottom/boss/ghost/shadow.png");
    this.core = ImageMaster.loadImage("images/monsters/theBottom/boss/ghost/core.png");
    // createOrbs();

    this.animY -= 400.0F;
  }
    
  // private void createOrbs()
  // {
  //   this.orbs.add(new HexaghostOrb(-90.0F, 380.0F, this.orbs.size()));
  //   this.orbs.add(new HexaghostOrb(90.0F, 380.0F, this.orbs.size()));
  //   this.orbs.add(new HexaghostOrb(160.0F, 250.0F, this.orbs.size()));
  //   this.orbs.add(new HexaghostOrb(90.0F, 120.0F, this.orbs.size()));
  //   this.orbs.add(new HexaghostOrb(-90.0F, 120.0F, this.orbs.size()));
  //   this.orbs.add(new HexaghostOrb(-160.0F, 250.0F, this.orbs.size()));
  // }
  
  public void takeTurn() {}
  
  protected void getMove(int num) {}
    
  public void die() {}
  
  public void update()
  {
    super.update();

    this.effect.update();
    this.plasma1Angle += this.rotationSpeed * Gdx.graphics.getDeltaTime();
    this.plasma2Angle += this.rotationSpeed / 2.0F * Gdx.graphics.getDeltaTime();
    this.plasma3Angle += this.rotationSpeed / 3.0F * Gdx.graphics.getDeltaTime();
    
    this.rotationSpeed = MathHelper.fadeLerpSnap(this.rotationSpeed, this.targetRotationSpeed);
    this.effect.speed = (this.rotationSpeed * Gdx.graphics.getDeltaTime());

    // this.particleTimer -= Gdx.graphics.getDeltaTime();
    // if (this.particleTimer < 0.0F)
    // {
    //   AbstractDungeon.effectList.add(new GhostlyFireEffect(this.x + oX + this.effect.y * 2.0F, this.y + oY + this.effect.y * 2.0F));
      
    //   this.particleTimer = 0.06F;
    // }

    // for (HexaghostOrb orb : this.orbs) {
    //   orb.update(this.drawX + this.animX, this.drawY + this.animY);
    // }
  }
  
  public void render(SpriteBatch sb)
  {
    // Render Body
    sb.setColor(this.tint.color);
    sb.draw(this.plasma3,
            this.drawX - 256.0F + this.animX + 12.0F * Settings.scale,
            this.drawY + this.animY + AbstractDungeon.sceneOffsetY + this.effect.y * 2.0F + 256.0F* Settings.scale,
            256.0F, 256.0F, 512.0F, 512.0F,
            Settings.scale * 0.475F, Settings.scale * 0.475F,
            this.plasma3Angle, 0, 0, 512, 512, false, false);
    sb.draw(this.plasma2,
            this.drawX - 256.0F + this.animX + 6.0F * Settings.scale,
            this.drawY + this.animY + AbstractDungeon.sceneOffsetY + this.effect.y + 256.0F* Settings.scale,
            256.0F, 256.0F, 512.0F, 512.0F,
            Settings.scale*0.5F, Settings.scale*0.5F,
            this.plasma2Angle, 0, 0, 512, 512, false, false);
    sb.draw(this.plasma1,
            this.drawX - 256.0F + this.animX,
            this.drawY + this.animY + AbstractDungeon.sceneOffsetY + this.effect.y * 0.5F + 256.0F* Settings.scale,
            256.0F, 256.0F, 512.0F, 512.0F,
            Settings.scale*0.5F, Settings.scale*0.5F,
            this.plasma1Angle, 0, 0, 512, 512, false, false);
    sb.draw(this.shadow,
            this.drawX - 256.0F + this.animX + 12.0F * Settings.scale,
            this.drawY + this.animY + AbstractDungeon.sceneOffsetY + this.effect.y / 4.0F - 15.0F * Settings.scale + 256.0F* Settings.scale,
            256.0F, 256.0F, 512.0F, 512.0F,
            Settings.scale*0.5F, Settings.scale*0.5F,
            0.0F, 0, 0, 512, 512, false, false);

    // Render Core
    sb.draw(this.core,
     this.drawX - 256.0F + this.animX * Settings.scale, 
     this.drawY + this.animY + AbstractDungeon.sceneOffsetY + this.effect.y * 2.0F + 256.0F * Settings.scale, 
     256.0F, 256.0F, 512.0F, 512.0F,
     Settings.scale*0.5F, Settings.scale*0.5F, 
     0.0F, 
     0, 0, 
     this.img.getWidth(), this.img.getHeight(), 
     this.flipHorizontal, this.flipVertical);
  }
}
