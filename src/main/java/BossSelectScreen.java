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
import chronometry.BossChoicePatch;
import chronometry.HexaghostModel;
import basemod.ReflectionHacks;

public class BossSelectScreen {

    public boolean isDone;

    public Texture smokeImg;
    public float shineTimer = 0.0f;
    public static final float SHINE_INTERAL = 0.1f;
    public static ArrayList<Float> monsterX = new ArrayList();
    public static ArrayList<Float> monsterY = new ArrayList();
    public float fireTimer = 0.0F;

    boolean isVoting;
    boolean mayVote;
    
    protected ArrayList<AbstractMonster> bosses = new ArrayList();
    boolean reopened = false;

    // Constructor
    public BossSelectScreen() {
        this.isDone = false;
        this.isVoting = false;
        this.mayVote = false;

        this.monsterX.add(964.0f);
        this.monsterY.add(540.0f);
        this.monsterX.add(804.0f);
        this.monsterY.add(360.0f);
        this.monsterX.add(1124.0f);
        this.monsterY.add(360.0f);

        this.smokeImg = ImageMaster.loadImage("versusImages/BossScreenOverlay.png");

        TwitchVoter.registerListener(new TwitchVoteListener() {
            @Override
            public void onTwitchAvailable() {
                SlayTheStreamer.bossSelectScreen.updateVote();
            }
            
            @Override
            public void onTwitchUnavailable() {
                SlayTheStreamer.bossSelectScreen.updateVote();
            }
        });
    }
    
    public void update() {
        // Special effect
        this.shineTimer -= Gdx.graphics.getDeltaTime();
        if (this.shineTimer < 0.0f && !Settings.DISABLE_EFFECTS) {
            this.shineTimer = 0.1f;
            AbstractDungeon.topLevelEffects.add(new BossChestShineEffect());
            AbstractDungeon.topLevelEffects.add(new BossChestShineEffect(MathUtils.random(0.0f, Settings.WIDTH), MathUtils.random(0.0f, Settings.HEIGHT - 128.0f * Settings.scale)));
        }

        if (this.reopened) { return; }

        for (AbstractMonster m : this.bosses) {
            if (m.name == "Hexaghost") {
                m.update();
            }
            if (m.id == "TheCollector") {
              this.fireTimer -= Gdx.graphics.getDeltaTime();
              Skeleton skeleton = (Skeleton)ReflectionHacks.getPrivate(m, AbstractCreature.class, "skeleton");

              if (this.fireTimer < 0.0F)
              {
                this.fireTimer = 0.07F;
                GlowyFireEyesEffect left = new GlowyFireEyesEffect(skeleton
                
                  .getX() + skeleton.findBone("lefteyefireslot").getX(), skeleton
                  .getY() + skeleton.findBone("lefteyefireslot").getY() + 70.0F * Settings.scale);

                GlowyFireEyesEffect right = new GlowyFireEyesEffect(skeleton
                
                  .getX() + skeleton.findBone("righteyefireslot").getX(), skeleton
                  .getY() + skeleton.findBone("righteyefireslot").getY() + 70.0F * Settings.scale);

                float leftScale = ((float)ReflectionHacks.getPrivate(left, AbstractGameEffect.class, "scale")) / 2.0F;
                float rightScale = ((float)ReflectionHacks.getPrivate(right, AbstractGameEffect.class, "scale")) / 2.0F;

                ReflectionHacks.setPrivate(left, AbstractGameEffect.class, "scale", leftScale);
                ReflectionHacks.setPrivate(right, AbstractGameEffect.class, "scale", rightScale);

                AbstractDungeon.topLevelEffects.add(left);
                AbstractDungeon.topLevelEffects.add(right);
                
                AbstractDungeon.topLevelEffects.add(new StaffFireEffect(skeleton
                
                  .getX() + skeleton.findBone("fireslot").getX() - 60.0F * Settings.scale, skeleton
                  .getY() + skeleton.findBone("fireslot").getY() + 195.0F * Settings.scale));
              }
            }
        }
    }
        
    public void open() {
        // Clean up
        this.refresh();
        this.bosses.clear();
        Settings.hideCombatElements = true;

        // Display
        AbstractDungeon.dynamicBanner.appear(800.0f * Settings.scale, "Choose the Most Deadly");
        AbstractDungeon.isScreenUp = true;
        AbstractDungeon.screen = BossChoicePatch.BOSS_SELECT;
        AbstractDungeon.overlayMenu.proceedButton.hide();
        AbstractDungeon.overlayMenu.showBlackScreen();

        // Spawn the display images
        for (int i = 0; i < 3; i++) {
            AbstractMonster m = getBoss(AbstractDungeon.bossList.get(i));
            m.drawX = this.monsterX.get(i);
            m.drawY = this.monsterY.get(i)-42.0F * Settings.scale;
            this.bosses.add(m);
        }

        // Update Voting
        this.mayVote = true;
        this.updateVote();
    }
    
    public void reopen() {
        this.reopened = true;
    }

