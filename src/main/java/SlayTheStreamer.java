package chronometry;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer.PlayerClass;
import com.megacrit.cardcrawl.core.EnergyManager;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;

import basemod.BaseMod;
import basemod.interfaces.*;

import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;

import chronometry.patches.*;
import chronometry.ConfigPanel;
import chronometry.BossSelectScreen;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SpireInitializer
public class SlayTheStreamer implements PostInitializeSubscriber, StartGameSubscriber, PostDungeonInitializeSubscriber {

    public static final Logger logger = LogManager.getLogger(SlayTheStreamer.class.getName());

    private static final String MOD_NAME = "Slay the Streamer";
    private static final String AUTHOR = "Chronometrics";
    private static final String DESCRIPTION = "Chat vs Streamer in the ultimate showdown! The streamer begins with a winnable deck, and chat tries to find ways to ruin it by voting and influencing the run throughout the stream. Requires Twitch Integration to work.";

    public static SpireConfig config;
    public static boolean bossHidden = false;
    public static BossSelectScreen bossSelectScreen;

    public static Texture startScreenImage;

    @SuppressWarnings("deprecation")
    public SlayTheStreamer() {
        BaseMod.subscribe(this);
    }

    public static void log(String s) {
        logger.info(s);
    }

    public static void initialize() {
        @SuppressWarnings("unused")
        SlayTheStreamer slayTheStreamer = new SlayTheStreamer();
    }   

    public void receivePostInitialize() {
        bossSelectScreen = new BossSelectScreen();
        startScreenImage = ImageMaster.loadImage("versusImages/FacesOfEvil.png");

        try {
            config = new SpireConfig("SlayTheStreamer", "config");
            setDefaultPrefs();
        } catch (Exception e) {
            logger.info("Could not save config");
            logger.error(e.toString());
        }

        // Guarantee a whale
        Settings.isTestingNeow = true;

        Texture badgeTexture = ImageMaster.loadImage("versusImages/Badge.png");
        BaseMod.registerModBadge(badgeTexture, MOD_NAME, AUTHOR, DESCRIPTION, new ConfigPanel());   
    }

    public void receivePostDungeonInitialize() {
        if (config.getBool("VoteOnBosses")) {
            this.bossHidden = true;
        }
    }

    public void setDefaultPrefs() {
        if (!config.has("CardPickPool"))        { config.setInt("CardPickPool", 30); }
        if (!config.has("CardPickChoices"))     { config.setInt("CardPickChoices", 10); }

        if (!config.has("GuaranteedRares"))     { config.setInt("GuaranteedRares", 2); }
        if (!config.has("GuaranteedUncommons")) { config.setInt("GuaranteedUncommons", 5); }
        if (!config.has("GuaranteedCommons"))   { config.setInt("GuaranteedCommons", 10); }

        if (!config.has("VoteOnBosses"))        { config.setBool("VoteOnBosses", true); }
        if (!config.has("VoteOnNeow"))          { config.setBool("VoteOnNeow", true); }

        try {
            config.save();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void receiveStartGame() {
        // Remove Pandora's Box
        for (Iterator<String> s = AbstractDungeon.bossRelicPool.iterator(); s.hasNext();)
        {
          String derp = (String)s.next();
          if (derp.equals("Pandora's Box"))
          {
            s.remove();
            break;
          }
        }

        // Set us to trial mode so we don't get Neow bonuses
        Settings.isTestingNeow = true;
        Settings.isFinalActAvailable = true;
    }
}
