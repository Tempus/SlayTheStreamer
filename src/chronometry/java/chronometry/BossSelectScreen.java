package chronometry;

import basemod.BaseMod;
import basemod.ReflectionHacks;
import chronometry.patches.BossChoicePatch;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.Skeleton;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.MonsterHelper;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.monsters.beyond.AwakenedOne;
import com.megacrit.cardcrawl.monsters.beyond.Donu;
import com.megacrit.cardcrawl.monsters.beyond.TimeEater;
import com.megacrit.cardcrawl.monsters.city.BronzeAutomaton;
import com.megacrit.cardcrawl.monsters.city.Champ;
import com.megacrit.cardcrawl.monsters.city.TheCollector;
import com.megacrit.cardcrawl.monsters.exordium.SlimeBoss;
import com.megacrit.cardcrawl.monsters.exordium.TheGuardian;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.BossChestShineEffect;
import com.megacrit.cardcrawl.vfx.GlowyFireEyesEffect;
import com.megacrit.cardcrawl.vfx.StaffFireEffect;
import de.robojumper.ststwitch.TwitchPanel;
import de.robojumper.ststwitch.TwitchVoteListener;
import de.robojumper.ststwitch.TwitchVoteOption;
import de.robojumper.ststwitch.TwitchVoter;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

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

        this.monsterX.add(964.0f * Settings.scale);
        this.monsterY.add(540.0f * Settings.scale);
        this.monsterX.add(804.0f * Settings.scale);
        this.monsterY.add(360.0f * Settings.scale);
        this.monsterX.add(1124.0f * Settings.scale);
        this.monsterY.add(360.0f * Settings.scale);

        this.smokeImg = ImageMaster.loadImage("versusImages/BossScreenOverlay.png");

        TwitchVoter.registerListener(new TwitchVoteListener() {
            @Override
            public void onTwitchAvailable() {
                chronometry.SlayTheStreamer.bossSelectScreen.updateVote();
            }
            
            @Override
            public void onTwitchUnavailable() {
                chronometry.SlayTheStreamer.bossSelectScreen.updateVote();
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
        //TODO: unhardcode
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
                return new chronometry.HexaghostModel();
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
                //This stuff is based on basemod patches, for how it adds custom bosses.
                BaseMod.BossInfo bossInfo = BaseMod.getBossInfo(bossID);
                if (bossInfo != null) {
                    MonsterGroup bossGroup = MonsterHelper.getEncounter(bossID);
                    if (bossGroup.monsters.size() == 1)
                    {
                        return bossGroup.monsters.get(0);
                    }

                    for (AbstractMonster mo : bossGroup.monsters)
                    {
                        if (mo.type == AbstractMonster.EnemyType.BOSS)
                        {
                            return mo;
                        }
                    }
                    //log .error("No bosses in encounter: " + bossID);
                    return bossGroup.monsters.get(0);
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
            chronometry.SlayTheStreamer.log("Twitch not active");
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