    public AbstractMonster getBoss(String bossID) {
        AbstractMonster m;
        switch (bossID) {

            case "Hexaghost":
                return new HexaghostModel();
            case "Slime Boss":
                m = new SlimeBoss();
                // m.animY += 50.0F;
                return m;
            case "The Guardian":
                m = new TheGuardian();
                m.animY -= 50.0F;
                return m;

            case "Champ":
                return new Champ();
            case "Collector":
                return new TheCollector();
            case "Automaton":
                return new BronzeAutomaton();

            case "Awakened One":
                return new AwakenedOne(0.0F, 0.0F);
            case "Time Eater":
                return new TimeEater();
            case "Donu and Deca":
                return new Donu();
            
            default: //Probably a modded boss.
                try { //This stuff is based on basemod patches, for how it adds custom bosses.
			        BaseMod.BossInfo bossInfo = BaseMod.getBossInfo(bossID);
			        if (bossInfo != null) {
                        MonsterGroup bossGroup = MonsterHelper.getEncounter(bossKey);
                        
                        for (AbstractMonster m : bossGroup)
                        {
                            if (m.type == AbstractMonster.EnemyType.BOSS)
                            {
                                return m;
                            }
                        }
                        //log .error("No bosses in encounter: " + bossID);
			        }
                    else
                    {
                    //log .error("Failed to find boss: " + bossID);
                    }
                } catch (IllegalAccessException | InstantiationException e) {
                    //log .error("Failed to instantiate boss: " + bossID);
                }
        }
        return null;
    }

    public void refresh() {
        this.isDone = false;
        this.shineTimer = 0.0f;
    }
    
    public void hide() {
        AbstractDungeon.dynamicBanner.hide();
    }
    
    //Render Code

    public void render(final SpriteBatch sb) {
        update();

        for (final AbstractGameEffect e : AbstractDungeon.effectList) {
            e.render(sb);
        }
        // AbstractDungeon.player.render(sb);
        sb.setColor(Color.WHITE);
        sb.draw(this.smokeImg, 470.0f * Settings.scale, AbstractDungeon.floorY - 258.0f * Settings.scale, this.smokeImg.getWidth() * Settings.scale, this.smokeImg.getHeight() * Settings.scale);

        for (AbstractMonster m : this.bosses) {
            m.render(sb);
        }

        if (AbstractDungeon.topPanel.twitch.isPresent()) {
            this.renderTwitchVotes(sb);
        }

        // For whomever next looks at this code, I just want you to know
        //
        //      You are beautiful to me
        //
    }
    
    public void renderTwitchVotes(final SpriteBatch sb) {
        if (!this.isVoting) {
            SlayTheStreamer.log("Twitch not active");
            return;
        }
        if (this.getVoter().isPresent()) {
            final TwitchVoter twitchVoter = this.getVoter().get();
            final TwitchVoteOption[] options = twitchVoter.getOptions();
            final int sum = Arrays.stream(options).map(c -> c.voteCount).reduce(0, Integer::sum);
            for (int i = 0; i < 3; ++i) {
                String s = "#" + (i) + ": " + options[i].voteCount;
                if (sum > 0) {
                    s = s + " (" + options[i].voteCount * 100 / sum + "%)";
                }
                switch (i) {
                    case 0: {
                        FontHelper.renderFontCentered(sb, FontHelper.panelNameFont, s, this.monsterX.get(0), this.monsterY.get(0) - 75.0f * Settings.scale, Color.WHITE.cpy());
                        break;
                    }
                    case 1: {
                        FontHelper.renderFontCentered(sb, FontHelper.panelNameFont, s, this.monsterX.get(1), this.monsterY.get(1) - 75.0f * Settings.scale, Color.WHITE.cpy());
                        break;
                    }
                    case 2: {
                        FontHelper.renderFontCentered(sb, FontHelper.panelNameFont, s, this.monsterX.get(2), this.monsterY.get(2) - 75.0f * Settings.scale, Color.WHITE.cpy());
                        break;
                    }
                }
            }
            FontHelper.renderFontCentered(sb, FontHelper.panelNameFont, "VOTE NOW: " + twitchVoter.getSecondsRemaining() + "s left.", Settings.WIDTH / 2.0f, 192.0f * Settings.scale, Color.WHITE.cpy());
        }
        sb.draw(SlayTheStreamer.startScreenImage, Settings.WIDTH / 2.0F, 0);
    }

    // Twitch Voting Stuff goes here            
    
    public Optional<TwitchVoter> getVoter() {
        return TwitchPanel.getDefaultVoter();
    }
    
    public void updateVote() {
        if (this.getVoter().isPresent()) {
            final TwitchVoter twitchVoter = this.getVoter().get();
            if (this.mayVote && twitchVoter.isVotingConnected() && !this.isVoting) {
                String[] array = new String[AbstractDungeon.bossList.size()];
                array = AbstractDungeon.bossList.toArray(array);

                this.isVoting = twitchVoter.initiateSimpleNumberVote(array, this::completeVoting);
            }
            else if (this.isVoting && (!this.mayVote || !twitchVoter.isVotingConnected())) {
                twitchVoter.endVoting(true);
                this.isVoting = false;
            }
        }
    }
    
    public void completeVoting(final int option) {
        if (!this.isVoting) {
            return;
        }
        this.isVoting = false;
        if (this.getVoter().isPresent()) {
            final TwitchVoter twitchVoter = this.getVoter().get();
            AbstractDungeon.topPanel.twitch.ifPresent(twitchPanel -> twitchPanel.connection.sendMessage("Voting on boss ended... chose " + twitchVoter.getOptions()[option].displayName));
        }

        // Make the changes to the dungeon
        AbstractDungeon.bossKey = AbstractDungeon.bossList.get(option);
        SlayTheStreamer.bossHidden = false;
        AbstractDungeon.closeCurrentScreen();
        AbstractDungeon.dungeonMapScreen.open(false);
        Settings.hideCombatElements = false;
        this.bosses.clear();

        // Private functions are dumb
        try {
            Method m = AbstractDungeon.class.getDeclaredMethod("setBoss", String.class);
            m.setAccessible(true);
            m.invoke(CardCrawlGame.dungeon, AbstractDungeon.bossKey);
        }
        catch (Throwable e) {}
    }
}
