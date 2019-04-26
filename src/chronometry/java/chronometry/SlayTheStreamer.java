package chronometry;

import basemod.BaseMod;
import basemod.ReflectionHacks;
import basemod.interfaces.PostDungeonInitializeSubscriber;
import basemod.interfaces.PostInitializeSubscriber;
import basemod.interfaces.StartGameSubscriber;
import chronometry.patches.*;
import chronometry.patches.NoSkipBossRelicPatch;
import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import de.robojumper.ststwitch.TwitchConnection;
import de.robojumper.ststwitch.TwitchMessageListener;
import de.robojumper.ststwitch.TwitchVoteListener;
import de.robojumper.ststwitch.TwitchVoter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

    // TODO:
    //   Active monsters could have a listener that lets the user talk on screen
    //crashes if you try to restart the run

@SpireInitializer
public class SlayTheStreamer implements PostInitializeSubscriber, StartGameSubscriber, PostDungeonInitializeSubscriber {

    public static final Logger logger = LogManager.getLogger(SlayTheStreamer.class.getName());

    private static final String MOD_NAME = "Slay the Streamer";
    private static final String AUTHOR = "Chronometrics";
    private static final String DESCRIPTION = "Chat vs Streamer in the ultimate showdown! The streamer begins with a winnable deck, and chat tries to find ways to ruin it by voting and influencing the run throughout the stream. Requires Twitch Integration to work.";

    public static SpireConfig config;
    public static boolean bossHidden = false;
    public static BossSelectScreen bossSelectScreen;

    public static NoSkipBossRelicPatch noSkip;
    public static Texture startScreenImage;

    public static Map<String, Integer> usedNames = new HashMap(); // displayname, # voted
    public static Map<String, String> displayNames = new HashMap(); // username, displayname
    public static Map<String, Integer> votedTimes = new HashMap();  // displayname, voted times

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
        noSkip = new NoSkipBossRelicPatch();
        startScreenImage = ImageMaster.loadImage("versusImages/FacesOfEvil.png");

        try {
            config = new SpireConfig("SlayTheStreamer", "config");
            setDefaultPrefs();
        } catch (Exception e) {
            logger.info("Could not save config");
            logger.error(e.toString());
        }

        if (config.getBool("VoteOnBosses")) {
            this.bossHidden = true;
        }

        // Guarantee a whale
        Settings.isTestingNeow = true;

        Texture badgeTexture = ImageMaster.loadImage("versusImages/Badge.png");
        BaseMod.registerModBadge(badgeTexture, MOD_NAME, AUTHOR, DESCRIPTION, new ConfigPanel());

        // Neow Voting - needs to only happen once!
        TwitchVoter.registerListener(new TwitchVoteListener() {
            @Override
            public void onTwitchAvailable() {
                StartGamePatch.updateVote();
            }

            @Override
            public void onTwitchUnavailable() {
                StartGamePatch.updateVote();
            }
        });

        List<TwitchMessageListener> listeners = (List<TwitchMessageListener>)ReflectionHacks.getPrivate(AbstractDungeon.topPanel.twitch.get().connection, TwitchConnection.class, "listeners");

        TwitchMessageListener t = new TwitchMessageListener() {
            @Override
            public void onMessage(String msg, String user){
                MonsterMessageRepeater.parseMessage(msg, user);
            }
        };

        listeners.add(t);
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

        if (!config.has("MerchantNames"))       { config.setString("MerchantNames", "Casey,Anthony"); }
        if (!config.has("MonsterTitles"))       { config.setString("MonsterTitles", "Painbringer,Snecko's Eye,Lord of Reptiles,Whistleblower,Paragon of Chat,Baron of Slimes,Count of Encouragement,Duke of Wonder,Enslaver of Slaves,Mangler of Malaphors,Antiquated,Unsummoner,Titan,Thorny,Baffling,True Hero,Patron of Demons,Provoker of Perseveration,Bastion of Bureaucracy,Shaper of Towers,Spinner of Cloth,Duchess of the Exordium,Instiller of Dishonesty,Installer of Distilleries,Judge and Jury,Executable,Agreeable,Analytic,Attractive,Backward-Compatible,Bleeding-Edge,Boiling Mad,Brave,Bullheaded,Chic,Cold,Corrugated,Corrupt,Cost-Effective,Daydreamer,Dazzling,Delightful,Destructive,Devoted,Disheveled,Distinctive,Dreamless,Dynamically-Loading,Elastic,Ethical,Exceptional,Expansive,Fashionable,Feature-Driven,Focused,Frictionless,Frustrated,Future-Proof,Handsome,Hasty,Holier-than-thou,Holistic,Illustrious,Incorrigible,Industrious,Inept,Intermandated,Intuitive,Jealous,Levelheaded,Magnanimous,Magnetic,Misrepresented,Multidisciplinary,Muscular,Musical,Obsessive,Open-Sourced,Outlandish,Overzealous,Precognitive,Prehistoric,Princely,Professional,Quarrelsome,Rapturous,Regal,Responsible,Ridiculous,Robust,Rugged,Sleep-Deprived,Smiling,Stubborn,Synergistic,Timid,Underappreciated"); }

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
        if (config.getBool("VoteOnBosses")) {
            this.bossHidden = true;
        }
    }
}
